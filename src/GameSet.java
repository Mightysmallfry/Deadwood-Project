import java.util.ArrayList;
import java.util.HashMap;

public class GameSet {
    protected String _name;
    protected HashMap<String,GameSet> _neighbors;
    protected ArrayList<Player> _players;

    protected Area _area;

    public GameSet()
    {
        _name = "Test GameSet Name";
        _neighbors = new HashMap<>();
        _players = new ArrayList<>();
        _area = new Area();
    }

    public GameSet(String name)
    {
        _name = name;
        _neighbors = new HashMap<>();
        _players = new ArrayList<Player>();
        _area = new Area();
    }

    public GameSet(String name, HashMap<String,GameSet> neighbors)
    {
        _name = name;
        _neighbors = neighbors;
        _players = new ArrayList<>();
        _area = new Area();
    }

    public GameSet(String name, HashMap<String,GameSet> neighbors, Area area)
    {
        _name = name;
        _neighbors = neighbors;
        _players = new ArrayList<>();
        _area = area;
    }


    public void SetNeighbors(HashMap<String,GameSet> neighbors) {
        _neighbors = neighbors;
    }

    /**
     * Adds the neighbor to array list of neighbors, if the neighbor
     * does not already exist in the array.
     */
    //Does not work with hashMap
//    public void AddNeighbor(GameSet neighbor) {
//        if (!_neighbors.contains(neighbor))
//        {
//            _neighbors.add(neighbor);
//        }
//    }

//    public void RemoveNeighbor(GameSet neighbor) {
//        if (!_neighbors.contains(neighbor)) {
//            return;
//        }
//
//        _neighbors.remove(neighbor);
//    }

    public HashMap<String,GameSet> GetNeighbors() {
        return _neighbors;
    }

    public ArrayList<Player> GetPlayers() {
        return _players;
    }

    /**
     * Adds player to the current set if they are not already present
     * @param player
     */
    public void AddPlayer(Player player) {
        if (!_players.contains(player))
        {
            _players.add(player);
        }
    }

    public void RemovePlayer(Player player) {
        if (!_players.contains(player)){
            return;
        }

        _players.remove(player);
    }

    public void SetName(String name){
        _name = name;
    }

    public String GetName() {
        return _name;
    }

    public void SetArea(Area area) {
        _area = area;
    }

    public Area GetArea() {
        return _area;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("|| Game Set ||\n");
        sb.append("Name : ");
        sb.append(_name).append("\n");

        sb.append("Area : ");
        sb.append(_area != null? _area.toString() : "null").append("\n");

        sb.append("Neighbors : ");
        sb.append(_neighbors != null ? _neighbors.keySet() : "null").append("\n");

        sb.append("Players : ");
        sb.append(_players != null ? _players.toString() : "null").append("\n");

        return sb.toString();
    }
}
