package mothman.managers;

import mothman.player.Player;
import mothman.sets.*;
import mothman.utils.TurnDisplayInfo;
import mothman.viewports.Viewport;
import mothman.viewports.ViewportGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewportController {

    private Viewport _viewport;

    public ViewportController(Viewport viewport) {
        _viewport = viewport;
    }

    public Viewport GetViewport() { return _viewport; }

    public String AskName() {
        return _viewport.GetName();
    }

    public String AskAction() {
        ArrayList<String> possibleActions = GetActionList();
        TurnDisplayInfo info = BuildTurnInfo();
        String choice = _viewport.GetAction(possibleActions, info);

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

    /**
     * Handles the full upgrade interaction — rank then currency — routing to the
     * GUI panel flow or text fallback depending on the active viewport.
     *
     * @return int[]{ rank, cost } for the chosen upgrade, or null if cancelle.
     */
    public int[] AskUpgrade(int currentRank, int maxRank, ArrayList<UpgradeData> upgrades) {
        int    rankRequest;
        String currencyChoice;

        if (_viewport instanceof ViewportGui gui) {
            String[] result = gui.ShowUpgradeMenu(currentRank, maxRank, upgrades);
            if (result == null) return null;
            rankRequest    = Integer.parseInt(result[0]);
            currencyChoice = result[1];
        } else {
            ShowMessage("Your current rank is: " + currentRank);
            ShowMessage("Available Upgrades:");
            for (int rank = currentRank + 1; rank <= maxRank; rank++) {
                Integer dollarCost = null;
                Integer creditCost = null;
                for (UpgradeData u : upgrades) {
                    if (u.GetRank() == rank) {
                        if ("dollar".equals(u.GetCurrencyType())) dollarCost = u.GetCostAmount();
                        if ("credit".equals(u.GetCurrencyType())) creditCost = u.GetCostAmount();
                    }
                }
                if (dollarCost != null || creditCost != null) {
                    StringBuilder sb = new StringBuilder("Rank " + rank + " | Cost: | ");
                    if (creditCost != null) sb.append(creditCost).append(" credits | ");
                    if (dollarCost != null) sb.append(dollarCost).append(" dollars | ");
                    ShowMessage(sb.toString());
                }
            }
            rankRequest    = AskUpgradeRank();
            currencyChoice = AskUpgradeCurrency();
        }
        for (UpgradeData u : upgrades) {
            if (u.GetRank() == rankRequest && u.GetCurrencyType().equals(currencyChoice)) {
                return new int[]{ rankRequest, u.GetCostAmount() };
            }
        }
        return null;
    }

    public void ShowMessage(String message) {
        _viewport.DisplayMessage(message);
    }

    public void ShowBoard() {
        _viewport.DisplayMessage(PlayerManager.LocatePlayers());
    }

    public String AskMove(HashMap<String, GameSet> neighbors) {
        return _viewport.GetMove(neighbors);
    }

    // Internal helpers
    /**
     * Builds a snapshot of the current game state for the viewport to display.
     * cardImageName and cardArea are populated here so ViewportGui can place
     * the scene card image on the board at the correct position.
     */
    private TurnDisplayInfo BuildTurnInfo() {
        TurnDisplayInfo info = new TurnDisplayInfo();
        Player player = PlayerManager.GetInstance().GetCurrentPlayer();
        GameSet currentSet = player.GetLocation().GetCurrentGameSet();

        info.playerId     = player.GetPersonalId();
        info.locationName = currentSet.GetName();
        info.actionTokens = GameManager.GetInstance().GetActionTokens();
        info.isActingSet  = currentSet instanceof ActingSet;

        if (currentSet instanceof ActingSet actSet) {
            SceneCard card = actSet.GetCurrentSceneCard();
            info.sceneComplete = (card == null);
            if (card != null) {
                info.budget        = card.GetDifficulty();
                info.maxShots      = actSet.GetMaxProgress();
                info.currentShots  = actSet.GetCurrentProgress();
                info.cardImageName = card.GetImageName();
                if (player.HasRole()) {
                    info.roleLine = player.GetLocation().GetCurrentRole().GetLine();
                }
            }
        }

        Map<String, String> images = new HashMap<>();
        Map<String, mothman.utils.Area> areas  = new HashMap<>();
        GameSet[] allSets = GameManager.GetInstance()
                .GetGameBoard().GetAllGameSets();

        for (GameSet gs : allSets) {

            if (gs instanceof ActingSet actingSet) {

                SceneCard card = actingSet.GetCurrentSceneCard();

                if (card != null && card.IsVisible()) {

                    images.put(gs.GetName(), card.GetImageName());
                    areas.put(gs.GetName(), gs.GetArea());
                }
            }
        }

        info.activeCardImages = images;
        info.activeCardAreas  = areas;

        return info;
    }

    private ArrayList<String> GetActionList() {
        ArrayList<String> possibleActions = new ArrayList<>();
        GameSet currentSet = PlayerManager.GetInstance().GetCurrentPlayer()
                .GetLocation().GetCurrentGameSet();

        boolean rolesAvailable = false;
        if (currentSet instanceof ActingSet) {
            if (((ActingSet) currentSet).GetCurrentSceneCard() != null) {
                rolesAvailable = !((ActingSet) currentSet).GetAvailableRoles().isEmpty();
            }
        }

        possibleActions.add("quit");
        possibleActions.add("pass");

        if (!PlayerManager.GetInstance().GetCurrentPlayer().HasRole() &&
                GameManager.GetInstance().GetActionTokens() >= 0 &&
                rolesAvailable) {
            possibleActions.add("acquire");
        }

        if (PlayerManager.GetInstance().GetCurrentPlayer().HasRole() &&
                GameManager.GetInstance().GetActionTokens() > 0 &&
                currentSet instanceof ActingSet &&
                ((ActingSet) currentSet).GetCurrentSceneCard() != null) {
            possibleActions.add("act");
            possibleActions.add("rehearse");
        } else {
            if (GameManager.GetInstance().GetActionTokens() > 0) {
                possibleActions.add("move");
            }
            if (currentSet instanceof CastingSet) {
                possibleActions.add("upgrade");
            }
        }
        return possibleActions;
    }

    public void DealCards(){
        _viewport.DealCards();
    }

}