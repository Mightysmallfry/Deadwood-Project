import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SceneCard {
    // Members
    private int _difficulty;
    private boolean _visible;

    private String _name;
    private String _description;

    private ArrayList<ActingRole> _roles;
    private Map<ActingRole, Player> _roleCatalog;

    // Constructors
    public SceneCard()
    {
        _difficulty = 1;
        _visible = false;
        _name = "Test Card";
        _description = "Descriptive description";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty)
    {
        _difficulty = difficulty;
        _visible = false;
        _name = "Test Card";
        _description = "Descriptive description";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name)
    {
        _difficulty = difficulty;
        _visible = false;
        _name = name;
        _description = "Descriptive description";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }
    public SceneCard(int difficulty, String name, String description)
    {
        _difficulty = difficulty;
        _visible = false;
        _name = name;
        _description = description;
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name, String description, boolean visible)
    {
        _difficulty = difficulty;
        _visible = visible;
        _name = name;
        _description = description;
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new Hashtable<ActingRole, Player>();
    }

    public SceneCard(int difficulty, String name, String description, boolean visible, ArrayList<ActingRole> roles)
    {
        _difficulty = difficulty;
        _visible = visible;
        _name = name;
        _description = description;
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
        for (ActingRole role : _roleCatalog.keySet())
        {
            if (_roleCatalog.get(role) == null)
            {
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
