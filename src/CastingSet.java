import java.util.ArrayList;
import java.util.HashMap;

public class CastingSet extends GameSet {
    private final String CASTING_OFFICE_NAME = "Casting Office";
    // Cost Maps should be <rank, cost> pairing
    private ArrayList<UpgradeData> _upgrades;

    public CastingSet() {
        _name = CASTING_OFFICE_NAME;
        _upgrades = new ArrayList<UpgradeData>();
    }

    public CastingSet(String name, ArrayList<UpgradeData> upgrades) {
        _name = CASTING_OFFICE_NAME;
        _upgrades = upgrades;
    }

    public void SetUpgrades(ArrayList<UpgradeData> upgrades) {
        _upgrades = upgrades;
    }

    public ArrayList<UpgradeData> GetUpgrades() {
        return _upgrades;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString());

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
