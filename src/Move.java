import java.util.HashMap;
import java.util.Scanner;

public class Move implements TurnAction{
    private final int ACTION_COST = 1;

    private Scanner _input = new Scanner(System.in);
    private boolean _valid = false;

    @Override
    public void execute() {
        // Get the player's current location

        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = currentPlayer.Get_Location().Get_CurrentGameSet();

        // Get the neighbors of current location
        //TODO: Update upon hashmap neighbors
        //HashMap<String, GameSet> neighbors = currentSet.GetNeighbors();
        HashMap<String, GameSet> neighbors = new HashMap<>();
        System.out.println("Check move, not good neighbors");

        String playerInput = "";
        // Get the player's input
        while (!_valid)
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

            // Good Input
            if (neighbors.containsKey(playerInput))
            {
                _valid = !_valid;
            }
        }

        // Update player location

        // Update new Location
        GameSet targetLocation = currentSet.GetNeighbors().get(playerInput);
        targetLocation.AddPlayer(currentPlayer);

        // Update old Location and Player
        currentSet.GetPlayers().remove(currentPlayer);
        currentPlayer.Get_Location().Set_CurrentGameSet(targetLocation);

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);
    }
}
