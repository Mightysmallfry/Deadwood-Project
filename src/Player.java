public class Player {
    // Statics
    private static int _ID = 1;

    // Members
    private LocationComponent _location;
    private CurrencyComponent _currency;
    private ActingRole _role;
    private int _personalId = 0;
    private int _score = 0;
    private int _currentRank = 1;

    //TODO: Consider that we have _role and _location.currentRole.
    // A bit redundant and currently not in a good way

    // Constructors
    public Player()
    {
        _personalId = _ID;
        _ID++;
    }

    public Player(int startingRank)
    {
        SetPersonalId(GetId());
        _ID++;
        SetCurrentRank(startingRank);
    }

    // Setter start
    public void SetCurrency(CurrencyComponent currencyComponent) {
        _currency = currencyComponent;
    }

    public void SetRole(ActingRole role) {
        _role = role;
    }

    public void SetPersonalId(int personalId) {
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

    public ActingRole GetRole() {
        return _role;
    }

    public int GetPersonalId() {
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

    public int GetId() { return _ID; }

    // Methods
    public boolean HasRole(ActingRole role)
    {
        // need to implement an equality check in ActingRole
        return _role != null && _role.equals(role);
    }

    public boolean HasRole() {
        return _role != null;
    }

    @Override
    public String toString()
    {
        return "Player{" +
                "_location=" + _location +
                ", _currency=" + _currency +
                ", _id=" + _ID +
                ", _personalId='" + _personalId + '\'' +
                ", _score=" + _score +
                ", _currentRank=" + _currentRank +
                ", _role=" + _role +
                '}';
    }
}