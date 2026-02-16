package mothman.turnactions;

import java.util.ArrayList;
import java.util.Scanner;

import mothman.utils.*;
import mothman.player.*;
import mothman.managers.*;
import mothman.sets.*;

public class Upgrade implements TurnAction {
    private Scanner Input = new Scanner(System.in);

    @Override
    public void Execute()
    {
        // Get Player's current location
        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        // Ensure that it is the casting Set
        if (!(currentSet instanceof CastingSet))
        {
            System.out.println("You are not at a casting set!");
            return;
        }

        // Figure out what upgrade levels are available to the player
        CastingSet castingSet = (CastingSet) currentSet;

        ArrayList<UpgradeData> upgrades = castingSet.GetUpgrades();
        CurrencyComponent playerCurrency = currentPlayer.GetCurrency();

        int playerCoin = playerCurrency.GetCoins();
        int playerCredit = playerCurrency.GetCredits();


        System.out.println("Available Upgrades:");

        int currentRank = currentPlayer.GetCurrentRank();
        int maxRank = castingSet.GetMaxRank();

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

            // Only print if at least one currency option exists
            if (dollarCost != null || creditCost != null) {

                System.out.print("Rank " + rank + " | Cost: | ");

                if (creditCost != null) {
                    System.out.print(creditCost + " credits | ");
                }

                if (dollarCost != null) {
                    System.out.print(dollarCost + " dollars | ");
                }

                System.out.println();
            }
        }


        System.out.println("Enter the rank you want to purchase:");
        int rankRequest;

        while (true) {
            try {
                rankRequest = Integer.parseInt(Input.nextLine().strip());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        UpgradeData selectedUpgrade = null;

        for (UpgradeData upgrade : upgrades) {
            if (upgrade.GetRank() == rankRequest) {
                selectedUpgrade = upgrade;
                break;
            }
        }

        if (selectedUpgrade == null) {
            System.out.println("That rank does not exist!");
            return;
        }

        if (rankRequest <= currentPlayer.GetCurrentRank()) {
            System.out.println("You already have this rank or higher!");
            return;
        }

        if (rankRequest > maxRank) {
            System.out.println("That rank does not exist!");
            return;
        }

        //here we ask what payment type
        System.out.println("Please enter payment type. [dollar] [credit]");
        String currencyChoice = Input.nextLine().strip();

        int cost = selectedUpgrade.GetCostAmount();

        switch (currencyChoice) {

            case "dollar":
                if (playerCoin < cost) {
                    System.out.println("You do not have enough dollars!");
                    return;
                }

                playerCurrency.SetCoins(playerCoin - cost);
                currentPlayer.SetCurrentRank(rankRequest);
                System.out.println("Successfully upgraded to Rank " + rankRequest + "!");
                break;

            case "credit":
                if (playerCredit < cost) {
                    System.out.println("You do not have enough credits!");
                    return;
                }

                playerCurrency.SetCredits(playerCredit - cost);
                currentPlayer.SetCurrentRank(rankRequest);
                System.out.println("Successfully upgraded to Rank " + rankRequest + "!");
                break;

            default:
                System.out.println("Invalid currency type.");
        }


        // return to action select
        // Upgrading costs no action points
    }

}


