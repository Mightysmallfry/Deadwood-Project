package mothman.player;

public class CurrencyComponent {

    //members
    private int _coins = 0;
    private int _credits = 0;

    //constructors
    public CurrencyComponent() {}

    public CurrencyComponent(int startingCoins)
    {
        _coins = startingCoins;
        _credits = 0;
    }

    public CurrencyComponent(int startingCoins, int startingCredits)
    {
        _coins = startingCoins;
        _credits = startingCredits;
    }

    // Setters
    public void SetCoins(int _coins) {
        this._coins = _coins;
    }

    public void SetCredits(int _credits) {
        this._credits = _credits;
    }

    // Getters
    public int GetCoins() {
        return _coins;
    }

    public int GetCredits() {
        return _credits;
    }

    // Methods

    public void IncreaseCoins(int coins)
    {
        _coins += coins;
    }

    public void IncreaseCredits(int credits)
    {
        _credits += credits;
    }
    
    @Override
    public String toString() {
        return "Coins: " + _coins + ", Credits: " + _credits;
    }
}
