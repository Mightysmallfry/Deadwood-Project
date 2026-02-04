import java.util.ArrayList;

public class GameSet {
    protected String _name;
    protected ArrayList<GameSet> _neighbors;
    protected ArrayList<Player> _players;

    protected Area _area;

    public GameSet()
    {
        _name = "Test GameSet Name";
        _neighbors = new ArrayList<GameSet>();
        _players = new ArrayList<Player>();
        _area = new Area();
    }

    public GameSet(String name)
    {
        _name = name;
        _neighbors = new ArrayList<GameSet>();
        _players = new ArrayList<Player>();
        _area = new Area();
    }

    public GameSet(String name, ArrayList<GameSet> neighbors)
    {
        _name = name;
        _neighbors = neighbors;
        _players = new ArrayList<Player>();
        _area = new Area();
    }

    public GameSet(String name, ArrayList<GameSet> neighbors, Area area)
    {
        _name = name;
        _neighbors = neighbors;
        _players = new ArrayList<Player>();
        _area = area;
    }


    public void SetNeighbors(ArrayList<GameSet> neighbors) {
        _neighbors = neighbors;
    }

    /**
     * Adds the neighbor to array list of neighbors, if the neighbor
     * does not already exist in the array.
     * @param neighbor
     */
    public void AddNeighbor(GameSet neighbor) {
        if (!_neighbors.contains(neighbor))
        {
            _neighbors.add(neighbor);
        }
    }

    public void RemoveNeighbor(GameSet neighbor) {
        if (!_neighbors.contains(neighbor)) {
            return;
        }

        _neighbors.remove(neighbor);
    }

    public ArrayList<GameSet> GetNeighbors() {
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

    public String ToString() {
        return "GameSet{" +
                "_name='" + _name + '\'' +
                ", _neighbors=" + _neighbors +
                ", _players=" + _players +
                '}';
    }
}
