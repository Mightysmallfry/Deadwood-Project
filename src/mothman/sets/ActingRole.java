package mothman.sets;

import mothman.utils.Area;

public class ActingRole {

    // Members
    private String _name = "";    // Name of Role
    private String _line = "";    // Speaking Line
    private int _rank = 1;        // Also called level

    private Area _area = new Area();

    // Constructors
    public ActingRole() {}

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

    public Area GetArea() {
        return _area;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActingRole {");
        sb.append("<Name> ").append(_name).append(", ");
        sb.append("<Rank> ").append(_rank).append(", ");
        sb.append("<Area> ").append(_area != null ? _area.toString() : "null").append(", ");
        sb.append("<Line> ").append(_line != null ? _line : "null");
        sb.append("}");


        return sb.toString();
    }
}
