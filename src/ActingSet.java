import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActingSet extends GameSet {
    // Members
    private int _maximumProgress;   // At this progress the card should be complete
    private int _currentProgress;   // Current Progress via running Game

    private boolean _complete;

    private SceneCard _currentSceneCard;
    private ArrayList<ActingRole> _localRoles;
    private Map<ActingRole, Player> _roleCatalog;

    // Constructors
    public ActingSet(){
        // Inherited
        _name = "Basic Acting Set";
        _area = new Area();
        _neighbors = new ArrayList<>();
        _players = new ArrayList<>();

        // Owned
        _maximumProgress = 3;
        _currentProgress = 0;
        _complete = false;
        _currentSceneCard = null;
        _localRoles = new ArrayList<>();
        _roleCatalog = new HashMap<>();
    }

    public ActingSet(String name, Area area, ArrayList<GameSet> neighbors,
                     int maximumProgress, ArrayList<ActingRole> localRoles){
        // Inherited
        _name = name;
        _area = area;
        _neighbors = neighbors;
        _players = new ArrayList<>();

        // Owned
        _maximumProgress = maximumProgress;
        _currentProgress = 0;
        _complete = false;
        _currentSceneCard = null;
        _localRoles = localRoles;

        for (ActingRole role : localRoles){
            _roleCatalog.put(role, null);
        }
    }


    // Methods
    public boolean IsComplete(){
        _complete = _maximumProgress == _currentProgress;
        return _complete;
    }

    public void ResetForNewDay() {
        _currentProgress = 0;
        _complete = false;

        // clear local role occupancy
        _roleCatalog.replaceAll((r, v) -> null);
    }


    public ArrayList<ActingRole> GetLocalRoles(){
        return _localRoles;
    }

    public ArrayList<ActingRole> GetAvailableRoles(){
        ArrayList<ActingRole> availableRoles = new ArrayList<>();

        // If the scene card is complete
        // There are no available roles
        if (_complete){
            return availableRoles;
        }

        for (ActingRole role : _roleCatalog.keySet()){
            if(_roleCatalog.get(role) == null)
            {
                availableRoles.add(role);
            }
        }

        if (_currentSceneCard != null) {
            availableRoles.addAll(_currentSceneCard.GetEmptyRoles());
        }

        return availableRoles;
    }

    public void AddCard(SceneCard card){
        _currentSceneCard = card;
    }

    public SceneCard Get_CurrentSceneCard(){return _currentSceneCard;}

    public void RemoveCard() {
        _currentSceneCard = null;
    }

    public int GetCurrentProgress(){
        return _currentProgress;
    }

    public void SetCurrentProgress(int progress){
        _currentProgress = progress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("|| Acting Set ||\n");
        sb.append("Name : ");
        sb.append(_name).append("\n");

        sb.append("Scene Card : ");
        sb.append(_currentSceneCard != null ? _currentSceneCard.GetName() : "null").append("\n");

        sb.append("Progress : ");
        sb.append(_currentProgress).append("/").append(_maximumProgress).append("\n");

        sb.append("Area : ");
        sb.append(_area != null? _area.toString() : "null").append("\n");

        sb.append("Neighbors : ");
        sb.append(_neighbors != null ? _neighbors.toString() : "null").append("\n");

        sb.append("LocalRoles : ");
        sb.append(_localRoles != null ? _localRoles.toString() : "null").append("\n");

        sb.append("RoleCatalog : ");
        sb.append(_roleCatalog != null ? _roleCatalog.toString() : "null").append("\n");

        sb.append("Players : ");
        sb.append(_players != null ? _players.toString() : "null").append("\n");

        return sb.toString();
    }
}
