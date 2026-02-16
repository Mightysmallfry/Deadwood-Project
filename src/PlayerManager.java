import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class PlayerManager {

    // Members
    private static Player[] _playerLibrary;

    // Constructors
    public PlayerManager() {}

    public PlayerManager(RulesPackage rules, GameSet startLocation)
    {
        _playerLibrary = new Player[rules.GetPlayerCount()];
        for(int i = 0; i < rules.GetPlayerCount(); i++)
        {
            Player p = AddPlayer(rules, startLocation);
            _playerLibrary[i] = p;
        }
    }


    // Getters
    public Player[] GetPlayerLibrary() {return _playerLibrary;}

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

    private Player AddPlayer(RulesPackage rules, GameSet startingLocation)
    {
        // Get Player Name
        System.out.print("Please input your name: ");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine().strip();
        // sc.close();   // Also closes System.in, do this at the end of program
        System.out.println("Thank you!");

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
        //I need to make a way to exclude people who are just hanging out on the card!
        if(player.GetLocation().GetOnCard())
        {
            if(success) //if the roll is a success
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

        //grabling dificulty
        if (!(currentSet instanceof ActingSet act)) {
            return; // no bonus pay here
        }

        int difficulty = act.GetCurrentSceneCard().GetDifficulty();

        //rolling dice
        Dice dice = Dice.GetInstance();
        ArrayList<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < difficulty; i++) {
            rolls.add(dice.Roll(1, 6)); // 1d6
        }
        rolls.sort(Comparator.reverseOrder());// descending

        // Sort Players
        for(Player player : players){
            if(currentSet.equals(player.GetLocation().GetCurrentGameSet())
                    && player.GetLocation().GetOnCard()
                    && player.GetLocation().GetCurrentRole() != null)  //if it's the correct scene and on card add to a list, double check logic!!!
            {
                onCard.add(player);
            }
            else if(currentSet.equals(player.GetLocation().GetCurrentGameSet()) && !player.GetLocation().GetOnCard())
            {
                offCard.add(player);
            }
        }

        //onCard cant be empty if this is called but just in case
        if (onCard.isEmpty()) {
            return; // no one to pay
        }
        onCard.sort((p1, p2) -> Integer.compare(p2.GetLocation().GetCurrentRole().GetRank(), p1.GetLocation().GetCurrentRole().GetRank()));

        int playerCount = onCard.size();

        for (int i = 0; i < rolls.size(); i++) {
            Player p = onCard.get(i % playerCount);
            int payout = rolls.get(i);
            p.GetCurrency().IncreaseCoins(payout);
        }

        //for the elements in off card pay based on the rank
        for (Player player : offCard)
        {
            ActingRole role = player.GetLocation().GetCurrentRole();
            if (role != null) {
                player.GetCurrency().IncreaseCoins(role.GetRank());
            }
        }

        //takes off all player rolls on the scene
        for (Player player : offCard)
        {
            player.GetLocation().SetCurrentRole(null);
        }
        for (Player player : onCard)
        {
            player.GetLocation().SetCurrentRole(null);
        }

    }


    /**
     * Runs through the list of players and Tallys their Score
     * @return int[] of player scores
     */
    public int[] TallyScore()
    //as the last day finishes this tallies the score and displays it before the Deadwood ends.
    {
        Player[] players = GetPlayerLibrary();
        if (players == null || players.length == 0) {
            return new int[0];
        }
        int[] scores = new int[players.length];

        //for each player calculate their score then retrieve it.
        for (int i = 0; i < players.length; i++)
        {
            Player p = players[i];
            p.SetScore(); //This recalculates the players score.
            scores[i] = p.GetScore();
        }
        return scores;
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