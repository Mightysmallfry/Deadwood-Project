public class ActingRole {

    // Members
    private String _name;    // Name of Role
    private String _line;    // Speaking Line
    private int _rank;       // Also called level

    private Area _area;

    // Constructors
    public ActingRole(int rank, String name) {
        _rank = rank;
        _name = name;
        _line = "";
        _area = new Area();
    }

    public ActingRole(int rank, String name, String line) {
        _rank = rank;
        _name = name;
        _line = line;
        _area = new Area();
    }

    public ActingRole(int rank, String name, String line, Area area) {
        _rank = rank;
        _name = name;
        _line = line;
        _area = area;
    }

    // Methods
    public String GetName() {
        return _name;
    }

    public String GetLine() {
        return _line;
    }

    public int GetRank() {
        return _rank;
    }

    public void SetName(String name) {
        _name = name;
    }

    public void SetLine(String line) {
        _line = line;
    }

    public void SetRank(int rank) {
        _rank = rank;
    }

    public void SetArea(Area area) {
        _area = area;
    }

    public Area GetArea() {
        return _area;
    }

    public String ToString() {
        return "ActingRole{" +
                "name='" + _name + '\'' +
                ", line='" + _line + '\'' +
                ", rank=" + _rank +
                '}';
    }
}
