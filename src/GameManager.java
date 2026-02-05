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

    public GameManager(GameBoard gameBoard, Player[] players, Player currentPlayer, RulesPackage rules)
    {
        this._gameBoard = gameBoard;
        this._playerLibrary = players;
        this._currentPlayer = currentPlayer;
        this._rules = rules;
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
        RulesPackage rules = new RulesPackage(_playerLibrary);

        // store for later
        _rules = rules;

        // apply to players
        for (Player p : _playerLibrary) {
            p.Set_CurrentRank(rules.GetStartingRank());
            p.Get_Currency().IncreaseCredits(rules.GetStartingCredits());
        }
    }

    /**
     * This makes sure that the player is rewarded after their turn and calls the
     * function that deals with ending the day/game.
     */
    private void UpdateGame()
    //This plays after the player is done with a turn, rewarding the player and checking if the day/game is over.
    {
        Player current = Get_CurrentPlayer();
        LocationComponent loc = current.Get_Location();

        // Check if scene just wrapped
        if (loc.Get_CurrentGameSet() instanceof ActingSet actSet)
        {
            if (actSet.IsComplete())
            {
                BonusPay();
                actSet.RemoveCard();
            }
        }

        // Check if day is over
        if (IsEndDay())
        {
            EndDay();
            return;
        }
        // Move to next player
        AdvanceTurn();
    }

    /**
     * This Function is a Helper for UpdateGame that moves focus to the next player
     * after the game is done updating.
     */
    private void AdvanceTurn()
    {
        int index = 0;

        for (int i = 0; i < _playerLibrary.length; i++)
        {
            if (_playerLibrary[i] == _currentPlayer)
            {
                index = i;
                break;
            }
        }

        _currentPlayer = _playerLibrary[(index + 1) % _playerLibrary.length];
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

    /**
     * This gets the difficulty of the scene then creates a list of d6 rolls for assigning on card money.
     * Next it gets all the players on the scene and sorts them into on and off card.  It then it bounces between on
     * card members assigning them the previously rolled money dice starting with the player on the highest rank
     * and descending in order.  Next it pays off card members based on rank.
     */
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

        int difficulty = act.Get_CurrentSceneCard().GetDifficulty();

        //rolling dice
        Dice dice = Dice.GetInstance();
        ArrayList<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < difficulty; i++) {
            rolls.add(dice.Roll(1, 6)); // 1d6
        }
        rolls.sort((a, b) -> b - a); // descending

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

    /**
     *This starts the game using the given rules package as well as sets the day to one and moves players to the start.
     * Next it sets up all the players default values then populates the board and chooses a starting plyer.
     */
    public void StartGame()
    {
        UpdateRules();

        Set_CurrentDay(1);

        // Move all players to Trailer
        GameSet trailer = _gameBoard.Get_StartingSet();

        for (Player p : _playerLibrary)
        {
            LocationComponent loc = p.Get_Location();

            loc.Set_CurrentRole(null);
            loc.Set_OnCard(false);
            loc.Set_RehearseTokens(0);

            loc.Set_CurrentGameSet(trailer);
            trailer.AddPlayer(p);
        }

        // Populate board with scene cards
        _gameBoard.Populate();

        // Choose first player
        _currentPlayer = _playerLibrary[0]; //Could make this a dice roll.
    }

    /**
     * This function ends the game (:  It grabs the score of each player and gets the winners, next it displays the
     * winners to the players.
     */
    public void EndGame()
    {
        int[] scores = TallyScore(_playerLibrary);

        // Grab the highest score
        int highest = Integer.MIN_VALUE;
        for (int score : scores) {
            if (score > highest) {
                highest = score;
            }
        }

        // Grab Winner
        ArrayList<Player> winners = new ArrayList<>();//Could add a tiebreaker piece.
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == highest) {
                winners.add(_playerLibrary[i]);
            }
        }
        
        // Display
        System.out.println("=== GAME OVER ===");
        for (Player winner : winners)
        {
            System.out.println("Winner: Player " + winner.Get_PersonalId() + " with score " + winner.Get_Score());
        }
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
