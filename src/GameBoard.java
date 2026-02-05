import java.util.ArrayList;

public class GameBoard {
    private final int _DEFAULT_SET_COUNT = 10;  // Should include Casting and Trailer Sets

    // Members
    private GameSet[] _gameSets;
    private GameSet _startingGameSet;   // Trailer Set
    private CastingSet _upgradeSet;     // Casting Office Set

    // Constructors
    // The GameBoard should be constructed with 10 sets in total
    // This includes the trailer and casting office.
    public GameBoard()
    {
        _gameSets = new GameSet[_DEFAULT_SET_COUNT];
    }

    public GameBoard(GameSet[] gameSets)
    {
        _gameSets = gameSets;
    }
    public GameBoard(GameSet[] gameSets, CastingSet upgradeSet)
    {
        _gameSets = gameSets;
        _upgradeSet = upgradeSet;
    }
    public GameBoard(GameSet[] gameSets, CastingSet upgradeSet, GameSet startingGameSet)
    {
        _gameSets = gameSets;
        _upgradeSet = upgradeSet;
        _startingGameSet = startingGameSet;
    }

    public GameSet Get_StartingSet(){return _startingGameSet;}

    //Methods
    public void Clear()
    //This method clears the cards on the board in preparation for a day reset
    {
        //This could just be act.ResetForNewDay
        for (GameSet gameSet : _gameSets) {
            if (gameSet instanceof ActingSet)
            {
                ((ActingSet) gameSet).RemoveCard();
            }
        }
    }


    //This method places the game sets including Upgrade and Casting
    //My understanding is that this just populates the sets and decides if they are Acting,
    // the SceneCard will come into existence if a player steps on an acting scene.

    /**
     * Populates each scene that can have a card with a scene card if
     * it does not already have one. The scene cards are chosen at random.
     */
    public void Populate()
    {
        if (_gameSets == null){
            return;
        }

        for (GameSet gameSet : _gameSets) {
            if (gameSet instanceof ActingSet){
                ArrayList<SceneCard> cards = SceneCard.GetAvailableCards();
                SceneCard randomCard = cards.get(Dice.GetInstance().Roll(1, cards.size()));
                ((ActingSet) gameSet).AddCard(randomCard);
            }
        }
    }


    /**
     * Attempts to add the given gameSet if a gameSet index is available.
     * Otherwise does nothing
     * @param gameSet
     */
    public void AddGameSet(GameSet gameSet)
    {
        for (int i = 0; i < _gameSets.length; i++) {
            if (_gameSets[i] == null) {
                _gameSets[i] = gameSet;
                break;
            }
        }
    }

    /**
     * Iterates through the list of gameSets, setting the index
     * value to null if the targetSet exists within the list.
     * @param targetSet
     */
    public void RemoveGameSet(GameSet targetSet)
    {
        for (int i = 0; i < _gameSets.length; i++) {
            if (_gameSets[i] == targetSet) {
                _gameSets[i] = null;
            }
        }
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
