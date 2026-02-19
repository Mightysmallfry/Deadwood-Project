package mothman.sets;

import mothman.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SceneCard {
    // Statics
    private static ArrayList<SceneCard> _cardCatalog = new ArrayList<SceneCard>();

    // Members
    private boolean _visible;
    private int _difficulty;
    private int _cardNumber;

    private String _name;
    private String _description;
    private String _imageName;

    private ArrayList<ActingRole> _roles;
    private Map<ActingRole, Player> _roleCatalog;

    private boolean _used;


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
        _roleCatalog = new HashMap<ActingRole, Player>();

        _used = false;
        RegisterCard(this);
    }


    public SceneCard(int difficulty, String name, String imageName)
    {
        _difficulty = difficulty;
        _name = name;
        _imageName = imageName;
        _cardNumber = 0;
        _visible = false;
        _description = "Descriptive description";
        _roles = new ArrayList<ActingRole>();
        _roleCatalog = new HashMap<ActingRole, Player>();
        _used = false;
        RegisterCard(this);
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
        _roleCatalog = new HashMap<ActingRole, Player>();
        _used = false;
        RegisterCard(this);
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
        _roleCatalog = new HashMap<ActingRole, Player>();
        _used = false;
        RegisterCard(this);
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
        _roleCatalog = new HashMap<ActingRole, Player>();
        _used = false;
        RegisterCard(this);
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
        _roleCatalog = new HashMap<ActingRole, Player>();

        for (ActingRole role : roles)
        {
            _roleCatalog.put(role, null);
        }

        _used = false;
        RegisterCard(this);
    }

    // Methods

    /**
     *
     * @return difficulty aka budget of the card
     */
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

    public void SetVisible(boolean isVisible){
        _visible = isVisible;
    }

    public boolean IsUsed(){
        return _used;
    }

    public void SetUsed(boolean isUsed) {
        _used = isUsed;
    }



    /**
     *
     * @return array list containing all roles present on the card
     * regardless of player occupation
     */
    public ArrayList<ActingRole> GetRoles(){
        return _roles;
    }

    /**
     * Searches through the role dictionary, returning an arrayList
     * containing all the roles without an associated player
     * @return availableRoles
     */
    public ArrayList<ActingRole> GetAvailableRoles(){
        ArrayList<ActingRole> availableRoles = new ArrayList<>();
        for (ActingRole actingRole : _roles)
        {
            if (_roleCatalog.get(actingRole) == null){
                availableRoles.add(actingRole);
            }
        }
        return availableRoles;
    }


    /**
     *
     * @return A map of all roles and the associated player
     */
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

    public void SetCardNumber(int cardNumber) {
        _cardNumber = cardNumber;
    }

    public void SetDescription(String description) {
        _description = description;
    }

    /**
     * Adds target role to the card, ignoring duplicates
     * @param actingRole
     */
    public void AddRole(ActingRole actingRole) {
        // Already exists, early return
        if (_roles.contains(actingRole)) {
            return;
        }

        _roles.add(actingRole);
        _roleCatalog.put(actingRole, null);
    }

    /**
     * Registers card with the card catalog.
     * Allowing it to be accessed globally
     */
    public void RegisterCard(SceneCard card)
    {
        SceneCard._cardCatalog.add(card);
    }

    /**
     * Gets the list of all cards that have been constructed and registered.
     * @return
     */
    public static ArrayList<SceneCard> GetCardCatalog()
    {
        return _cardCatalog;
    }

    /**
     * iterates through the card catalog checking if the card
     * has already been used in play
     * @return an ArrayList containing all unused cards, ready for play
     */
    public static ArrayList<SceneCard> GetAvailableCards()
    {
        ArrayList<SceneCard> availableCards = new ArrayList<SceneCard>();

        for (int i = 0; i < SceneCard._cardCatalog.size(); i++) {
            if (SceneCard._cardCatalog.get(i) != null && !SceneCard._cardCatalog.get(i).IsUsed()) {
                availableCards.add(SceneCard._cardCatalog.get(i));
            }
        }
        return availableCards;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("==== Scene Card ====\n");

        sb.append("name : ");
        sb.append(_name != null ? _name : null).append("\n");

        sb.append("difficulty : ");
        sb.append(_difficulty).append("\n");

        sb.append("cardNumber : ");
        sb.append(_cardNumber).append("\n");

        sb.append("description : \n");
        sb.append(_description != null ? _description : null).append("\n");

        sb.append("visible : ");
        sb.append(_visible).append("\n");

        sb.append("imageName : ");
        sb.append(_imageName != null ? _imageName : null).append("\n");

        sb.append("roles : ");
        if (_roles != null) {
            sb.append("\n");
            for (ActingRole role : _roles) {
                sb.append("- ");
                sb.append(role.toString());
                sb.append("\n");
            }
        }

        sb.append("roleCatalog : (Role, Player)");
        if (_roleCatalog != null){
            sb.append("\n");
            for (Map.Entry<ActingRole, Player> entry : _roleCatalog.entrySet()) {
                sb.append("- {");
                sb.append(entry.getKey().GetName()).append(", ");
                Player entryValue = entry.getValue();
                sb.append(entryValue != null ? entryValue : "null");
                sb.append("}\n");
            }
        }

        return sb.toString();
    }
}
