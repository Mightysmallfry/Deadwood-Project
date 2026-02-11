import java.util.ArrayList;
import java.util.Scanner;

public class GameManager {
    // Constants
    private final int DEFAULT_ACTION_TOKENS = 1;

    // Statics
    private static GameManager _instance;
    private static RulesPackage _rules;

    // Members
    private Player _currentPlayer;
    private int _currentDay;
    private GameBoard _gameBoard;
    private TurnAction _playerAction = new Upgrade();
    private Scanner _input = new Scanner(System.in);

    private int _actionTokens = DEFAULT_ACTION_TOKENS;
    private boolean _hasMoved = false;

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
    public RulesPackage GetRules(){return _rules;}

    public GameBoard GetGameBoard() {return _gameBoard;}

    public Player GetCurrentPlayer() {return _currentPlayer;}

    public int GetCurrentDay(){return _currentDay;}

    // Setters
    public static void SetRules(RulesPackage rules) {_rules = rules;}

    public void SetGameBoard(GameBoard gameBoard) {_gameBoard = gameBoard;}

    public void SetCurrentPlayer(Player currentPlayer) {_currentPlayer = currentPlayer;}

    public void SetCurrentDay(int day){_currentDay = day;}

    public boolean HasMoved() { return _hasMoved; }
    public void HasMoved(boolean hasMoved) {_hasMoved = hasMoved;}

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
    //This processes the current player's turn
    {
        PlayerManager manager = PlayerManager.GetInstance();
        Player current = GetCurrentPlayer();
        LocationComponent loc = current.GetLocation();

        // While taking turn
        boolean takingTurn = true;
        while (takingTurn)
        {
            ArrayList<String> possibleActions = GetActionList();
            DisplayActionList(possibleActions);

            System.out.print("Choice: ");
            String playerChoice = _input.nextLine().toLowerCase().strip();

            if (!possibleActions.contains(playerChoice))
            {
                System.out.println("Invalid Choice");
                continue;
            }

            // TODO: How do we want to quit?
            switch (playerChoice)
            {
                case "quit":
                    System.exit(1);
                    break;
                case "pass":
                    takingTurn = false;
                    break;
                case "acquire":
                    SetPlayerAction(new Acquire());
                    break;
                case "act":
                    SetPlayerAction(new Act());
                    break;
                case "rehearse":
                    SetPlayerAction(new Rehearse());
                    break;
                case "move":
                    SetPlayerAction(new Move());
                    break;
                case "upgrade":
                    SetPlayerAction(new Upgrade());
                    break;
            }
            _playerAction.Execute();
        }

        // Check if scene just wrapped
        if (loc.GetCurrentGameSet() instanceof ActingSet actSet)
        {
            if (actSet.IsComplete())
            {
                manager.BonusPay(GetCurrentPlayer());
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
        SetCurrentDay(GetCurrentDay() + 1);

        if (IsEndGame()) {
            EndGame();
            return;
        }

        GameSet trailer = GetGameBoard().GetStartingSet();
        for (Player p : manager.GetPlayerLibrary())
        {
            LocationComponent loc = p.GetLocation();

            // remove from old set
            GameSet oldSet = loc.GetCurrentGameSet();
            if (oldSet != null) {
                oldSet.RemovePlayer(p);
            }

            // reset player state
            loc.SetCurrentRole(null);
            loc.SetOnCard(false);
            loc.SetRehearseTokens(0);

            // move to trailer
            loc.SetCurrentGameSet(trailer);
            trailer.AddPlayer(p);
        }

        GetGameBoard().Clear();
        GetGameBoard().Populate();
    }


    /**
     * This Function is a Helper for UpdateGame that moves focus to the next player
     * after the game is done updating.
     */
    private void AdvanceTurn()
    {
        // Reset Action Tokens for the next player
        _actionTokens = DEFAULT_ACTION_TOKENS;

        PlayerManager manager = PlayerManager.GetInstance();
        int index = 0;

        for (int i = 0; i < manager.GetPlayerLibrary().length; i++)
        {
            if (manager.GetPlayerLibrary()[i] == _currentPlayer)
            {
                index = i;
                break;
            }
        }
        _currentPlayer = manager.GetPlayerLibrary()[(index + 1) % manager.GetPlayerLibrary().length];
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
                if (act.GetCurrentSceneCard() != null) {
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
    public boolean IsEndGame() {return GetRules().GetDays() == GetCurrentDay();}


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
                winners.add(manager.GetPlayerLibrary()[i]);
            }
        }

        // Display
        System.out.println("=== GAME OVER ===");
        for (Player winner : winners)
        {
            System.out.println("Winner: Player " + winner.GetPersonalId() + " with score " + winner.GetScore());
        }
    }

    /**
     *This starts the game using the given rules package as well as sets the day to one and moves players to the start.
     * Next it sets up all the players default values then populates the board and chooses a starting player.
     */
    public void StartGame()
    {
        PlayerManager manager = PlayerManager.GetInstance();
        UpdateRules();

        SetCurrentDay(1);

        // Move all players to Trailer
        GameSet trailer = _gameBoard.GetStartingSet();

        for (Player p : manager.GetPlayerLibrary())
        {
            LocationComponent loc = p.GetLocation();

            loc.SetCurrentRole(null);
            loc.SetOnCard(false);
            loc.SetRehearseTokens(0);

            loc.SetCurrentGameSet(trailer);
            trailer.AddPlayer(p);
        }

        // Populate board with scene cards
        _gameBoard.Populate();

        // Choose first player
        _currentPlayer = manager.GetPlayerLibrary()[0]; //Could make this a dice roll.
    }

    private void UpdateRules()
    //This is used after Deadwood asks for players to change the rules depending on the player number.
    {
        PlayerManager manager = PlayerManager.GetInstance();
        RulesPackage rules = new RulesPackage(manager.GetPlayerLibrary());

        // store for later
        _rules = rules;

        // apply to players
        for (Player p : manager.GetPlayerLibrary()) {
            p.SetCurrentRank(rules.GetStartingRank());
            p.GetCurrency().IncreaseCredits(rules.GetStartingCredits());
        }
    }

    /**
     * Checks what actions are available to the player and returns
     * them as a list of strings
     * @return
     */
    private ArrayList<String> GetActionList()
    {
        ArrayList<String> possibleActions = new ArrayList<>();

        // The player is always allowed to:
        // - quit
        // - pass turn to next
        possibleActions.add("quit");
        possibleActions.add("pass");

        // The player has a role
        if (_currentPlayer.HasRole() && _actionTokens > 0) {
            // Acquire
            possibleActions.add("acquire");
            // Act
            possibleActions.add("act");
            // Rehearse
            possibleActions.add("rehearse");

        } else {
            if (_actionTokens > 0) {
                // Move
                possibleActions.add("move");
            }
            // Upgrade
            if (_currentPlayer.GetLocation().GetCurrentGameSet() instanceof CastingSet) {
                possibleActions.add("upgrade");
            }
        }

        return possibleActions;
    }

    private void DisplayActionList(ArrayList<String> actionList)
    {
        System.out.print("Available actions : ");
        for (int i = 0; i < actionList.size(); i++) {
            System.out.print("[" + actionList.get(i) + "]");

            if (i != actionList.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }


    public int GetActionTokens() {
        return _actionTokens;
    }

    public void SetActionTokens(int tokenCount) {
        _actionTokens = tokenCount;
    }



}