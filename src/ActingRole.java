public class ActingRole {

    // Members
    private String _name;    // Name of Role
    private String _line;    // Speaking Line

    private int _rank;       // Also called level

    // Constructors
    public ActingRole(int rank, String name) {
        _rank = rank;
        _name = name;
    }

    public ActingRole(int rank, String name, String line) {
        _rank = rank;
        _name = name;
        _line = line;
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

    public String ToString() {
        return "ActingRole{" +
                "name='" + _name + '\'' +
                ", line='" + _line + '\'' +
                ", rank=" + _rank +
                '}';
    }
}
