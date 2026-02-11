public class Player {

    // Members
    private LocationComponent _location;
    private CurrencyComponent _currency;
    private ActingRole _role;
    private static int _ID = 1;
    private int _personalId;
    private int _score;
    private int _currentRank;

    // Constructors
    public Player()
    {
        Set_PersonalId(Get_Id());
        _ID++;
        Set_CurrentRank(1);
    }

    public Player(int startingRank)
    {
        Set_PersonalId(Get_Id());
        _ID++;
        Set_CurrentRank(startingRank);
    }

    // Setter start
    public void Set_Currency(CurrencyComponent _currency) {
        this._currency = _currency;
    }

    public void Set_Role(ActingRole _role) {
        this._role = _role;
    }

    public void Set_PersonalId(int _personalId) {
        this._personalId = _personalId;
    }

    public void Set_Location(LocationComponent _location) {
        this._location = _location;
    }

    public void Set_CurrentRank(int _currentRank) {
        this._currentRank = _currentRank;
    }

    public void Set_Score()
    {
        CurrencyComponent currency = Get_Currency();
        this._score = ((Get_CurrentRank() * 5) + currency.Get_Coins() + currency.Get_Credits());
    }

    public void Set_Score(int _score){this._score = _score;}


    // Getter start
    public CurrencyComponent Get_Currency() {
        return _currency;
    }

    public ActingRole Get_Role() {
        return _role;
    }

    public int Get_PersonalId() {
        return _personalId;
    }

    public int Get_CurrentRank() {
        return _currentRank;
    }

    public int Get_Score() {
        return _score;
    }

    public LocationComponent Get_Location() {
        return _location;
    }

    public int Get_Id() { return _ID; }

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