package mothman.utils;

import mothman.player.Player;

public class RulesPackage {
    // constants

    // defaults (4 players case)
    private int _days = 4;
    private int _startingCredits = 0;
    private int _startingRank = 1;
    private int _playerCount = 4;

    // Basic constructor, returns new with default 4 player case
    public RulesPackage() {}

    public RulesPackage(int playerCount) {

        _playerCount = playerCount;

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

    public int GetPlayerCount() { return _playerCount; }

    public void SetDays(int days) {
        _days = days;
    }

    public void SetStartingCredits(int credits) {
        _startingCredits = credits;
    }

    public void SetStartingRank(int rank) {
        _startingRank = rank;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("RulesPackage [ ");
        sb.append("<Player Count>: " + _playerCount + " ");
        sb.append("<Total Days>: " + _days + " ");
        sb.append("<Starting Rank>: " + _startingRank + " ");
        sb.append("<Starting Credits>: " + _startingCredits + "]");
        return sb.toString();
    }


}
