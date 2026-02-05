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
            if(_roleCatalog.get(role) != null)
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
        return "ActingSet{" +
                "_maximumProgress=" + _maximumProgress +
                ", _currentProgress=" + _currentProgress +
                ", _complete=" + _complete +
                ", _currentSceneCard=" + _currentSceneCard +
                ", _localRoles=" + _localRoles +
                ", _roleCatalog=" + _roleCatalog +
                ", _name='" + _name + '\'' +
                ", _neighbors=" + _neighbors +
                ", _players=" + _players +
                ", _area=" + _area +
                '}';
    }
}
