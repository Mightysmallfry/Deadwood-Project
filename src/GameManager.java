public class GameManager {

    private static GameManager _instance;

    // Members
    private TurnAction _playerAction = new Upgrade();

    // Constructors
    private GameManager() {}

    public static GameManager GetInstance() {
        if (_instance == null)
        {
            _instance = new GameManager();
        }
        return _instance;
    }

    // Methods
    /**
     * @param playerAction any player action that implements the TurnAction interface
     */
    private void SetPlayerAction(TurnAction playerAction) {
        _playerAction = playerAction;
    }


}
