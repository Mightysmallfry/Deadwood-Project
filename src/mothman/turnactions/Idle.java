package mothman.turnactions;

import mothman.managers.ViewportController;

public class Idle implements TurnAction{

    @Override
    public void Execute(ViewportController vc) {
        // Do nothing as this is our basic idle state
    }
}
