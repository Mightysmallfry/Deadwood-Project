public class CurrencyComponent {

    //members
    private int _coins;
    private int _credits;

    //constructors
    public CurrencyComponent()
    {
        Set_Coins(0);
        Set_Credits(0);
    }

    public CurrencyComponent(int startingcoins)
    {
        Set_Coins(startingcoins);
        Set_Credits(0);
    }

    public CurrencyComponent(int startingCoins, int startingCredits)
    {
        Set_Coins(startingCoins);
        Set_Credits(startingCredits);
    }

    // Setters
    public void Set_Coins(int _coins) {
        this._coins = _coins;
    }

    public void Set_Credits(int _credits) {
        this._credits = _credits;
    }

    // Getters
    public int Get_Coins() {
        return _coins;
    }

    public int Get_Credits() {
        return _credits;
    }

    // Methods



    //Overide???
    public String toString() {
        return "Coins: " + Get_Coins() + ", Credits: " + Get_Credits();
    }
}
