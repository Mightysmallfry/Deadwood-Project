package mothman.viewports;

import mothman.sets.ActingRole;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.utils.TurnDisplayInfo;

import java.util.ArrayList;
import java.util.HashMap;

    // TODO: We should consider making this interface smaller if possible
public interface Viewport {
    String GetName();
    int GetUpgradeRank();
    String GetUpgradeCurrency();
    String GetMove(HashMap<String, GameSet> neighbors);
    String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info);
    String GetRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles);
    void DisplayActionList(ArrayList<String> actionList);
    void DisplayMessage(String message); // replaces System.out in TurnActions
    void DealCards(TurnDisplayInfo info);
    void UpdateCardDisplay(TurnDisplayInfo info);
}
