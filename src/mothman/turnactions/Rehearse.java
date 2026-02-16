package mothman.turnactions;

import mothman.player.*;
import mothman.managers.GameManager;

public class Rehearse implements TurnAction{
    private final int ACTION_COST = 1;

    @Override
    public void Execute() {

        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();

        if (!currentPlayer.HasRole())
        {
            return;
        }

        int rehearseToken = currentPlayer.GetLocation().GetRehearseTokens();
        rehearseToken++;
        currentPlayer.GetLocation().SetRehearseTokens(rehearseToken);

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);
    }
}
