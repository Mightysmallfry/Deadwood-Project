import java.util.HashMap;
import java.util.Map;

public class CastingSet extends GameSet {

    // Cost Maps should be <rank, cost> pairing
    private Map<Integer, Integer> _upgradeCostsCoin;
    private Map<Integer, Integer> _upgradeCostsCredit;

    public CastingSet() {
        _upgradeCostsCoin = new HashMap<Integer, Integer>();
        _upgradeCostsCredit = new HashMap<Integer, Integer>();
    }
}
