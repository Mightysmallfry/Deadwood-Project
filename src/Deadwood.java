import org.w3c.dom.Document;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Deadwood {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Deadwood [PlayerCount]");
            System.exit(0);
        }
        int playerCount = Integer.parseInt(args[0]);

        System.out.println("Hello and welcome to Deadwood gunslinger!");
        System.out.printf("You have %d players!\n", playerCount);

        // Create and alter the rules based on num of players
        RulesPackage rulesPackage = new RulesPackage(playerCount);

        // Parse the data needed for our game board
        // - Sets - Cards - Roles
        XMLParser parser = new XMLParser();
        Document cardDocument = null;
        Document setDocument = null;
        try{
            Path cardPath = Paths.get("xml", "cards.xml");
            cardDocument = parser.GetDocumentFromFile(cardPath.toString());
//            setDocument = parser.GetDocumentFromFile("board.xml");
        } catch (Exception e) {
            System.out.println("Something went wrong parsing : " + e);
        }

        if (cardDocument == null){
            System.out.println("Something went wrong initializing cardDocuments after parsing");
            System.exit(0);
        }
        if (setDocument == null) {
            System.out.println("Something went wrong initializing setDocuments after parsing");
            System.exit(0);
        }

        parser.FindSceneCardData(cardDocument);

        // If you want to test if we parsed the cards right
        // Simply add
        // TestSceneCards();
        // After you execute FindSceneCardData()


        ArrayList<ActingSet> actingSets = parser.FindActingSetData(setDocument);

        CastingSet castingSet = parser.FindCastingSetData(setDocument);
        GameSet trailerSet = parser.FindTrailerSetData(setDocument);

        // Be careful of size of acting sets
        ActingSet[] formatedActedSets = actingSets.toArray(new ActingSet[actingSets.size()]);

        // Create an empty Game Board to play the game with the made sets
        GameBoard gameBoard = new GameBoard(formatedActedSets, castingSet, trailerSet);
            // How do we limit the game sets to 10 in total?

        // Populate the Game Board with cards
        gameBoard.Populate();

        // Create a Game Manager, passing in the rulesPackage
        // When creating players, maybe add playerName?
        // We keep track via id anyway. Not necessary but would make sense
        GameManager gameManager = new GameManager();



        // Begin game with player 0/1

        // When it is time end the game

        // Display winner/leaderboard

        // Quit Program

    }


    public static void TestSceneCards() {
        // Testing The Cards sets
        for (SceneCard sceneCard : SceneCard.GetCardCatalog())
        {
            System.out.println(sceneCard.toString());
        }
    }



}
