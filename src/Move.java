import java.util.HashMap;
import java.util.Scanner;

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
        //TODO: Update upon hashmap neighbors
        //HashMap<String, GameSet> neighbors = currentSet.GetNeighbors();
        HashMap<String, GameSet> neighbors = new HashMap<>();
        System.out.println("Check move, not good neighbors");

        String playerInput = "";
        // Get the player's input
        while (!_validInput)
        {
            // Check validity
            playerInput = _input.nextLine().toLowerCase().strip();
            if (playerInput.equals(currentSet.GetName())){
                System.out.println("You're already there!");
                return;
            }

            boolean foundLocation = neighbors.containsKey(playerInput);
            if (!foundLocation) {
                System.out.println("Target location out of reach, please try again!");
            }

            // Got Good Input
            if (neighbors.containsKey(playerInput))
            {
                _validInput = !_validInput;
            }
        }

        // Update player location

        // Update new Location
        GameSet targetLocation = currentSet.GetNeighbors().get(playerInput);
        targetLocation.AddPlayer(currentPlayer);

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
