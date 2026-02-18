package mothman.turnactions;

import java.util.ArrayList;
import java.util.Scanner;
import mothman.managers.*;
import mothman.player.*;
import mothman.sets.*;

public class Acquire implements TurnAction {
    // Acquiring a role should not cost anything
    private final int ACTION_COST = 0;

    private Scanner _input = new Scanner(System.in);

    @Override
    public void Execute() {
        // Already has a role
        if (PlayerManager.GetInstance().GetCurrentPlayer().GetLocation().GetCurrentRole() != null)
        {
            return;
        }

        // Get Player and Location
        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        // Get Available roles
        if (!(currentSet instanceof ActingSet)) {
            System.out.println("You need to be on an acting set to get a job!");
            return;
        }

        ActingSet actingSet = (ActingSet) currentSet;
        ArrayList<ActingRole> availableRoles = actingSet.GetAvailableRoles();

        if (availableRoles.size() == 0) {
            System.out.println("There are no available roles, better luck next time!");
            return;
        }
        SceneCard sceneCard = actingSet.GetCurrentSceneCard();

        // Get Player Selection
        String playerChoice = "";
        ActingRole chosenRole = null;

        boolean choosingRole = true;
        while (choosingRole)
        {
            System.out.println("Active Scene: " + sceneCard.GetName());

            System.out.print("Available Starring Roles:");
            for (ActingRole role : sceneCard.GetAvailableRoles()) {
                System.out.print("[" + role.GetName() + ", rank: " + role.GetRank() + "] ");
            }
            System.out.println();

            System.out.print("Available Extra Roles: ");
            for (ActingRole role : actingSet.GetAvailableLocalRoles()) {
                System.out.print("[" + role.GetName() + ", rank: " + role.GetRank() + "] ");
            }
            System.out.println("");
            System.out.println("[cancel] at anytime");

            playerChoice = _input.nextLine().strip();

            if (playerChoice.equals("cancel")) {
                return;
            }

            for (ActingRole role : availableRoles) {
                if (playerChoice.equals(role.GetName())) {

                    if (currentPlayer.GetCurrentRank() >= role.GetRank()){
                        choosingRole = false;
                        chosenRole = role;
                    }
                    else {
                        System.out.println("! You're rank is too low. Choose another role!");
                    }
                }
            }
        }


        //Role test now displays at the start of your turn if you have a role
        //it was changed in game manager

        // Change Player location role
        currentPlayer.GetLocation().SetCurrentRole(chosenRole);

        // Register with scene catalog
        // Check if the role is on a card
        if (actingSet.GetLocalRoles().contains(chosenRole)) {
            currentPlayer.GetLocation().SetOnCard(false);
            actingSet.GetRoleCatalog().put(chosenRole, currentPlayer);
        } else {
            currentPlayer.GetLocation().SetOnCard(true);
            sceneCard.GetRoleCatalog().put(chosenRole, currentPlayer);
        }

        // Process end of action
        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);
    }
}
