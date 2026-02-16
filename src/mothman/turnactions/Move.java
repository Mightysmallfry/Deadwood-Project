package mothman.turnactions;

import java.util.HashMap;
import java.util.Scanner;

import mothman.sets.*;
import mothman.player.*;
import mothman.managers.GameManager;

public class Move implements TurnAction{
    //TODO: Can the player acquire a role after and before moving?
    // If so change the action cost to 0;
    private final int ACTION_COST = 1;

    private Scanner _input = new Scanner(System.in);
    private boolean _validInput = false;

    @Override
    public void Execute() {
        // If you move once you are done.
        if (GameManager.GetInstance().HasMoved()) {
            return;
        }

        // Get the player's current location
        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.GetLocation().GetCurrentGameSet();

        // Get the neighbors of current location
        HashMap<String, GameSet> neighbors = currentSet.GetNeighbors();
        String playerInput = "";
        // Get the player's input
        while (!_validInput)
        {
            // Print Available Options
            System.out.print("Available Locations: ");
            for (HashMap.Entry<String, GameSet> entry : neighbors.entrySet()) {
                System.out.print("[" + entry.getKey() + "] ");
            }

            System.out.println("or [cancel]");

            // Check validity
            playerInput = _input.nextLine().strip();

            if (playerInput.equals("cancel")) {
                return;
            }

            if (playerInput.equals(currentSet.GetName())){
                System.out.println("! You're already there!");
                return;
            }

            boolean foundLocation = neighbors.containsKey(playerInput);
            if (!foundLocation) {
                System.out.println("! Target location out of reach, please try again!");
            }
            if (foundLocation)
            {
                _validInput = !_validInput;
                System.out.println("! Moving current player!");
            }
        }

        // Update player location

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
