public class Player {
    // Statics

    // Members
    private LocationComponent _location;
    private CurrencyComponent _currency;
//    private ActingRole _role;
    private String _personalId; //name
    private int _score = 0;
    private int _currentRank;

    //Role now only lives in location component.
    //Static ID currently does basically nothing so I figure we remove it because we now have a better method in the player manager
    //I think we should just hve it be its index in the array at least.

    // Constructors
    public Player()
    {
        SetPersonalId("");
    }

    public Player(String personalId)
    {
        SetPersonalId(personalId);
        SetCurrentRank(1);
    }

    public Player(String personalId, int startingRank)
    {
        SetPersonalId(personalId);
        SetCurrentRank(_currentRank);
    }

    public Player(String personalId, int startingRank, LocationComponent location)
    {
        SetPersonalId(personalId);
        SetCurrentRank(_currentRank);
        SetLocation(location);
    }

    // Setter start
    public void SetCurrency(CurrencyComponent currencyComponent) {
        _currency = currencyComponent;
    }

//    public void SetRole(ActingRole role) {
//        _role = role;
//    }

    public void SetPersonalId(String personalId) {
        _personalId = personalId;
    }

    public void SetLocation(LocationComponent locationComponent) {
        _location = locationComponent;
    }

    public void SetCurrentRank(int rank) {
        _currentRank = rank;
    }

    public void SetScore()
    {
        CurrencyComponent currency = GetCurrency();
        this._score = ((GetCurrentRank() * 5) + currency.GetCoins() + currency.GetCredits());
    }

    public void SetScore(int _score){this._score = _score;}


    // Getter start
    public CurrencyComponent GetCurrency() {
        return _currency;
    }

//    public ActingRole GetRole() {
//        return _role;
//    }

    public String GetPersonalId() {
        return _personalId;
    }

    public int GetCurrentRank() {
        return _currentRank;
    }

    public int GetScore() {
        return _score;
    }

    public LocationComponent GetLocation() {
        return _location;
    }

    // Methods
    public boolean HasRole(ActingRole role)
    {

        ActingRole currentRole = GetLocation().GetCurrentRole();
        return currentRole != null && currentRole.equals(role);
    }

    public boolean HasRole() {
        return GetLocation().GetCurrentRole() != null;
    }

    @Override
    public String toString()
    {
        return "Player{" +
                "_location=" + _location +
                ", _currency=" + _currency +
                ", _personalId='" + _personalId + '\'' +
                ", _score=" + _score +
                ", _currentRank=" + _currentRank +
                '}';
    }
}