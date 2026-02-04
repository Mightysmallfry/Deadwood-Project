public class GameBoard {

    // Members
    private GameSet[] _gameSets;
    private GameSet _startingGameSet;   // Trailer Set
    private CastingSet _upgradeSet;     // Casting Office Set

    // Constructors
    public GameBoard()
    {

    }

    public GameBoard(GameSet[] gameSets)
    {
        this._gameSets = gameSets;
    }

    //Methods
    public void Clear()
    //This method clears the cards on the board in preparation for a day reset
    {

    }

    public void Populate()
     //This method places the game sets including Upgrade and Casting
    {

    }

    public void AddGameSet(GameSet gameSet)
     //This adds a GameSet to be used on the board during population
    {

    }

    public void RemoveGameSet(GameSet gameSet)
     //Helps remove the GameSets for clearing the board
    {

    }

    public GameSet[] GetAllGameSets()
    {
        return _gameSets;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("GameBoard {\n");

        sb.append("  Starting Set: ");
        sb.append(_startingGameSet != null ? _startingGameSet.toString() : "null");
        sb.append("\n");

        sb.append("  Upgrade Set: ");
        sb.append(_upgradeSet != null ? _upgradeSet.toString() : "null");
        sb.append("\n");

        sb.append("  Total GameSets: ");
        sb.append(_gameSets != null ? _gameSets.length : 0);
        sb.append("\n");

        sb.append("  GameSets:\n");

        if (_gameSets != null) {
            for (int i = 0; i < _gameSets.length; i++) {
                sb.append("    [")
                        .append(i)
                        .append("] ")
                        .append(_gameSets[i] != null ? _gameSets[i].toString() : "null")
                        .append("\n");
            }
        } else {
            sb.append("    null\n");
        }

        sb.append("}");

        return sb.toString();
    }

}
