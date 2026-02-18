package mothman.turnactions;

import mothman.player.*;
import mothman.managers.*;
import mothman.sets.*;
import mothman.utils.*;

public class Act implements TurnAction{
    private final int ACTION_COST = 1;

    @Override
    public void Execute() {

        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();

        // Pre-conditions
        if (!currentPlayer.HasRole()) {
            System.out.println("You don't have a role yet!");
            return;
        }

        if (!(currentPlayer.GetLocation().GetCurrentGameSet() instanceof ActingSet)){
            System.out.println("You need to be on an acting set first!");
        }

        // Let's just make it
        // We'll assume the player is on an acting set
        ActingSet currentSet = (ActingSet) currentPlayer.GetLocation().GetCurrentGameSet();
        SceneCard currentCard = currentSet.GetCurrentSceneCard();

        // Roll 1d6 against difficulty
        int attempt = Dice.GetInstance().Roll();
        System.out.println("You rolled: " + attempt + " with a " + currentPlayer.GetLocation().GetRehearseTokens() + " bonus");
        attempt = attempt + currentPlayer.GetLocation().GetRehearseTokens();

        boolean success = attempt >= currentCard.GetDifficulty();

        // On success increment both

        //added creation of PlayerManager to pay the players

        if (success)//check for the scene breaking
        {
            currentSet.SetCurrentProgress(currentSet.GetCurrentProgress() + 1);
            PlayerManager.GetInstance().BasicPay(currentPlayer, true);

            if (currentSet.IsComplete()) {//This makes sense
                PlayerManager.GetInstance().BasicPay(currentPlayer, true);
                PlayerManager.GetInstance().BonusPay(currentPlayer);
                PlayerManager.GetInstance().PostSceneReset(currentPlayer);
                currentSet.RemoveCard();
            }
        } else {
            PlayerManager.GetInstance().BasicPay(currentPlayer, false);
        }

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);

    }
}
