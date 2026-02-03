public class Player {

    private LocationComponent _location;
    private CurrencyComponent _currency;
    private ActingRole _role;
    private static int _id = 1;
    private int _personalId;
    private int _score;
    private int _currentRank;

    private Player()
    {
        Set_PersonalId(Get_Id());
        _id++;
        Set_CurrentRank(1);
    }

    private Player(int startingRank)
    {
        Set_PersonalId(Get_Id());
        _id++;
        Set_CurrentRank(startingRank);
    }

    //setter start
    public void Set_Currency(CurrencyComponent _currency) {
        this._currency = _currency;
    }

    public void set_role(ActingRole _role) {
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

    public void Set_Score(int _score) {
        this._score = _score;
    }

    //getter start
    public CurrencyComponent Get_Currency() {
        return _currency;
    }

    public ActingRole get_role() {
        return _role;
    }

    public int Get_PersonalId() {
        return _personalId;
    }

    public int Get_GurrentRank() {
        return _currentRank;
    }

    public int Get_Score() {
        return _score;
    }

    public LocationComponent get_location() {
        return _location;
    }

    public int Get_Id() {
        return _id;
    }

    public boolean HasRole(ActingRole role)
    {
        //need to implement a equality check in ActingRole
        return role == get_role();
    }

    public String ToString()
    {
        return "Player{" +
                "_location=" + _location +
                ", _currency=" + _currency +
                ", _id=" + _id +
                ", _personalId='" + _personalId + '\'' +
                ", _score=" + _score +
                ", _currentRank=" + _currentRank +
                ", _role=" + _role +
                '}';
    }
}