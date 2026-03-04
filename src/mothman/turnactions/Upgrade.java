package mothman.turnactions;

import java.util.ArrayList;

import mothman.utils.*;
import mothman.player.*;
import mothman.managers.*;
import mothman.sets.*;

public class Upgrade implements TurnAction {

    @Override
    public void Execute(ViewportController vc)
    {
        // Get Player's current location
        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        if (!(currentSet instanceof CastingSet))
        {
            vc.ShowMessage("You are not at a casting set!");
            return;
        }

        CastingSet castingSet = (CastingSet) currentSet;
        ArrayList<UpgradeData> upgrades = castingSet.GetUpgrades();
        CurrencyComponent playerCurrency = currentPlayer.GetCurrency();

        int currentRank = currentPlayer.GetCurrentRank();
        int maxRank     = castingSet.GetMaxRank();
        int[] result = vc.AskUpgrade(currentRank, maxRank, upgrades);
        if (result == null) return; // player cancelled

        int rankRequest = result[0];
        int cost        = result[1];
        String currencyChoice = resolveCurrency(upgrades, rankRequest, cost);
        if (rankRequest <= currentRank) {
            vc.ShowMessage("You already have this rank or higher!");
            return;
        }

        if (rankRequest > maxRank) {
            vc.ShowMessage("That rank does not exist!");
            return;
        }

        // --- Process payment ---
        switch (currencyChoice) {

            case "dollar":
                if (playerCurrency.GetCoins() < cost) {
                    vc.ShowMessage("You do not have enough dollars!");
                    return;
                }
                playerCurrency.SetCoins(playerCurrency.GetCoins() - cost);
                currentPlayer.SetCurrentRank(rankRequest);
                vc.ShowMessage("Successfully upgraded to Rank " + rankRequest + "!");
                break;

            case "credit":
                if (playerCurrency.GetCredits() < cost) {
                    vc.ShowMessage("You do not have enough credits!");
                    return;
                }
                playerCurrency.SetCredits(playerCurrency.GetCredits() - cost);
                currentPlayer.SetCurrentRank(rankRequest);
                vc.ShowMessage("Successfully upgraded from Rank " + currentRank + " to Rank " + rankRequest + "!");
                break;

            default:
                vc.ShowMessage("Invalid currency type.");
        }
    }

    /**
     * Derives the currency string from the upgrades list by matching rank and cost.
     * This avoids prompting the player a second time since AskUpgrade already captured it.
     */
    private String resolveCurrency(ArrayList<UpgradeData> upgrades, int rank, int cost) {
        for (UpgradeData u : upgrades) {
            if (u.GetRank() == rank && u.GetCostAmount() == cost) {
                return u.GetCurrencyType();
            }
        }
        return "dollar"; // safe default
    }
}