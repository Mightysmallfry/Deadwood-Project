import java.util.ArrayList;
import java.util.Comparator;

public class PlayerManager {

    // Members
    private static PlayerManager _instance;
    private GameBoard _gameBoard;
    private Player[] _playerLibrary;

    // Constructors
    private PlayerManager() {}

    public static PlayerManager GetInstance() { //How does this work with setting the players and all that
        if (_instance == null)
        {
            _instance = new PlayerManager();
        }
        return _instance;
    }

    // Getters
    public GameBoard Get_GameBoard() {return _gameBoard;}

    public Player[] Get_PlayerLibrary() {return _playerLibrary;}

    public int Get_PlayerCount() {
        return _playerLibrary == null ? 0 : _playerLibrary.length;
    }

    // Setters
    public void Set_GameBoard(GameBoard _gameBoard) {this._gameBoard = _gameBoard;}

    public void Set_PlayerLibrary(Player[] _playerLibrary) {this._playerLibrary = _playerLibrary;}


    // Methods
    /**
     * This looks at if the roll was a success and pays the players
     * My problem is where is this called?
     * If its in update then this WILL NOT WORK.
     */
    public void BasicPay(Player player, boolean success)
    {
        //I need to make a way to exclude people who are just hanging out on the card!
        if(player.Get_Location().Get_OnCard())
        {
            if(success) //if the roll is a success
            {
                player.Get_Currency().IncreaseCoins(2);
            }
        }
        else
        {
            if(success)
            {
                player.Get_Currency().IncreaseCredits(1);
                player.Get_Currency().IncreaseCoins(1);
            }
            else
            {
                player.Get_Currency().IncreaseCoins(1);
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
        Player[] players = Get_PlayerLibrary();
        if (players == null || players.length == 0) return;
        ArrayList<Player> onCard = new ArrayList<>();
        ArrayList<Player> offCard = new ArrayList<>();
        GameSet currentSet = currentPlayer.Get_Location().Get_CurrentGameSet();

        //grabling dificulty
        if (!(currentSet instanceof ActingSet act)) {
            return; // no bonus pay here
        }

        int difficulty = act.Get_CurrentSceneCard().GetDifficulty();

        //rolling dice
        Dice dice = Dice.GetInstance();
        ArrayList<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < difficulty; i++) {
            rolls.add(dice.Roll(1, 6)); // 1d6
        }
        rolls.sort(Comparator.reverseOrder());// descending

        // Sort Players
        for(Player player : players){
            if(currentSet.equals(player.Get_Location().Get_CurrentGameSet())
                    && player.Get_Location().Get_OnCard()
                    && player.Get_Location().Get_CurrentRole() != null)  //if it's the correct scene and on card add to a list, double check logic!!!
            {
                onCard.add(player);
            }
            else if(currentSet.equals(player.Get_Location().Get_CurrentGameSet()) && !player.Get_Location().Get_OnCard())
            {
                offCard.add(player);
            }
        }

        //onCard cant be empty if this is called but just in case
        if (onCard.isEmpty()) {
            return; // no one to pay
        }
        onCard.sort((p1, p2) -> Integer.compare(p2.Get_Location().Get_CurrentRole().GetRank(), p1.Get_Location().Get_CurrentRole().GetRank()));

        int playerCount = onCard.size();

        for (int i = 0; i < rolls.size(); i++) {
            Player p = onCard.get(i % playerCount);
            int payout = rolls.get(i);
            p.Get_Currency().IncreaseCoins(payout);
        }

        //for the elements in off card pay based on the rank
        for (Player player : offCard)
        {
            ActingRole role = player.Get_Location().Get_CurrentRole();
            if (role != null) {
                player.Get_Currency().IncreaseCoins(role.GetRank());
            }
        }

    }

    /**
     * Runs through the list of players and Tallys their Score
     * @return int[] of player scores
     */
    public int[] TallyScore()
    //as the last day finishes this tallies the score and displays it before the Deadwood ends.
    {
        Player[] players = Get_PlayerLibrary();
        if (players == null || players.length == 0)
            {
                return new int[0];
            }
        int[] scores = new int[players.length];

        //for each player calculate their score then retrieve it.
        for (int i = 0; i < players.length; i++)
        {
            Player p = players[i];
            p.Set_Score(); //This recalculates the players score.
            scores[i] = p.Get_Score();
        }
        return scores;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("PlayerManager {\n");

        sb.append("  GameBoard: ");
        sb.append(_gameBoard != null ? _gameBoard.toString() : "null");
        sb.append("\n");

        sb.append("  Current Player: ");
        sb.append("\n");

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