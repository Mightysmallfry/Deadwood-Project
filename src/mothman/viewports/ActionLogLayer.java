package mothman.viewports;

import mothman.gui.ActionLogPanel;
import mothman.utils.TurnDisplayInfo;

public class ActionLogLayer implements ViewLayer {

    private ActionLogPanel actionLogPanel;

    public ActionLogLayer(ActionLogPanel actionLogPanel) {
        this.actionLogPanel = actionLogPanel;
    }

    @Override
    public void update(TurnDisplayInfo info) {
        actionLogPanel.Update();
    }

    public void addToLog(String message) {
        actionLogPanel.AddToLog(message);
    }
}
