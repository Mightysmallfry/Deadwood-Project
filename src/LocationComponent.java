public class LocationComponent {

    //Members
    private int _rehearseTokens = 0;
    private GameSet _currentGameSet;
    private ActingRole _currentRole;
    private boolean _onCard = false;

    // Constructors
    public LocationComponent() {}

    public LocationComponent(GameSet startingLocation)
    {
        SetCurrentGameSet(startingLocation);
    }

    public LocationComponent(GameSet startingLocation, ActingRole startingRole)
    {
        SetCurrentGameSet(startingLocation);
        SetCurrentRole(startingRole);
    }

    //Getters
    public ActingRole GetCurrentRole() {return _currentRole;}

    public int GetRehearseTokens() {return _rehearseTokens;}

    public GameSet GetCurrentGameSet() {return _currentGameSet;}

    public boolean GetOnCard(){return _onCard;}

    // Setters
    public void SetCurrentGameSet(GameSet currentGameSet) {_currentGameSet = currentGameSet;}

    public void SetCurrentRole(ActingRole currentRole) {_currentRole = currentRole;}

    public void SetOnCard(boolean onCard) {_onCard = onCard;}

    public void SetRehearseTokens(int rehearseTokens) {_rehearseTokens = rehearseTokens;}

    // Methods
    @Override
    public String toString() {
        return "LocationComponent {" +
                "rehearseTokens=" + _rehearseTokens +
                ", currentGameSet=" + (_currentGameSet != null ? _currentGameSet.toString() : "None") +
                ", currentRole=" + (_currentRole != null ? _currentRole.toString() : "None") +
                ", onCard=" + _onCard +
                "}";
    }
}
