package mothman.turnactions;

import mothman.player.*;
import mothman.managers.*;
import mothman.sets.*;
import mothman.utils.*;
import mothman.viewports.ViewportGui;

public class Act implements TurnAction {
    private final int ACTION_COST = 1;

    @Override
    public void Execute(ViewportController vc) {

        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();

        if (!currentPlayer.HasRole()) {
            vc.ShowMessage("You don't have a role yet!");
            return;
        }

        if (!(currentPlayer.GetLocation().GetCurrentGameSet() instanceof ActingSet)) {
            vc.ShowMessage("You need to be on an acting set first!");
            return;
        }

        ActingSet currentSet = (ActingSet) currentPlayer.GetLocation().GetCurrentGameSet();
        SceneCard currentCard = currentSet.GetCurrentSceneCard();

        // Roll 1d6 against difficulty
        int attempt = Dice.GetInstance().Roll();
        vc.ShowMessage("You rolled: " + attempt + " with a " + currentPlayer.GetLocation().GetRehearseTokens() + " bonus");
        attempt += currentPlayer.GetLocation().GetRehearseTokens();

        boolean success = attempt >= currentCard.GetDifficulty();

        PlayerManager playerManager = PlayerManager.GetInstance();

        if (success) {
            currentSet.SetCurrentProgress(currentSet.GetCurrentProgress() + 1);
            playerManager.BasicPay(currentPlayer, true);

            if (currentSet.IsComplete()) {
                playerManager.BasicPay(currentPlayer, true);
                playerManager.BonusPay(currentPlayer);
                playerManager.PostSceneReset(currentPlayer);
            }
        } else {
            playerManager.BasicPay(currentPlayer, false);
            vc.ShowMessage("Act failed, better luck next time!");
        }

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        GameManager.GetInstance().SetActionTokens(actionTokens - ACTION_COST);
    }
}