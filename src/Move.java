import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Move implements TurnAction{
    private Scanner Input = new Scanner(System.in);
    private boolean _valid = false;

    @Override
    public void execute() {
        // Get the player's current location

        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();
        GameSet currentLocation = currentPlayer.Get_Location().Get_CurrentGameSet();

        // Get the neighbors of current location
        HashMap<String, GameSet> neighbors = currentLocation.GetNeighbors();

        // Get the player's input
        while (!_valid)
        {
            // Check validity
            String playerInput = Input.nextLine().toLowerCase().strip();
            if (playerInput.equals(currentLocation.GetName())){
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
        //  Accounting for both locationComponent and both GameSets involved

        // return to action select
    }
}
