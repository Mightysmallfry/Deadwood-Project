package mothman.sets;

import mothman.utils.Area;

public class UpgradeData
{
    private int _rank;
    private int _costAmount;
    private String _currencyType;

    Area _area;

    public UpgradeData() {
        _rank = 0;
        _costAmount = 0;
        _currencyType = "dollar";
        _area = new Area();
    }

    public UpgradeData(int rank, int costAmount, String currencyType, Area area) {
        _rank = rank;
        _costAmount = costAmount;
        _currencyType = currencyType;
        _area = area;
    }

    public int GetRank() {
        return _rank;
    }

    public int GetCostAmount() {
        return _costAmount;
    }

    public String GetCurrencyType() {
        return _currencyType;
    }

    public Area GetArea() {
        return _area;
    }

    @Override
    public String toString() {
        return "{<rank> " + _rank +
                ", <currency> " + _currencyType +
                ", <cost> " + _costAmount +
                ", <area> " + _area.toString() + '}';
    }
}
