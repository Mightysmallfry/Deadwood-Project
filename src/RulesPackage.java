public class RulesPackage {

    private int _days;
    private int _startingCredits;
    private int _startingRank;

    public RulesPackage(int playerCount) {

        // defaults (4 players case)
        _days = 4;
        _startingCredits = 0;
        _startingRank = 1;

        if (playerCount == 2 || playerCount == 3) {
            _days = 3;
        }
        else if (playerCount == 5) {
            _startingCredits = 2;
        }
        else if (playerCount == 6) {
            _startingCredits = 4;
        }
        else if (playerCount == 7 || playerCount == 8) {
            _startingRank = 2;
        }
    }

    public RulesPackage(Player[] playerList) {
        int playerCount = playerList.length;
        // defaults (4 players case)
        _days = 4;
        _startingCredits = 0;
        _startingRank = 1;

        if (playerCount == 2 || playerCount == 3) {
            _days = 3;
        }
        else if (playerCount == 5) {
            _startingCredits = 2;
        }
        else if (playerCount == 6) {
            _startingCredits = 4;
        }
        else if (playerCount == 7 || playerCount == 8) {
            _startingRank = 2;
        }
    }


    // Getters only — rules should not mutate mid-game
    public int GetDays() {
        return _days;
    }

    public int GetStartingCredits() {
        return _startingCredits;
    }

    public int GetStartingRank() {
        return _startingRank;
    }
}
