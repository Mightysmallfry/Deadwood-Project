import java.util.ArrayList;
import java.util.Scanner;

public class Acquire implements TurnAction {
    private final int ACTION_COST = 1;

    private Scanner _input = new Scanner(System.in);

    @Override
    public void Execute() {
        // Already has a role
        if (GameManager.GetInstance().GetCurrentPlayer().GetLocation().GetCurrentRole() != null)
        {
            return;
        }

        // Get Available roles
        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        if (!(currentSet instanceof ActingSet)) {
            System.out.println("You need to be on an acting set to get a job!");
            return;
        }

        ActingSet actingSet = (ActingSet) currentSet;
        ArrayList<ActingRole> availableRoles = actingSet.GetAvailableRoles();

        if (availableRoles.size() == 0) {
            System.out.println("There are no available roles, good luck next time!");
            return;
        }

        // Display available roles
        // Do we want a simple display class that's a singleton?
        // Used for printing any list or thing to the screen?
        // Seems a bit overkill

        // Get Player Selection
        String playerChoice = "";
        ActingRole chosenRole = null;

        boolean choosingRole = true;
        while (choosingRole)
        {
            playerChoice = _input.nextLine().strip();

            for (ActingRole role : availableRoles) {
                if (playerChoice.equals(role.GetName())) {
                    choosingRole = false;
                    chosenRole = role;
                }
            }
        }

        // Register with scene catalog
        actingSet.GetRoleCatalog().put(chosenRole, currentPlayer);

        // Change Player location role
        currentPlayer.GetLocation().SetCurrentRole(chosenRole);

        //TODO: I don't think we should have this method's existence
        currentPlayer.SetRole(chosenRole);

        // Check if the role is on a card
        if (actingSet.GetLocalRoles().contains(chosenRole)) {
            currentPlayer.GetLocation().SetOnCard(false);
        } else {
            currentPlayer.GetLocation().SetOnCard(true);
        }

        // Process end of action
        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);
    }
}
