package mothman.sets;

import java.util.ArrayList;

public class CastingSet extends GameSet {
    private final String CASTING_OFFICE_NAME = "Casting Office";
    private int MAX_RANK = 6;
    // upgrade Maps should be <rank, cost> pairing
    private ArrayList<UpgradeData> _upgrades;

    public CastingSet() {
        _name = CASTING_OFFICE_NAME;
        _upgrades = new ArrayList<UpgradeData>();
    }

    public CastingSet(String name, ArrayList<UpgradeData> upgrades) {
        _name = CASTING_OFFICE_NAME;
        _upgrades = upgrades;
        CalcMaxRank();
    }

    public void SetUpgrades(ArrayList<UpgradeData> upgrades) {
        _upgrades = upgrades;
        CalcMaxRank();
    }

    public ArrayList<UpgradeData> GetUpgrades() {
        return _upgrades;
    }

    public int GetMaxRank() {
        return MAX_RANK;
    }

    // Recalculates the maximum rank that this casting set can raise player to
    public void CalcMaxRank() {
        if (_upgrades == null || _upgrades.isEmpty()) {
            return;
        }

        for (int i = 0; i < _upgrades.size(); i++) {
            if (_upgrades.get(i).GetRank() > MAX_RANK) {
                MAX_RANK = _upgrades.get(i).GetRank();
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("|| Casting Set ||\n");
        sb.append("Name : ");
        sb.append(_name).append("\n");

        sb.append("Area : ");
        sb.append(_area != null? _area.toString() : "null").append("\n");

        sb.append("Neighbors : ");
        sb.append(_neighbors != null ? _neighbors.keySet() : "null").append("\n");

        sb.append("Players : ");
        sb.append(_players != null ? _players.toString() : "null").append("\n");

        sb.append("Upgrades :\n");
        if (_upgrades != null) {
            for (UpgradeData u : _upgrades) {
                sb.append("- ").append(u.toString()).append("\n");
            }
        } else {
            sb.append("null\n");
        }

        return sb.toString();
    }
}
