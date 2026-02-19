package mothman.managers;

import mothman.player.LocationComponent;
import mothman.player.Player;
import mothman.sets.ActingSet;
import mothman.sets.GameSet;
import mothman.turnactions.*;
import mothman.utils.Dice;
import mothman.utils.RulesPackage;

import java.util.ArrayList;

public class GameManager {
    // Constants
    private final int DEFAULT_ACTION_TOKENS = 1;

    // Statics
    private static GameManager _instance;
    private static RulesPackage _rules = new RulesPackage();

    // Members
    private int _currentDay;
    private GameBoard _gameBoard;

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

    public int GetCurrentDay(){return _currentDay;}

    // Setters
    public void SetRules(RulesPackage rules) {_rules = rules;}

    public void SetGameBoard(GameBoard gameBoard) {_gameBoard = gameBoard;}

    public void SetCurrentDay(int day){_currentDay = day;}

    public boolean HasMoved() { return _hasPlayerMoved; }
    public void HasMoved(boolean hasMoved) {
        _hasPlayerMoved = hasMoved;}

    /**
     * This is the primary game loop. We get the current player, let them choose their turn action
     * Execute said action, check for any end conditions and then advance to the next player
     */
    public void UpdateGame(String action, ViewportController vc)
    {
        switch (action) {
            case "quit"     -> System.exit(1);
            case "pass"     -> {}
            case "acquire"  -> new Acquire().Execute(vc);
            case "act"      -> new Act().Execute(vc);
            case "rehearse" -> new Rehearse().Execute(vc);
            case "move"     -> new Move().Execute(vc);
            case "upgrade"  -> new Upgrade().Execute(vc);
            case "force"    -> _gameBoard.Clear();
            case "end game" -> EndGame(vc);
            case "board"    -> vc.ShowBoard();
            case "profile"  -> vc.ShowMessage(PlayerManager.GetInstance().GetCurrentPlayer().toString());
            default         -> vc.ShowMessage("Invalid Choice");
        }
    }

    /**
     * Gets called if there is only one scene card remaining UpdateGame.
     * Removes SceneCards and Resets Shot Tokens
     */
    public void EndDay(ViewportController vc) //may be off by 1
    {
        vc.ShowMessage("-|-|- Ending Day " + _currentDay + " -|-|-");

        // Check if it is the end of the Game
        if (IsEndGame()) {
            EndGame(vc);
            return;
        }

        // Continue the Day loop
        _currentDay++;
        vc.ShowMessage("-|-|- Starting Day " + _currentDay + " -|-|-");

        // Reset everyone's location to the trailer and reset sets
        GameSet trailer = GetGameBoard().GetStartingSet();
        for (Player p : PlayerManager.GetInstance().GetPlayerLibrary())
        {
            LocationComponent loc = p.GetLocation();

            // remove from old set first
            GameSet oldSet = loc.GetCurrentGameSet();
            if (oldSet instanceof ActingSet) {
                ((ActingSet) oldSet).RemovePlayer(p);
            } else if (oldSet != null) {
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
    public void AdvanceTurn()
    {
        // Reset Action Tokens and action for the next player
        _actionTokens = DEFAULT_ACTION_TOKENS;
        _hasPlayerMoved = false;


        int index = 0;

        for (int i = 0; i < PlayerManager.GetInstance().GetPlayerLibrary().length; i++)
        {
            if (PlayerManager.GetInstance().GetPlayerLibrary()[i] == PlayerManager.GetInstance().GetCurrentPlayer())
            {
                index = i;
                break;
            }
        }
        PlayerManager.GetInstance().SetCurrentPlayer(PlayerManager.GetInstance().GetPlayerLibrary()[(index + 1) % PlayerManager.GetInstance().GetPlayerLibrary().length]);
    }

    /**
     * Checks the amount of scene cards in play to know how many remain
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
    public void EndGame(ViewportController vc)
    {
        _hasGameEnded = true;
        int[] scores = PlayerManager.GetInstance().TallyScore();

        // Grab the highest score
        int highest = Integer.MIN_VALUE;
        for (int score : scores) {
            if (score > highest) {
                highest = score;
            }
        }


        // Grab Winner
        ArrayList<Player> winners = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == highest) {
                winners.add(PlayerManager.GetInstance().GetPlayerLibrary()[i]);
            }
        }

        // Tiebreaker
        if (winners.size() > 1)
        {
            int[] creditScores = PlayerManager.GetInstance().TallyCredits(winners);
            // Grab the highest credit score.
            int highestCredit = Integer.MIN_VALUE;
            for (int creditScore : creditScores)
            {
                if (creditScore > highestCredit)
                {
                    highestCredit = creditScore;
                }
            }

            ArrayList<Player> tieWinners = new ArrayList<>();
            for (int i = 0; i < creditScores.length; i++)
            {
                if(creditScores[i] == highestCredit){
                    tieWinners.add(PlayerManager.GetInstance().GetPlayerLibrary()[i]);
                }
            }


            vc.ShowMessage("=== GAME OVER ===");
            for (Player winner : tieWinners)
            {
                vc.ShowMessage("Winner: Player " + winner.GetPersonalId() + " with score " + winner.GetScore());
            }
        }
        else
        {
            // Display
            vc.ShowMessage("=== GAME OVER ===");
            for (Player winner : winners)
            {
                vc.ShowMessage("Winner: Player " + winner.GetPersonalId() + " with score " + winner.GetScore());
            }
        }

    }

    /**
     *This starts the game using the given rules package as well as sets the day to one and moves players to the start.
     * Next it sets up all the players default values then populates the board and chooses a starting player.
     */
    public void StartGame()
    {
        // Populate board
        _gameBoard.Populate();
        // Let's be careful with indexing here
        SetCurrentDay(1);

        // Use die to choose a first player
        // -1 because indexing starts at 0 and dice will always give is 1-8 instead of 0-7
        Dice dice = Dice.GetInstance();
        int startingPlayer = dice.Roll(1, _rules.GetPlayerCount()) - 1;
        PlayerManager.GetInstance().SetCurrentPlayer(PlayerManager.GetInstance().GetPlayerLibrary()[startingPlayer]);

        // Start updating the game until it ends.
        _hasGameEnded = false;
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

        sb.append("Current Day: ");
        sb.append(_currentDay).append("\n");

        sb.append("Has Moved: ");
        sb.append(_hasPlayerMoved).append("\n");

        return sb.toString();
    }


}