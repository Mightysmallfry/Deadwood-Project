package mothman.viewports;

import mothman.gui.ActionMenuPanel;
import mothman.utils.TurnDisplayInfo;

public class MenuLayer implements ViewLayer {

    private ActionMenuPanel menuPanel;

    public MenuLayer(ActionMenuPanel menuPanel) {
        this.menuPanel = menuPanel;
    }

    @Override
    public void update(TurnDisplayInfo info) {
        menuPanel.update(info);
    }

    public String getAction() {
        return menuPanel.getAction();
    }

    public int getRoleSelection() {
        return menuPanel.getRoleSelection();
    }

    public boolean getUpgradeCurrent() {
        return menuPanel.getUpgradeCurrent();
    }

}