import java.util.ArrayList;
import java.util.Scanner;

public class GameManager {
    // Constants
    private final int DEFAULT_ACTION_TOKENS = 1;

    // Statics
    private static GameManager _instance;
    private static RulesPackage _rules = new RulesPackage();

    // Members
    private Player _currentPlayer;
    private int _currentDay;
    private GameBoard _gameBoard;
    private TurnAction _playerAction = new Idle();
    private Scanner _input = new Scanner(System.in);

    private int _actionTokens = DEFAULT_ACTION_TOKENS;
    private boolean _hasPlayerMoved = false;
    private boolean _hasGameEnded = false;

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
    public void SetRules(RulesPackage rules) {_rules = rules;}

    public void SetGameBoard(GameBoard gameBoard) {_gameBoard = gameBoard;}

    public void SetCurrentPlayer(Player currentPlayer) {_currentPlayer = currentPlayer;}

    public void SetCurrentDay(int day){_currentDay = day;}

    public boolean HasMoved() { return _hasPlayerMoved; }
    public void HasMoved(boolean hasMoved) {
        _hasPlayerMoved = hasMoved;}

    /**
     * @param playerAction any player action that implements the TurnAction interface
     */
    private void SetPlayerAction(TurnAction playerAction) {
        _playerAction = playerAction;
    }


    /**
     * This is the primary game loop. We get the current player, let them choose their turn action
     * Execute said action, check for any end conditions and then advance to the next player
     */
    private void UpdateGame()
    //This processes the current player's turn
    {
        // While taking turn
        boolean takingTurn = true;
        while (takingTurn)
        {
            // Make sure we keep track of these actively
            LocationComponent loc = _currentPlayer.GetLocation();
            GameSet currentSet = loc.GetCurrentGameSet();

            // Print who's turn it is
            System.out.println("||" + _currentPlayer.GetPersonalId() + "'s Turn||");
            System.out.println("||Location : " + currentSet.GetName() + "||");
            System.out.println("Action Points Available: " + _actionTokens);

            // Print Available Options
            ArrayList<String> possibleActions = GetActionList();
            DisplayActionList(possibleActions);

            System.out.print("Choice: ");
            String playerChoice = _input.nextLine().toLowerCase().strip();

            if (!possibleActions.contains(playerChoice))
            {
                System.out.println("Invalid Choice");
                continue;
            }

            switch (playerChoice)
            {
                case "quit":
                    System.out.println("Quiting Game!");
                    System.exit(1);
                    break;
                case "pass":
                    System.out.println("Turn Ended");
                    takingTurn = false;
                    break;
                case "profile":
                    System.out.println(Player.GetProfileString(_currentPlayer));
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

            // Execute Selection and return to idle
            // System.out.println("Player Action: " + _playerAction.getClass());
            _playerAction.Execute();
            _playerAction = new Idle();
        }

        // Check if scene just wrapped
        if (_currentPlayer.GetLocation().GetCurrentGameSet() instanceof ActingSet actSet)
        {
            if (actSet.IsComplete())
            {
                //TODO: I commented the playerManager because
                // We only use it once in this entire method, here
                // We need to talk about how we work with playerManager
                PlayerManager manager = new PlayerManager();
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
        PlayerManager manager = new PlayerManager();
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
        // Reset Action Tokens and action for the next player
        _actionTokens = DEFAULT_ACTION_TOKENS;
        _playerAction = new Idle();
        _hasPlayerMoved = false;


        PlayerManager manager = new PlayerManager();
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
        _hasGameEnded = true;
        PlayerManager manager = new PlayerManager(); //WE ONLY PASS IN THE RULES IN START GAME //Otherwise we pass in nothing
        int[] scores = manager.TallyScore(); //This will be its own thing eventually REMEMBER TO CHANGE

        // Grab the highest score
        int highest = Integer.MIN_VALUE;
        for (int score : scores) {
            if (score > highest) {
                highest = score;
            }
        }

        // Grab Winner
        //TODO: As tiebreaker might I recommend name?
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
    public void StartGame(PlayerManager playerManager)
    {
        // Populate board
        _gameBoard.Populate();
        GameSet trailer = _gameBoard.GetStartingSet();

        // Let's be careful with indexing here
        SetCurrentDay(1);

        // Use die to choose a first player
        //-1 because indexing starts at 0 and dice will always give is 1-8 instead of 0-7
        Dice dice = Dice.GetInstance();
        int startingPlayer = dice.Roll(1, _rules.GetPlayerCount()) - 1;
        SetCurrentPlayer(playerManager.GetPlayerLibrary()[startingPlayer]);

        // Can Confirm Current Player is correctly chosen at random
        // System.out.println("startingPlayerIndex " + startingPlayer);
        // System.out.println("StartingCurrentPlayer: " + _currentPlayer.GetPersonalId());

        // Start updating the game until it ends.
        _hasGameEnded = false;
        int earlyBreak = 0;
        while (!_hasGameEnded)
        {
            earlyBreak++;

            UpdateGame();

            // Only allows 3 turns
            if (earlyBreak == 3){

                /// ========= EARLY EXIT HELPER =========
                System.out.println("Early Helper Has Been Hit");
                break;
                /// =====================================
            }
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
        GameSet currentSet = _currentPlayer.GetLocation().GetCurrentGameSet();
        boolean rolesAvailable = false;
        if (currentSet instanceof ActingSet){
            rolesAvailable = !((ActingSet) currentSet).GetAvailableRoles().isEmpty();
        }

        // The player is always allowed to:
        // - quit
        // - pass turn to next
        // - location ask where they are
        // - who are they
        // - board where is everyone?
        possibleActions.add("quit");
        possibleActions.add("pass");
        possibleActions.add("profile");

        if (!_currentPlayer.HasRole() && _actionTokens >= 0 && rolesAvailable){
            // Acquire
            possibleActions.add("acquire");
        }
        // The player has a role
        if (_currentPlayer.HasRole() && _actionTokens > 0) {
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


    public boolean HasGameEnded() {return _hasGameEnded;}
    public int GetActionTokens() {
        return _actionTokens;
    }

    public void SetActionTokens(int tokenCount) {
        _actionTokens = tokenCount;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("|~| Game Manager |~|\n");
        sb.append("Ruleset: ");
        sb.append(_rules.toString()).append("\n");

        sb.append("Current Player: ");
        sb.append(_currentPlayer).append("\n");

        sb.append("Current Day: ");
        sb.append(_currentDay).append("\n");

        sb.append("Has Moved: ");
        sb.append(_hasPlayerMoved).append("\n");

        return sb.toString();
    }


}