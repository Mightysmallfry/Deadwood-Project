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
        SetParser parser = new SetParser();
        Document cardDocument = null;
        Document setDocument = null;
        try{
            Path cardPath = Paths.get("xml", "cards.xml");
            Path setPath = Paths.get("xml", "board.xml");
            cardDocument = parser.GetDocumentFromFile(cardPath.toString());
            setDocument = parser.GetDocumentFromFile(setPath.toString());
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

        // All of our cards are already
        // Added to the static library of cards
        parser.ParseSceneCardData(cardDocument);

        CastingSet castingSet = parser.ParseCastingSet(setDocument);
        GameSet trailerSet = parser.FindTrailerSetData(setDocument);
        ArrayList<ActingSet> actingSets = parser.FindActingSetData(setDocument);

        TestSceneCards();
        System.out.println(castingSet.toString());
        System.out.println(trailerSet.toString());
        // TestActingSets(actingSets);

        System.exit(0);


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

    public static void TestActingSets(ArrayList<ActingSet> actingSets) {
        for (ActingSet set : actingSets)
        {
            System.out.println(actingSets.toString());
        }
    }

}
