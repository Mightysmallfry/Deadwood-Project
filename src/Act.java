public class Act implements TurnAction{
    private final int ACTION_COST = 1;

    @Override
    public void Execute() {

        Player currentPlayer = GameManager.GetInstance().GetCurrentPlayer();

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

        boolean success = attempt >= currentCard.GetDifficulty();
        // On success increment both

        //added creation of PlayerManager to pay the players
        PlayerManager manager = new PlayerManager();

        if (success)
        {
            currentSet.SetCurrentProgress(currentSet.GetCurrentProgress() + 1);
            for (Player player : currentSet.GetPlayers())
            {
                manager.BasicPay(player, true);
            }
            if (currentSet.IsComplete()) {
                manager.BonusPay(currentPlayer);
            }
        } else {
            for (Player player : currentSet.GetPlayers())
            {
                manager.BasicPay(player, false);
            }
        }

        int actionTokens = GameManager.GetInstance().GetActionTokens();
        actionTokens -= ACTION_COST;
        GameManager.GetInstance().SetActionTokens(actionTokens);

    }
}
