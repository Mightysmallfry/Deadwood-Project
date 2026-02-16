package mothman.player;

import mothman.sets.*;

public class Player {
    // Statics

    // Members
    private LocationComponent _location = new LocationComponent();
    private CurrencyComponent _currency = new CurrencyComponent();
    // Name
    private String _personalId = "";
    private int _score = 0;
    private int _currentRank = 1;

    // Constructors
    public Player() {}

    public Player(String personalId)
    {
        _personalId = personalId;
    }

    public Player(String personalId, int startingRank)
    {
        _personalId = personalId;
        _currentRank = startingRank;
    }

    public Player(String personalId, int startingRank, LocationComponent location)
    {
        _personalId = personalId;
        _currentRank = startingRank;
        _location = location;
    }

    // Setter start
    public void SetCurrency(CurrencyComponent currencyComponent) {
        _currency = currencyComponent;
    }

    public void SetPersonalId(String personalId) {
        _personalId = personalId;
    }

    public void SetLocation(LocationComponent locationComponent) {
        _location = locationComponent;
    }

    public void SetCurrentRank(int rank) {
        _currentRank = rank;
    }

    public void SetScore()
    {
        CurrencyComponent currency = GetCurrency();
        this._score = ((GetCurrentRank() * 5) + currency.GetCoins() + currency.GetCredits());
    }

    public void SetScore(int _score){this._score = _score;}

    // Getter start
    public CurrencyComponent GetCurrency() {
        return _currency;
    }

    public String GetPersonalId() {
        return _personalId;
    }

    public int GetCurrentRank() {
        return _currentRank;
    }

    public int GetScore() {
        return _score;
    }

    public LocationComponent GetLocation() {
        return _location;
    }

    // Methods
    public boolean HasRole(ActingRole role)
    {

        ActingRole currentRole = GetLocation().GetCurrentRole();
        return currentRole != null && currentRole.equals(role);
    }

    public boolean HasRole() {
        return GetLocation().GetCurrentRole() != null;
    }

    public static String GetProfileString(Player player){
        GameSet currentSet = player.GetLocation().GetCurrentGameSet();
        StringBuilder sb = new StringBuilder();

        sb.append("[").append(player.GetPersonalId()).append("]\n");
        sb.append("> RANK: ").append(player.GetCurrentRank()).append("\n");
        sb.append("> COIN: ").append(player.GetCurrency().GetCoins()).append("\n");
        sb.append("> CRED: ").append(player.GetCurrency().GetCredits()).append("\n");
        sb.append("> LOC: ").append(currentSet.GetName()).append("\n");


        if (currentSet instanceof ActingSet)
        {
            SceneCard card = ((ActingSet) currentSet).GetCurrentSceneCard();

            if (card != null) {
                sb.append("> SCN: ").append(card.GetName()).append("\n");
            }

            int rehearseTokens = player.GetLocation().GetRehearseTokens();
            sb.append("> REH: ").append(rehearseTokens).append("\n");
        }

        if (player.HasRole())
        {
            sb.append("> ROL: ").append(player.GetLocation().GetCurrentRole().GetName());
        }
        return sb.toString();
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Player { ");
        sb.append(_personalId);

        sb.append(" <Score>: ");
        sb.append(_score);

        sb.append(" <rank>: ");
        sb.append(_currentRank);

        sb.append(" <coins>: ");
        sb.append(_currency != null ? _currency.GetCoins() : "null");

        sb.append(" <credits>: ");
        sb.append(_currency != null ? _currency.GetCredits() : "null");

        sb.append(" <location>: ");
        sb.append(_location != null ? _location.GetCurrentGameSet().GetName() : "null");

        sb.append("}");

        return sb.toString();
    }
}