package mothman.turnactions;

import java.util.ArrayList;
import java.util.Scanner;

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

        // Ensure that it is the casting Set
        if (!(currentSet instanceof CastingSet))
        {
            vc.ShowMessage("You are not at a casting set!");
            return;
        }

        // Figure out what upgrade levels are available to the player
        CastingSet castingSet = (CastingSet) currentSet;
        ArrayList<UpgradeData> upgrades = castingSet.GetUpgrades();
        CurrencyComponent playerCurrency = currentPlayer.GetCurrency();

        int playerCoin = playerCurrency.GetCoins();
        int playerCredit = playerCurrency.GetCredits();

        int currentRank = currentPlayer.GetCurrentRank();
        int maxRank = castingSet.GetMaxRank();

        vc.ShowMessage("Your current rank is: " + currentRank);
        vc.ShowMessage("Available Upgrades:");
        for (int rank = currentRank + 1; rank <= maxRank; rank++) {

            Integer dollarCost = null;
            Integer creditCost = null;

            // Find both costs for this rank
            for (UpgradeData upgrade : upgrades) {

                if (upgrade.GetRank() == rank) {

                    if (upgrade.GetCurrencyType().equals("dollar")) {
                        dollarCost = upgrade.GetCostAmount();
                    }

                    if (upgrade.GetCurrencyType().equals("credit")) {
                        creditCost = upgrade.GetCostAmount();
                    }
                }
            }

            //Redone to work with new implementation
            if (dollarCost != null || creditCost != null) {
                StringBuilder sb = new StringBuilder("Rank " + rank + " | Cost: | ");
                if (creditCost != null) sb.append(creditCost + " credits | ");
                if (dollarCost != null) sb.append(dollarCost + " dollars | ");
                vc.ShowMessage(sb.toString());
            }
        }

        int rankRequest = vc.AskUpgradeRank();

        UpgradeData selectedUpgrade = null;
        for (UpgradeData upgrade : upgrades) {
            if (upgrade.GetRank() == rankRequest) {
                selectedUpgrade = upgrade;
                break;
            }
        }

        if (selectedUpgrade == null) {
            vc.ShowMessage("That rank does not exist!");
            return;
        }

        if (rankRequest <= currentPlayer.GetCurrentRank()) {
            vc.ShowMessage("You already have this rank or higher!");
            return;
        }

        if (rankRequest > maxRank) {
            vc.ShowMessage("That rank does not exist!");
            return;
        }

        //here we ask what payment type
        String currencyChoice = vc.AskUpgradeCurrency();
        int cost = selectedUpgrade.GetCostAmount();

        switch (currencyChoice) {

            case "dollar":
                if (playerCoin < cost) {
                    vc.ShowMessage("You do not have enough dollars!");
                    return;
                }

                playerCurrency.SetCoins(playerCoin - cost);
                currentPlayer.SetCurrentRank(rankRequest);
                vc.ShowMessage("Successfully upgraded to Rank " + rankRequest + "!");
                break;

            case "credit":
                if (playerCredit < cost) {
                    vc.ShowMessage("You do not have enough credits!");
                    return;
                }

                playerCurrency.SetCredits(playerCredit - cost);
                currentPlayer.SetCurrentRank(rankRequest);
                vc.ShowMessage("Successfully upgraded from rank " + currentRank + " to Rank"  + rankRequest + "!");
                break;

            default:
                vc.ShowMessage("Invalid currency type.");
        }


        // return to action select
        // Upgrading costs no action points
    }

}


