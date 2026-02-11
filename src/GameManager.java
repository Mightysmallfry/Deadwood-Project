import java.util.ArrayList;

public class GameManager {


    // Members
    private static GameManager _instance;
    private Player _currentPlayer;
    private int _currentDay;
    private GameBoard _gameBoard;
    private TurnAction _playerAction = new Upgrade();
    private RulesPackage _rules;



    // Constructors
    private GameManager() {}

    public static GameManager GetInstance() {
        if (_instance == null)
        {
            _instance = new GameManager();
        }
        return _instance;
    }

    // Methods

    // Getters
    public RulesPackage Get_Rules(){return _rules;}

    public GameBoard Get_GameBoard() {return _gameBoard;}

    public Player Get_CurrentPlayer() {return _currentPlayer;}

    public int Get_CurrentDay(){return _currentDay;}

    // Setters

    public void Set_Rules(RulesPackage rules){this._rules = rules;}

    public void Set_GameBoard(GameBoard _gameBoard) {this._gameBoard = _gameBoard;}

    public void Set_CurrentPlayer(Player _currentPlayer) {this._currentPlayer = _currentPlayer;}

    public void Set_CurrentDay(int day){this._currentDay = day;}

    /**
     * @param playerAction any player action that implements the TurnAction interface
     */
    private void SetPlayerAction(TurnAction playerAction) {
        _playerAction = playerAction;
    }


    /**
     * This makes sure that the player is rewarded after their turn and calls the
     * function that deals with ending the day/game.
     */
    private void UpdateGame()
    //This plays after the player is done with a turn, rewarding the player and checking if the day/game is over.
    {
        PlayerManager manager = PlayerManager.GetInstance();
        Player current = Get_CurrentPlayer();
        LocationComponent loc = current.Get_Location();
        // Check if scene just wrapped
        if (loc.Get_CurrentGameSet() instanceof ActingSet actSet)
        {
            if (actSet.IsComplete())
            {
                manager.BonusPay(Get_CurrentPlayer());
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
     * Gets called if there is only one scene card remaining UpdateGame.
     * Removes SceneCards and Resets Shot Tokens
     */
    private void EndDay() //may be off by 1
    {
        PlayerManager manager = PlayerManager.GetInstance();
        Set_CurrentDay(Get_CurrentDay() + 1);

        if (IsEndGame()) {
            EndGame();
            return;
        }

        GameSet trailer = Get_GameBoard().Get_StartingSet();
        for (Player p : manager.Get_PlayerLibrary())
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
     * This Function is a Helper for UpdateGame that moves focus to the next player
     * after the game is done updating.
     */
    private void AdvanceTurn()
    {
        PlayerManager manager = PlayerManager.GetInstance();
        int index = 0;

        for (int i = 0; i < manager.Get_PlayerLibrary().length; i++)
        {
            if (manager.Get_PlayerLibrary()[i] == _currentPlayer)
            {
                index = i;
                break;
            }
        }
        _currentPlayer = manager.Get_PlayerLibrary()[(index + 1) % manager.Get_PlayerLibrary().length];
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
    public boolean IsEndGame() {return Get_Rules().GetDays() == Get_CurrentDay();}


    /**
     * This function ends the game (:  It grabs the score of each player and gets the winners, next it displays the
     * winners to the players.
     */
    public void EndGame()
    {
        PlayerManager manager = PlayerManager.GetInstance();
        int[] scores = manager.TallyScore();

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
                winners.add(manager.Get_PlayerLibrary()[i]);
            }
        }

        // Display
        System.out.println("=== GAME OVER ===");
        for (Player winner : winners)
        {
            System.out.println("Winner: Player " + winner.Get_PersonalId() + " with score " + winner.Get_Score());
        }
    }

    /**
     *This starts the game using the given rules package as well as sets the day to one and moves players to the start.
     * Next it sets up all the players default values then populates the board and chooses a starting plyer.
     */
    public void StartGame()
    {
        PlayerManager manager = PlayerManager.GetInstance();
        UpdateRules();

        Set_CurrentDay(1);

        // Move all players to Trailer
        GameSet trailer = _gameBoard.Get_StartingSet();

        for (Player p : manager.Get_PlayerLibrary())
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
        _currentPlayer = manager.Get_PlayerLibrary()[0]; //Could make this a dice roll.
    }

    private void UpdateRules()
    //This is used after Deadwood asks for players to change the rules depending on the player number.
    {
        PlayerManager manager = PlayerManager.GetInstance();
        RulesPackage rules = new RulesPackage(manager.Get_PlayerLibrary());

        // store for later
        _rules = rules;

        // apply to players
        for (Player p : manager.Get_PlayerLibrary()) {
            p.Set_CurrentRank(rules.GetStartingRank());
            p.Get_Currency().IncreaseCredits(rules.GetStartingCredits());
        }
    }


}