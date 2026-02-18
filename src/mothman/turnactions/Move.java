package mothman.turnactions;

import java.util.HashMap;

import mothman.managers.PlayerManager;
import mothman.managers.ViewportController;
import mothman.sets.*;
import mothman.player.*;
import mothman.managers.GameManager;

public class Move implements TurnAction{
    private final int ACTION_COST = 1;
    private boolean _validInput = false;

    @Override
    public void Execute(ViewportController vc) {
        // If you move once you are done moving for the turn.
        if (GameManager.GetInstance().HasMoved()) {
            vc.ShowMessage("You've already moved this turn!");
            return;
        }

        // Get the player's current location
        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();
        HashMap<String, GameSet> neighbors = currentSet.GetNeighbors();
        String playerInput = "";

        // Get the player's input
        while (!_validInput)
        {
            playerInput = vc.AskMove(neighbors);
            // Check validity

            if (playerInput.equals("cancel")) {
                return;
            }

            if (playerInput.equals(currentSet.GetName())){
                vc.ShowMessage("! You're already there!");
                return;
            }

            boolean foundLocation = neighbors.containsKey(playerInput);
            if (!foundLocation) {
                vc.ShowMessage("! Target location out of reach, please try again!");
            }
            if (foundLocation)
            {
                _validInput = !_validInput;

                vc.ShowMessage("! Moving current player from " + currentSet.GetName() + " to " + playerInput + "!");
            }
        }

        // Update new Location
        GameSet targetLocation = currentSet.GetNeighbors().get(playerInput);
        targetLocation.AddPlayer(currentPlayer);

        if (targetLocation instanceof ActingSet)
        {
            SceneCard card = ((ActingSet) targetLocation).GetCurrentSceneCard();

            // If not yet visible, make it now visible
            if (card != null && !card.IsVisible()){
                card.SetVisible(true);
            }
        }

        // Update old Location and Player
        currentSet.GetPlayers().remove(currentPlayer);
        currentPlayer.GetLocation().SetCurrentGameSet(targetLocation);

        // Update the action economy.
        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);

        // Make it so we can't move after this
        GameManager.GetInstance().HasMoved(true);
    }
}
