public class RulesPackage {

    private int days;
    private int startingCredits;
    private int startingRank;

    public RulesPackage(int playerCount) {

        // defaults (4 players case)
        days = 4;
        startingCredits = 0;
        startingRank = 1;

        if (playerCount == 2 || playerCount == 3) {
            days = 3;
        }
        else if (playerCount == 5) {
            startingCredits = 2;
        }
        else if (playerCount == 6) {
            startingCredits = 4;
        }
        else if (playerCount == 7 || playerCount == 8) {
            startingRank = 2;
        }
    }

    public RulesPackage(Player[] playerList) {
        int playerCount = playerList.length;
        // defaults (4 players case)
        days = 4;
        startingCredits = 0;
        startingRank = 1;

        if (playerCount == 2 || playerCount == 3) {
            days = 3;
        }
        else if (playerCount == 5) {
            startingCredits = 2;
        }
        else if (playerCount == 6) {
            startingCredits = 4;
        }
        else if (playerCount == 7 || playerCount == 8) {
            startingRank = 2;
        }
    }


    // Getters only — rules should not mutate mid-game
    public int GetDays() {
        return days;
    }

    public int GetStartingCredits() {
        return startingCredits;
    }

    public int GetStartingRank() {
        return startingRank;
    }
}
