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
        Document setDocument = null;
        try{
            Path setPath = Paths.get("xml", "board.xml");
            setDocument = parser.GetDocumentFromFile(setPath.toString());
        } catch (Exception e) {
            System.out.println("Something went wrong parsing : " + e);
        }

        if (setDocument == null) {
            System.out.println("Something went wrong initializing setDocuments after parsing");
            System.exit(0);
        }

        // All of our cards are already
        // Added to the static library of cards as we parse
        // But we can still access and test them as such
        Path cardPath = Paths.get("xml", "cards.xml");
        SceneCardParser cardParser = new SceneCardParser(cardPath.toString());
        ArrayList<SceneCard> cardList = cardParser.GetParsedList();

        // TODO: Fix Parsing the Sets
        CastingSet castingSet = parser.ParseCastingSet(setDocument);
        GameSet trailerSet = parser.FindTrailerSetData(setDocument);
        ArrayList<ActingSet> actingSets = parser.FindActingSetData(setDocument);

        TestCardList(cardList);
        TestSetList(actingSets);

        System.out.println(castingSet.toString());
        System.out.println(trailerSet.toString());

        System.exit(0);


        // Be careful of size of acting sets
        ActingSet[] formatedActedSets = actingSets.toArray(new ActingSet[actingSets.size()]);

        // Create an empty Game Board to play the game with the made sets
        GameBoard gameBoard = new GameBoard(formatedActedSets, castingSet, trailerSet);
            // How do we limit the game sets to 10 in total?

        // Populate the Game Board with cards
        gameBoard.Populate();

        // Create a Game Manager, passing in the rulesPackage
        //When start Game Is called it asks for the player count and sets the Rules for Game manager!

        // When creating players, maybe add playerName? //We do that in AddPlayer in PlayerManager the first time its called.
        // We keep track via id anyway. Not necessary but would make sense
        PlayerManager playerManager = new PlayerManager(); //Calling StartGame in GameManager creates all players and


        // Begin game with player 0/1 //First player is chosen in start game.

        // When it is time end the game

        // Display winner/leaderboard

        // Quit Program

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
