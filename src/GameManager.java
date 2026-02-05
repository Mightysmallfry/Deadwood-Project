import java.util.ArrayList;

public class GameManager {

    // Members
    private GameBoard _gameBoard;
    private Player[] _playerLibrary;
    private Player _currentPlayer;
    private int _currentDay;
    private RulesPackage _rules;

    // Constructors
    public GameManager(){

    }

    public GameManager(GameBoard gameBoard)
    {
        this._gameBoard = gameBoard;
    }

    public GameManager(GameBoard gameBoard, Player[] players)
    {
        this._gameBoard = gameBoard;
        this._playerLibrary = players;
    }

    public GameManager(GameBoard gameBoard, Player[] players, Player currentPlayer)
    {
        this._gameBoard = gameBoard;
        this._playerLibrary = players;
        this._currentPlayer = currentPlayer;
    }

    // Getters

    public Player Get_CurrentPlayer() {return _currentPlayer;}

    public GameBoard Get_GameBoard() {return _gameBoard;}

    public Player[] Get_PlayerLibrary() {return _playerLibrary;}

    public int Get_CurrentDay(){return _currentDay;}

    public RulesPackage Get_Rules(){return _rules;}

    public int Get_PlayerCount() {
        return _playerLibrary == null ? 0 : _playerLibrary.length;
    }

    // Setters

    public void Set_CurrentPlayer(Player _currentPlayer) {this._currentPlayer = _currentPlayer;}

    public void Set_GameBoard(GameBoard _gameBoard) {this._gameBoard = _gameBoard;}

    public void Set_PlayerLibrary(Player[] _playerLibrary) {this._playerLibrary = _playerLibrary;}

    public void Set_CurrentDay(int day){this._currentDay = day;}

    public void Set_Rules(RulesPackage rules){this._rules = rules;}

    // Methods
    private void UpdateRules()
    //This is used after Deadwood asks for players to change the rules depending on the player number.
    {
    //I dont think we need this
    }

    /**
     * This makes sure that the player is rewarded after their turn and that the day is not over.
     */
    private void UpdateGame()
    //This plays after the player is done with a turn, rewarding the player and checking if the day/game is over.
    {

    }

    /**
     * Gets called if there is only one scene card remaining UpdateGame.
     * Removes SceneCards and Resets Shot Tokens
     */
    private void EndDay()
    {
        Set_CurrentDay(Get_CurrentDay() + 1);

        if (IsEndGame()) {
            EndGame();
            return;
        }

        GameSet trailer = Get_GameBoard().Get_StartingSet();
        for (Player p : _playerLibrary)
        {
            LocationComponent loc = p.Get_Location();

            // remove from old set
            GameSet oldSet = loc.Get_CurrentGameSet();
            if (oldSet != null) {
                oldSet.RemovePlayer(p);
            }

            // reset player state
            loc.Set_CurrentRole(null);
            loc.Set_OnCard(false);
            loc.Set_RehearseTokens(0);

            // move to trailer
            loc.Set_CurrentGameSet(trailer);
            trailer.AddPlayer(p);
        }

        Get_GameBoard().Clear();
        Get_GameBoard().Populate();
    }


    /**
     * Checks the ammount of scene cards in play to know how many remain
     * If there is one left returns True
     * @return boolean
     */
    public boolean IsEndDay()
    {
        int activeScenes = 0;

        for (GameSet set : _gameBoard.GetAllGameSets()) {
            if (set instanceof ActingSet act) {
                if (act.Get_CurrentSceneCard() != null) {
                    activeScenes++;
                }
            }
        }
        return activeScenes <= 1;
    }

    /**
     * compares day limit to current day, if they  are the same return true
     * @return boolean
     */
    public boolean IsEndGame()
    {
        return Get_Rules().GetDays() == Get_CurrentDay();
    }

    /**
     * This looks at if the roll was a success and pays the players
     * My problem is where is this called?
     * If its in update then this WILL NOT WORK.
     */
    public void BasicPay(boolean success)
    {
        //I need to make a way to exclude people who are just hanging out on the card!
        Player player = Get_CurrentPlayer();
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

    public void BonusPay()
    {
        Player[] players = Get_PlayerLibrary();
        Player currentPlayer = Get_CurrentPlayer();
        ArrayList<Player> onCard = new ArrayList<>();
        ArrayList<Player> offCard = new ArrayList<>();
        GameSet currentSet = currentPlayer.Get_Location().Get_CurrentGameSet();

        //grabling dificulty
        if (!(currentSet instanceof ActingSet act)) {
            return; // no bonus pay here
        }

        int dificulty = act.Get_CurrentSceneCard().GetDifficulty();

        //rolling dice
        Dice dice = Dice.GetInstance();
        ArrayList<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < dificulty; i++) {
            rolls.add(dice.Roll(1, 6)); // 1d6
        }
        rolls.sort((a, b) -> b - a); // descending

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
     * @param players
     * @return int[] of player scores
     */
    public int[] TallyScore(Player[] players)
    //as the last day finishes this tallies the score and displays it before the Deadwood ends.
    {
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

    public void StartGame()
    {
        RulesPackage rules = new RulesPackage(_playerLibrary);

        for (Player p : _playerLibrary)
        {
            p.Set_CurrentRank(rules.GetStartingRank());
            p.Get_Currency().IncreaseCredits(rules.GetStartingCredits());
        }
        Set_CurrentDay(1);
    }

    public void EndGame()
    {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("GameManager {\n");

        sb.append("  GameBoard: ");
        sb.append(_gameBoard != null ? _gameBoard.toString() : "null");
        sb.append("\n");

        sb.append("  Current Player: ");
        sb.append(_currentPlayer != null ? _currentPlayer.toString() : "null");
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
