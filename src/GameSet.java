import java.util.ArrayList;

public class GameSet {
    private String _name;
    private ArrayList<GameSet> _neighbors;
    private ArrayList<Player> _players;

    public GameSet()
    {
        _name = "Test GameSet Name";
        _neighbors = new ArrayList<GameSet>();
        _players = new ArrayList<Player>();
    }

    public GameSet(String name)
    {
        _name = name;
        _neighbors = new ArrayList<GameSet>();
        _players = new ArrayList<Player>();
    }

    public GameSet(String name, ArrayList<GameSet> neighbors)
    {
        _name = name;
        _neighbors = neighbors;
        _players = new ArrayList<Player>();
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

    public ArrayList<GameSet> GetNeighbors() {
        return _neighbors;
    }

    public ArrayList<Player> GetPlayers() {
        return _players;
    }

    public String GetName() {
        return _name;
    }

    public String ToString() {
        return "GameSet{" +
                "_name='" + _name + '\'' +
                ", _neighbors=" + _neighbors +
                ", _players=" + _players +
                '}';
    }
}
