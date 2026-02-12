import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Deadwood {
    public static void main(String[] args) throws ParserConfigurationException {
        if (args.length != 1) {
            System.out.println("Usage: java Deadwood [PlayerCount]");
            System.exit(0);
        }
        int playerCount = Integer.parseInt(args[0]);

        System.out.println("Hello and welcome to Deadwood gunslinger!");
        System.out.printf("You have %d players!\n", playerCount);

        // Create and alter the rules based on num of players
        RulesPackage rulesPackage = new RulesPackage(playerCount);

        // =========    =========   =========   =========
        // CHECKPOINT 0: PROGRAM RUNS WITH CORRECT ARGS
        // =========    =========   =========   =========

        // ========= Parse All Data =========
        // - Sets - Cards - Roles
        Path cardPath = Paths.get("xml", "cards.xml");
        SceneCardParser cardParser = new SceneCardParser(cardPath.toString());
        ArrayList<SceneCard> cardList = cardParser.GetParsedList();

        // TODO: Pass in the fileName to constructor
        Path setPath = Paths.get("xml", "board.xml");
        SetParser boardParser = new SetParser(setPath.toString());

        CastingSet castingSet = boardParser.FindCastingSet();
        GameSet trailerSet = boardParser.FindTrailer();
        ArrayList<ActingSet> actingSets = boardParser.FindActingSets();

        // =========    =========   =========   =========
        // CHECKPOINT 1: ALL FILES PARSED AND DATA RECEIVED
        // =========    =========   =========   =========
        TestCardList(cardList);
        TestSetList(actingSets);

        System.out.println(castingSet.toString());
        System.out.println(trailerSet.toString());


        /// ========= EARLY EXIT HELPER =========
        System.exit(0);
        /// =====================================


        // ========= Set Up GameBoard =========
        // Be careful of size of acting sets
        ActingSet[] formatedActedSets = actingSets.toArray(new ActingSet[actingSets.size()]);

        // Create an empty Game Board to play the game with the made sets
        GameBoard gameBoard = new GameBoard(formatedActedSets, castingSet, trailerSet);
            // How do we limit the game sets to 10 in total?
            // Good Question

        // ========= Set Up PlayerManager =========
        // When creating players, maybe add playerName?
        // We do that in AddPlayer in PlayerManager the first time its called.
        // We keep track via id anyway. Not necessary but would make sense
        PlayerManager playerManager = new PlayerManager();
        //Calling StartGame in GameManager creates all players
        // But should it?

        // playerManager.CreatePlayers()?

        // ========= Set Up GameManager =========
        // Create a Game Manager, passing in the rulesPackage
        // When start Game Is called it asks for the player count and sets the Rules for Game manager!
        GameManager gameManager = GameManager.GetInstance();
        gameManager.SetRules(rulesPackage);
        gameManager.SetGameBoard(gameBoard);

        // =========    =========   =========   =========
        // CHECKPOINT 2: GAME HAS BEEN INSTANTIATED AND READY TO START
        // =========    =========   =========   =========

        // Now the game runs in its entirety or until someone quits.
        gameManager.StartGame();


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

}
