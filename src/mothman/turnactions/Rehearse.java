package mothman.turnactions;

import mothman.managers.PlayerManager;
import mothman.player.*;
import mothman.managers.GameManager;

public class Rehearse implements TurnAction{
    private final int ACTION_COST = 1;

    @Override
    public void Execute() {

        Player currentPlayer = PlayerManager.GetInstance().GetCurrentPlayer();

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
