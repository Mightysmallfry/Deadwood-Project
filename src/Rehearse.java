public class Rehearse implements TurnAction{
    private final int ACTION_COST = 1;

    @Override
    public void execute() {

        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();

        if (!currentPlayer.HasRole())
        {
            return;
        }

        int rehearseToken = currentPlayer.Get_Location().Get_RehearseTokens();
        rehearseToken++;
        currentPlayer.Get_Location().Set_RehearseTokens(rehearseToken);

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);
    }
}
