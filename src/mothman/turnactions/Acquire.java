package mothman.turnactions;

import java.util.ArrayList;
import mothman.managers.*;
import mothman.player.*;
import mothman.sets.*;

public class Acquire implements TurnAction {
    // Acquiring a role should not cost anything
    private final int ACTION_COST = 0;

    @Override
    public void Execute(ViewportController viewportController) {
        // Already has a role
        if (PlayerManager.GetInstance().GetCurrentPlayer().GetLocation().GetCurrentRole() != null)
        {
            return;
        }

        // Get Player and Location
        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        // Get Available roles and convert currentSet to an acting set
        if (!(currentSet instanceof ActingSet actingSet)) {
            viewportController.ShowMessage("You need to be on an acting set to get a job!");
            return;
        }

        ArrayList<ActingRole> availableRoles = actingSet.GetAvailableRoles();

        if (availableRoles.isEmpty()) {
            viewportController.ShowMessage("There are no available roles, better luck next time!");
            return;
        }
        SceneCard sceneCard = actingSet.GetCurrentSceneCard();

        // Get Player Selection
        ActingRole chosenRole = null;

        boolean choosingRole = true;
        while (choosingRole)
        {
           String playerChoice = viewportController.AskRoleSelection(sceneCard, actingSet.GetAvailableLocalRoles());

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
                        viewportController.ShowMessage("! You're rank is too low. Choose another role!");
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
