public class Acquire implements TurnAction {
    private final int ACTION_COST = 1;

    @Override
    public void Execute() {
        // Already has a role
        if (GameManager.GetInstance().GetCurrentPlayer().GetLocation().GetCurrentRole() != null)
        {
            return;
        }

        // Get Available roles

        // Get Player Selection

        // Set Player Choice

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);
    }
}
