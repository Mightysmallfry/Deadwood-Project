public class Deadwood {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Deadwood [PlayerCount]");
            System.exit(0);
        }
        int playerCount = Integer.parseInt(args[0]);

        System.out.println("Hello and welcome to Deadwood gunslinger!");
        System.out.printf("You have %d players!", playerCount);

        // Create and alter the rules based on num of players
        RulesPackage rulesPackage = new RulesPackage(playerCount);

        // Create a Game Manager, passing in the rulesPackage
            // When creating players, maybe add playerName?
            // We keep track via id anyway. Not necessary but would make sense

        // Parse the data needed for our game board
        // - Sets - Cards - Roles

        // Create an empty Game Board to play the game with the made sets

        // Populate the Game Board with cards

        // Begin game with player 0/1

        // When it is time end the game

        // Display winner/leaderboard

        // Quit Program

    }
}