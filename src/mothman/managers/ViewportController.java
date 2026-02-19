package mothman.managers;

import mothman.player.Player;
import mothman.sets.*;
import mothman.utils.TurnDisplayInfo;
import mothman.viewports.Viewport;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewportController {

    //Methods
    private Viewport _viewport;

    public ViewportController(Viewport viewport){
        _viewport = viewport;
    }


    // Prompt players for name
    public String AskName(){
        return _viewport.GetName();
    }

    // Prompt player for their actions
    public String AskAction() {
        ArrayList<String> possibleActions = GetActionList();
        TurnDisplayInfo info = BuildTurnInfo();
        String choice = _viewport.GetAction(possibleActions, info);

        // Hidden and always valid commands
        possibleActions.add("force");
        possibleActions.add("end game");

        return choice;
    }

    public String AskRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles) {
        return _viewport.GetRoleSelection(sceneCard, localRoles);
    }

    public int AskUpgradeRank() {
        return _viewport.GetUpgradeRank();
    }

    public String AskUpgradeCurrency() {
        return _viewport.GetUpgradeCurrency();
    }

    public void ShowMessage(String message) {
        _viewport.DisplayMessage(message);
    }

    /**
     *
     * @return A TurnDisplayInfo containing a snapshot of the current Game State
     */
    private TurnDisplayInfo BuildTurnInfo() {
        TurnDisplayInfo info = new TurnDisplayInfo();
        Player player = PlayerManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = player.GetLocation().GetCurrentGameSet();

        info.playerId = player.GetPersonalId();
        info.locationName = currentSet.GetName();
        info.actionTokens = GameManager.GetInstance().GetActionTokens();
        info.isActingSet = currentSet instanceof ActingSet;

        if (currentSet instanceof ActingSet actSet) {
            SceneCard card = actSet.GetCurrentSceneCard();
            info.sceneComplete = (card == null);
            if (card != null) {
                info.budget = card.GetDifficulty();
                info.maxShots = actSet.GetMaxProgress();
                info.currentShots = actSet.GetCurrentProgress();
                if (player.HasRole()) {
                    info.roleLine = player.GetLocation().GetCurrentRole().GetLine();
                }
            }
        }
        return info;
    }

    public void ShowBoard() {
        _viewport.DisplayMessage(PlayerManager.LocatePlayers());
    }

    public String AskMove(HashMap<String, GameSet> neighbors) {
        return _viewport.GetMove(neighbors);
    }

    /**
     * Checks what actions are available to the player and returns
     * them as a list of strings
     * @return Arraylist <String>
     */
    private ArrayList<String> GetActionList()
    {
        ArrayList<String> possibleActions = new ArrayList<>();
        GameSet currentSet = PlayerManager.GetInstance().GetCurrentPlayer().GetLocation().GetCurrentGameSet();
        boolean rolesAvailable = false;
        if (currentSet instanceof ActingSet){
            if (((ActingSet) currentSet).GetCurrentSceneCard() != null){
                rolesAvailable = !((ActingSet) currentSet).GetAvailableRoles().isEmpty();
            }
        }

        // The player is always allowed to:
        // - quit
        // - pass turn to next
        // - location ask where they are
        // - who are they
        // - board where is everyone?
        possibleActions.add("quit");
        possibleActions.add("pass");
        possibleActions.add("profile");
        possibleActions.add("board");

        if (!PlayerManager.GetInstance().GetCurrentPlayer().HasRole() &&
                GameManager.GetInstance().GetActionTokens() >= 0 &&
                rolesAvailable){
            // Acquire
            possibleActions.add("acquire");
        }

        // Check if the player has a valid role
        if (PlayerManager.GetInstance().GetCurrentPlayer().HasRole() &&
                GameManager.GetInstance().GetActionTokens() > 0 &&
                ((ActingSet)currentSet).GetCurrentSceneCard() != null){
            // Act
            possibleActions.add("act");
            // Rehearse
            possibleActions.add("rehearse");

        } else {
            if (GameManager.GetInstance().GetActionTokens() > 0) {
                // Move
                possibleActions.add("move");
            }
            if (PlayerManager.GetInstance().GetCurrentPlayer().GetLocation().GetCurrentGameSet() instanceof CastingSet) {
                // Upgrade
                possibleActions.add("upgrade");
            }
        }

        return possibleActions;
    }

}
