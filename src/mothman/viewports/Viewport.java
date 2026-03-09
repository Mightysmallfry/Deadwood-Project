package mothman.viewports;

import mothman.managers.ViewportController;
import mothman.player.Player;
import mothman.sets.ActingRole;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.sets.UpgradeData;
import mothman.utils.TurnDisplayInfo;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: We should consider making this interface smaller if possible
public interface Viewport {
    int[] AskUpgrade(int currentRank, int maxRank, ArrayList<UpgradeData> upgrades);
    String GetName();
    int GetUpgradeRank();
    String GetUpgradeCurrency();
    String GetMove(HashMap<String, GameSet> neighbors, Player player);
    String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info);
    String GetRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles, Player player);
    void DisplayActionList(ArrayList<String> actionList);
    void DisplayMessage(String message); // replaces System.out in TurnActions

    void Update(TurnDisplayInfo info);

    void DealCards(TurnDisplayInfo info);

    void SetController(ViewportController viewportController);

}