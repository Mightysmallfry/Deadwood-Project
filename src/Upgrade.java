import java.util.ArrayList;
import java.util.Scanner;

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

        // We'll assume players typically only want to upgrade within 3 levels of their current.
        // that way we're not too greedy or frugal with resources
        ArrayList<UpgradeData> possibleCoinUpgrades = new ArrayList<>(3);
        ArrayList<UpgradeData> possibleCreditUpgrades = new ArrayList<>(3);

        for (UpgradeData upgrade : upgrades)
        {
            if (upgrade.GetRank() <= currentPlayer.GetCurrentRank()) {
                continue;
            }

            switch (upgrade.GetCurrencyType())
            {
                case "dollar":
                    if (upgrade.GetCostAmount() <= playerCoin) {
                        possibleCoinUpgrades.add(upgrade);
                    }
                    break;

                case "credit":
                    if (upgrade.GetCostAmount() <= playerCredit) {
                        possibleCreditUpgrades.add(upgrade);
                    }
                    break;
            }
        }

        // Display the options that the player can afford
        for (UpgradeData upgrade : possibleCoinUpgrades){
            System.out.println(upgrade.toString());
        }

        for (UpgradeData upgrade : possibleCreditUpgrades){
            System.out.println(upgrade.toString());
        }

        // Get Player requested level
            // What currency do they want to pay
            // Assume minimum level
        String currencyChoice = Input.nextLine().strip();
        int rankRequest = 1;

        // What rank do they want
        while (true)
        {
            String playerInput = Input.nextLine().strip();
            try {
                rankRequest = Integer.parseInt(playerInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }

        int maxRank = castingSet.GetMaxRank();

        switch (currencyChoice){
            case "dollar":
                for (UpgradeData upgrade : possibleCoinUpgrades)
                {
                    if (1 < rankRequest && rankRequest < maxRank && rankRequest == upgrade.GetRank()) {
                        int newBalance = playerCurrency.GetCoins() - upgrade.GetCostAmount();
                        playerCurrency.SetCoins(newBalance);
                    }
                }
                break;

            case "credit":
                for (UpgradeData upgrade : possibleCreditUpgrades)
                {
                    if (1 < rankRequest && rankRequest < maxRank && rankRequest == upgrade.GetRank()) {
                        int newBalance = playerCurrency.GetCredits() - upgrade.GetCostAmount();
                        playerCurrency.SetCredits(newBalance);
                    }
                }
                break;
        }

        // return to action select
        // Upgrading costs no action points
    }

}


