import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SceneCard {
    // Members
    private boolean _visible;
    private int _difficulty;
    private int _cardNumber;

    private String _name;
    private String _description;
    private String _imageName;

    private ArrayList<ActingRole> _roles;
    private Map<ActingRole, Player> _roleCatalog;

    // Constructors
    public SceneCard()
    {
        _difficulty = 1;
        _cardNumber = 0;
        _visible = false;
        _name = "Test Card";
        _description = "Descriptive description";
        _imageName = "";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty)
    {
        _difficulty = difficulty;
        _cardNumber = 0;
        _visible = false;
        _name = "Test Card";
        _description = "Descriptive description";
        _imageName = "";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name)
    {
        _difficulty = difficulty;
        _cardNumber = 0;
        _visible = false;
        _name = name;
        _description = "Descriptive description";
        _imageName = "";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name, int cardNumber)
    {
        _difficulty = difficulty;
        _cardNumber = cardNumber;
        _visible = false;
        _name = name;
        _description = "Descriptive description";
        _imageName = "";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }


    public SceneCard(int difficulty, String name, int cardNumber,
                     String description)
    {
        _difficulty = difficulty;
        _cardNumber = cardNumber;
        _visible = false;
        _name = name;
        _description = description;
        _imageName = "";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name, int cardNumber,
                     String description, boolean visible)
    {
        _difficulty = difficulty;
        _cardNumber = cardNumber;
        _visible = visible;
        _name = name;
        _description = description;
        _imageName = "";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }


    public SceneCard(int difficulty, String name, int cardNumber,
                     String description, boolean visible, String imageName)
    {
        _difficulty = difficulty;
        _name = name;
        _cardNumber = cardNumber;
        _description = description;
        _visible = visible;
        _imageName = imageName;
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name, int cardNumber,
                     String description, boolean visible, String imageName,
                     ArrayList<ActingRole> roles)
    {
        _difficulty = difficulty;
        _name = name;
        _cardNumber = cardNumber;
        _description = description;
        _visible = visible;
        _imageName = imageName;
        _roles = roles;
        _roleCatalog = new Hashtable<ActingRole, Player>();

        for (ActingRole role : roles)
        {
            _roleCatalog.put(role, null);
        }
    }

    // Methods
    public int GetDifficulty(){
        return _difficulty;
    }

    public String GetName(){
        return _name;
    }

    public String GetDescription(){
        return _description;
    }

    public boolean IsVisible(){
        return _visible;
    }

    /**
     * Searches through the role dictionary, returning an arrayList
     * containing all the roles without an associated player
     * @return emptyRoles
     */
    public ArrayList<ActingRole> GetEmptyRoles()
    {
        ArrayList<ActingRole> emptyRoles = new ArrayList<ActingRole>();
        for (ActingRole role : _roleCatalog.keySet()) {
            if (_roleCatalog.get(role) == null) {
                emptyRoles.add(role);
            }
        }

        return emptyRoles;
    }

    /**
     *
     * @return array list containing all roles present on the card
     * regardless of player occupation
     */
    public ArrayList<ActingRole> GetRoles(){
        return _roles;
    }

    public Map<ActingRole, Player> GetRoleCatalog(){
        return _roleCatalog;
    }

    public int GetCardNumber() {
        return _cardNumber;
    }

    public String GetImageName() {
        return _imageName;
    }

    public void SetImageName(String imageName) {
        _imageName = imageName;
    }

    public String ToString() {
        return "SceneCard{" +
                "_name='" + _name +
                ", _difficulty=" + _difficulty + '\'' +
                ", _visible=" + _visible +
                ", _description='" + _description + '\'' +
                ", _roles=" + _roles +
                ", _roleDictionary=" + _roleCatalog +
                '}';
    }
}
