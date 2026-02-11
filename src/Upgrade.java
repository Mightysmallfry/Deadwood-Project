import java.util.ArrayList;
import java.util.Scanner;

public class Upgrade implements TurnAction {
    private Scanner Input = new Scanner(System.in);
    private final int MAX_RANK = 6;

    @Override
    public void execute()
    {
        // Get Player's current location
        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.Get_Location().Get_CurrentGameSet();

        // Ensure that it is the casting Set
        if (!(currentSet instanceof CastingSet))
        {
            System.out.println("You are not at a casting set!");
            return;
        }

        // Figure out what upgrade levels are available to the player
        CastingSet castingSet = (CastingSet) currentSet;

        ArrayList<UpgradeData> upgrades = castingSet.GetUpgrades();
        CurrencyComponent playerMoney = currentPlayer.Get_Currency();

        int playerCoin = playerMoney.Get_Coins();
        int playerCredit = playerMoney.Get_Credits();

        // We'll assume players typically only want to upgrade within 3 levels of their current.
        ArrayList<UpgradeData> possibleCoinUpgrades = new ArrayList<>(3);
        ArrayList<UpgradeData> possibleCreditUpgrades = new ArrayList<>(3);

        for (UpgradeData upgrade : upgrades)
        {
            if (upgrade.GetRank() <= currentPlayer.Get_CurrentRank()) {
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

        // Get Player requested level
            // What currency do they want to pay


            // What rank do they want
        while (true)
        {
            String playerInput = Input.nextLine().strip();
            try {
                int rankRequest = Integer.parseInt(playerInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number between: " +
                        currentPlayer.Get_CurrentRank() + " - " + MAX_RANK);

            }
        }

        // return to action select
    }

}


