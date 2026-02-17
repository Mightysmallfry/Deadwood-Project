package mothman;

import mothman.managers.GameBoard;
import mothman.managers.GameManager;
import mothman.managers.PlayerManager;
import mothman.parsers.GameSetParser;
import mothman.parsers.SceneCardParser;
import mothman.sets.ActingSet;
import mothman.sets.CastingSet;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.utils.RulesPackage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Deadwood {
    private static final int MINIMUM_PLAYER_COUNT = 2;
    private static final int MAXIMUM_PLAYER_COUNT = 8;
    private static final int DEFAULT_PLAYER_COUNT = 4;


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Deadwood [PlayerCount]");
            System.exit(0);
        }
        int playerCount = Integer.parseInt(args[0]);
        // ========= Guard Against Too Few or Many Players =========
        if (playerCount < MINIMUM_PLAYER_COUNT){
            System.out.println("Sorry it seems you have too few players to enjoy the game!");
            System.out.println("Please try again with at least: " + MINIMUM_PLAYER_COUNT + " Players");
            System.out.println("Normally, the game is played with: " + DEFAULT_PLAYER_COUNT + " Players");
            System.exit(1);
        } else if (playerCount > MAXIMUM_PLAYER_COUNT) {
            System.out.println("Sorry it seems you have too many players to enjoy the game!");
            System.out.println("Please try again with at most: " + MAXIMUM_PLAYER_COUNT + " Players");
            System.out.println("Normally, the game is played with: " + DEFAULT_PLAYER_COUNT + " Players");
            System.exit(1);
        }



        System.out.println("Hello and welcome to Deadwood gunslinger!");
        System.out.printf("You have %d players!\n", playerCount);

        // =========    =========   =========   =========
        // CHECKPOINT 0: PROGRAM RUNS WITH CORRECT ARGS
        // =========    =========   =========   =========

        // ========= Parse All Data =========
        // - Sets - Cards - Roles
        Path cardPath = Paths.get("xml", "cards.xml");
        SceneCardParser cardParser = new SceneCardParser(cardPath.toString());
        ArrayList<SceneCard> cardList = cardParser.GetParsedList();

        Path setPath = Paths.get("xml", "board.xml");
        GameSetParser boardParser = new GameSetParser(setPath.toString());
        CastingSet castingSet = boardParser.FindCastingSet();
        GameSet trailerSet = boardParser.FindTrailer();
        ArrayList<ActingSet> actingSets = boardParser.FindActingSets();

        // =========    =========   =========   =========
        // CHECKPOINT 1: ALL FILES PARSED AND DATA RECEIVED
        // =========    =========   =========   =========
//        TestCardList(cardList);
//        TestSetList(actingSets);
//
//        System.out.println(castingSet.toString());
//        System.out.println(trailerSet.toString());

        // ========= Set up RulePackage =========
        // Be careful of size of acting sets

        // Create and alter the rules based on num of players
        RulesPackage rulesPackage = new RulesPackage(playerCount);


        // Create a rule package for testing upgrades
        RulesPackage DevRulePackage = new RulesPackage(playerCount);
        DevRulePackage.SetStartingCredits(99);

        // ========= Set Up GameBoard =========
        // Be careful of size of acting sets

        // This is how we limit or at least ensure the
        // size of the board. Just grab the first 10 boards
        // We'll worry about neighbor checking it when it matters
        if (actingSets.size() > 10) {
            actingSets = limitSize(actingSets);
        }

        ActingSet[] formatedActedSets = actingSets.toArray(new ActingSet[actingSets.size()]);

        // Create an empty Game Board to play the game with the made sets
        GameBoard gameBoard = new GameBoard(formatedActedSets, castingSet, trailerSet);
            // How do we limit the game sets to 10 in total?
            // Good Question
        
        // ========= Set Up PlayerManager =========
        // When creating players, maybe add playerName?
        // We do that in AddPlayer in PlayerManager the first time its called.
        // We keep track via id anyway. Not necessary but would make sense
        PlayerManager playerManager = new PlayerManager(rulesPackage, trailerSet);
        //Calling StartGame in GameManager creates all players
        // But should it?

        // playerManager.CreatePlayers()?

//        System.out.println(playerManager.toString());
//        System.out.println(trailerSet.toString());

        // ========= Set Up GameManager =========
        // Create a Game Manager, passing in the rulesPackage
        // When start Game Is called it asks for the player count and sets the Rules for Game manager!
        GameManager gameManager = GameManager.GetInstance();
        gameManager.SetRules(rulesPackage);
        gameManager.SetGameBoard(gameBoard);

//        System.out.println(gameManager.toString());

        // =========    =========   =========   =========
        // CHECKPOINT 2: GAME HAS BEEN INSTANTIATED AND READY TO START
        // =========    =========   =========   =========

        // Now the game runs in its entirety or until someone quits.
        gameManager.StartGame(playerManager);

        // =========    =========   =========   =========
        // CHECKPOINT 3: QUIT PROGRAM
        // =========    =========   =========   =========

    }

    public static void TestCardList(ArrayList<SceneCard> list) {
        // Testing The Cards sets
        for (SceneCard sceneCard : list)
        {
            System.out.println(sceneCard.toString());
        }
    }

    public static void TestSetList(ArrayList<ActingSet> list) {
        for (ActingSet set : list)
        {
            System.out.println(set.toString());
        }
    }

    public static ArrayList<ActingSet> limitSize(ArrayList<ActingSet> list) {
        ArrayList<ActingSet> trimmedSet = new ArrayList<>(10);
        for (int i = 0; i < list.size(); i++)
        {
            trimmedSet.add(list.get(i));
        }
        return trimmedSet;
    }


}
