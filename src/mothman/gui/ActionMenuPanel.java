package mothman.gui;

import mothman.sets.UpgradeData;
import mothman.utils.TurnDisplayInfo;

import javax.swing.*;
import java.util.ArrayList;

public class ActionMenuPanel extends JPanel {

    public ActionMenuPanel() {
    }

    public void update(TurnDisplayInfo info) {
        // TODO: update buttons based on available actions
    }

    public String getAction() {
        // TODO: return selected action
        return null;
    }

    public int getRoleSelection() {
        // TODO: return selected role
        return -1;
    }

    public boolean getUpgradeCurrent() {
        // TODO: return upgrade selection state
        return false;
    }

    public String[] ShowUpgradeMenu(int currentRank, int maxRank, ArrayList<UpgradeData> upgrades) {
        // TODO: build GUI upgrade menu

        // returning null means cancelled
        return null;
    }
}