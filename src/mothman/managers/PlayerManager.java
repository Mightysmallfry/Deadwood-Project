package mothman.managers;

import mothman.player.LocationComponent;
import mothman.player.Player;
import mothman.sets.ActingRole;
import mothman.sets.ActingSet;
import mothman.sets.GameSet;
import mothman.utils.Dice;
import mothman.utils.RulesPackage;
import mothman.viewports.Viewport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;


public class PlayerManager {

    // Members
    private static Player[] _playerLibrary;
    private static PlayerManager _instance;
    private Player _currentPlayer;

    // Constructors
    private PlayerManager() {}

    private PlayerManager(RulesPackage rules, GameSet startLocation, ViewportController vc)
    {
        _playerLibrary = new Player[rules.GetPlayerCount()];
        for(int i = 0; i < rules.GetPlayerCount(); i++)
        {
            Player p = AddPlayer(rules, startLocation, vc);
            _playerLibrary[i] = p;
        }
    }

    /**
     * Only Safe to call this if you have already called the manager once before
     * with starting rules, start location and a viewport controller
     * @return
     */
    public static PlayerManager GetInstance(){
        if (_instance == null){
            _instance = new PlayerManager();
        }
        return _instance;
    }

    public static PlayerManager GetInstance(RulesPackage rules, GameSet startLocation, ViewportController vc){
        if (_instance == null){
            _instance = new PlayerManager(rules, startLocation, vc);
        }
        return _instance;
    }

    // Getters
    public Player[] GetPlayerLibrary() {return _playerLibrary;}

    public Player GetCurrentPlayer() {return _currentPlayer;}

    public void SetCurrentPlayer(Player currentPlayer) {_currentPlayer = currentPlayer;}

    public int GetPlayerCount() {
        return _playerLibrary == null ? 0 : _playerLibrary.length;
    }

    // Setters
    public void SetPlayerLibrary(Player[] playerLibrary) {_playerLibrary = playerLibrary;}


    // Methods

    /**
     * This is called at the start of the game and creates a player based on the rules.
     * @param rules
     * @param startingLocation
     * @return
     */

    private Player AddPlayer(RulesPackage rules, GameSet startingLocation, ViewportController vc)
    {
        // Get Player Name
        String name = vc.AskName();

        // Create new player object
        Player player = new Player(name, rules.GetStartingRank());
        player.GetCurrency().SetCredits(rules.GetStartingCredits());

        // Set Starting Location
        LocationComponent playerLocation = player.GetLocation();
        playerLocation.SetCurrentGameSet(startingLocation);

        // Register the player onto the starting board
        startingLocation.AddPlayer(player);
        return player;
    }



    /**
     * This looks at if the roll was a success and pays the players
     */
    public void BasicPay(Player player, boolean success)
    {
        if(player.GetLocation().GetOnCard())
        {
            if(success)
            {
                player.GetCurrency().IncreaseCoins(2);
            }
        }
        else
        {
            if(success)
            {
                player.GetCurrency().IncreaseCredits(1);
                player.GetCurrency().IncreaseCoins(1);
            }
            else
            {
                player.GetCurrency().IncreaseCoins(1);
            }
        }
    }

    /**
     * This gets the difficulty of the scene then creates a list of d6 rolls for assigning on card money.
     * Next it gets all the players on the scene and sorts them into on and off card.  It then it bounces between on
     * card members assigning them the previously rolled money dice starting with the player on the highest rank
     * and descending in order.  Next it pays off card members based on rank.
     */
    public void BonusPay(Player currentPlayer)
    {
        Player[] players = GetPlayerLibrary();
        if (players == null || players.length == 0) return;
        ArrayList<Player> onCard = new ArrayList<>();
        ArrayList<Player> offCard = new ArrayList<>();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        // Check if it is called on an acting set
        if (!(currentSet instanceof ActingSet act)) {
            return;
        }

        int difficulty = act.GetCurrentSceneCard().GetDifficulty();

        // roll for bonuses
        Dice dice = Dice.GetInstance();
        ArrayList<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < difficulty; i++) {
            // roll 1d6 * difficulty
            rolls.add(dice.Roll(1, 6));
        }
        // Sort in descending order
        rolls.sort(Comparator.reverseOrder());

        // Sort Players
        for(Player player : players){
            if(currentSet.equals(player.GetLocation().GetCurrentGameSet())
                    && player.GetLocation().GetOnCard()
                    && player.GetLocation().GetCurrentRole() != null)
            {
                onCard.add(player);
            }
            else if(currentSet.equals(player.GetLocation().GetCurrentGameSet()) && !player.GetLocation().GetOnCard())
            {
                offCard.add(player);
            }
        }

        // Check if we need to pay left over
        if (onCard.isEmpty()) {
            return;
        }
        onCard.sort((p1, p2) -> Integer.compare(p2.GetLocation().GetCurrentRole().GetRank(), p1.GetLocation().GetCurrentRole().GetRank()));

        int playerCount = onCard.size();

        for (int i = 0; i < rolls.size(); i++) {
            Player p = onCard.get(i % playerCount);
            int payout = rolls.get(i);
            p.GetCurrency().IncreaseCoins(payout);
        }

        // For people off of the card, pay them their rank
        for (Player player : offCard)
        {
            ActingRole role = player.GetLocation().GetCurrentRole();
            if (role != null) {
                player.GetCurrency().IncreaseCoins(role.GetRank());
            }
        }
    }

    public void PostSceneReset(Player currentPlayer)
    {
        for (Player player : _playerLibrary){
            if(player.GetLocation().GetCurrentGameSet() == currentPlayer.GetLocation().GetCurrentGameSet()){
                player.GetLocation().SetCurrentRole(null);
                player.GetLocation().SetRehearseTokens(0);
            }
        }
    }


    /**
     * Runs through the list of players and tallies their Score
     * @return int[] of player scores
     */
    public int[] TallyScore()
    {
        Player[] players = GetPlayerLibrary();
        if (players == null || players.length == 0) {
            return new int[0];
        }
        int[] scores = new int[players.length];

        // Calculate and Retrieve player scores
        for (int i = 0; i < players.length; i++)
        {
            Player p = players[i];
            p.SetScore();
            scores[i] = p.GetScore();
        }
        return scores;
    }

    public int[] TallyCredits(ArrayList<Player> players)
    {
        if (players == null || players.isEmpty())
        {
            return new int[0];
        }
        int[] creditScore = new int[players.size()];
        for (int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            creditScore[i] = p.GetCurrency().GetCredits();
        }
        return creditScore;
    }

    // TODO: Potentially change this to a viewportController call
    public static String LocatePlayers(){
        StringBuilder sb = new StringBuilder();

        for (Player player : _playerLibrary)
        {
            sb.append("- PLR: ").append(player.GetPersonalId());
            sb.append(" - LOC: ").append(player.GetLocation().GetCurrentGameSet().GetName()).append("\n");
        }

        return sb.toString();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("PlayerManager {\n");

        sb.append("  Player Count: ");
        sb.append(_playerLibrary != null ? _playerLibrary.length : 0);
        sb.append("\n");

        sb.append("  Players:\n");

        if (_playerLibrary != null) {
            for (int i = 0; i < _playerLibrary.length; i++) {
                sb.append("    [")
                        .append(i)
                        .append("] ")
                        .append(_playerLibrary[i] != null ? _playerLibrary[i].toString() : "null")
                        .append("\n");
            }
        } else {
            sb.append("    null\n");
        }

        sb.append("}");
        return sb.toString();
    }
}