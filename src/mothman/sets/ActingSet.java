package mothman.sets;

import mothman.player.Player;
import mothman.utils.Area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActingSet extends GameSet {
    // Members
    private int _maximumProgress = 3;   // At this progress the card should be complete
    private int _currentProgress = 0;   // Current Progress via running Game

    private boolean _complete = false;

    private SceneCard _currentSceneCard;
    private ArrayList<ActingRole> _localRoles;
    private Map<ActingRole, Player> _roleCatalog;

    // Constructors
    public ActingSet(){
        // Inherited
        _name = "Basic Acting Set";
        _area = new Area();
        _neighbors = new HashMap<>();
        _players = new ArrayList<>();

        // Owned
        _maximumProgress = 3;
        _currentProgress = 0;
        _complete = false;
        _currentSceneCard = null;
        _localRoles = new ArrayList<>();
        _roleCatalog = new HashMap<>();
    }

    public ActingSet(String name, Area area, HashMap<String,GameSet> neighbors,
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
        _roleCatalog = new HashMap<>(localRoles.size());

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

    public ArrayList<ActingRole> GetAvailableLocalRoles(){
        ArrayList<ActingRole> availableLocalRoles = new ArrayList<>();
        if (_localRoles.isEmpty() || _complete) {
            return null;
        }

        for (ActingRole role : _localRoles){
            if (_roleCatalog.get(role) == null){
                availableLocalRoles.add(role);
            }
        }
        return availableLocalRoles;
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
        if (_currentSceneCard != null) {
            return;
        }

        _currentSceneCard = card;
    }

    public SceneCard GetCurrentSceneCard(){return _currentSceneCard;}

    public void RemoveCard() {
        _currentSceneCard = null;
    }

    public int GetCurrentProgress(){
        return _currentProgress;
    }

    public int GetMaxProgress() {return _maximumProgress;}

    public void SetCurrentProgress(int progress){
        _currentProgress = progress;
    }

    public Map<ActingRole, Player> GetRoleCatalog()
    {
        return _roleCatalog;
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
        sb.append(_neighbors != null ? _neighbors.keySet() : "null").append("\n");

        sb.append("LocalRoles : ");
        if (_localRoles != null) {
            sb.append("\n");
            for (ActingRole role : _localRoles) {
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

        sb.append("Players : ");
        sb.append(_players != null ? _players : "null").append("\n");

        return sb.toString();
    }
}
