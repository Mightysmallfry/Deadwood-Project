public class LocationComponent {

    //Members
    private int _rehearseTokens;
    private GameSet _currentGameSet;
    private ActingRole _currentRole;
    private boolean _onCard;

    // Constructors
    public LocationComponent()
    {

    }

    public LocationComponent(GameSet startingLocation)
    {
        Set_CurrentGameSet(startingLocation);
    }

    public LocationComponent(GameSet startingLocation, ActingRole startingRole)
    {
        Set_CurrentGameSet(startingLocation);
        Set_CurrentRole(startingRole);
    }

    //Getters
    public ActingRole Get_CurrentRole() {return _currentRole;}

    public int Get_RehearseTokens() {return _rehearseTokens;}

    public GameSet Get_CurrentGameSet() {return _currentGameSet;}

    public boolean Get_OnCard(){return _onCard;}

    // Setters
    public void Set_CurrentGameSet(GameSet _currentGameSet) {this._currentGameSet = _currentGameSet;}

    public void Set_CurrentRole(ActingRole _currentRole) {this._currentRole = _currentRole;}

    public void Set_OnCard(boolean _onCard) {this._onCard = _onCard;}

    public void Set_RehearseTokens(int _rehearseTokens) {this._rehearseTokens = _rehearseTokens;}

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
