package mothman.managers;

import mothman.player.Player;
import mothman.sets.*;
import mothman.utils.TurnDisplayInfo;
import mothman.viewports.Viewport;

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
        return _viewport.AskUpgrade(currentRank, maxRank, upgrades);
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
        info.players = PlayerManager.GetInstance().GetPlayerLibrary();

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
        Map<String, String> allSceneCards = new HashMap<>();
        Map<String, mothman.utils.Area> allSceneCardAreas = new HashMap<>();
        Map<String, mothman.utils.Area> areas  = new HashMap<>();
        GameSet[] allSets = GameManager.GetInstance().GetGameBoard().GetAllGameSets();

        // Get all of the cards on the board
        for (GameSet gameSet : allSets) {
            if (gameSet instanceof ActingSet actingSet) {
                SceneCard card = actingSet.GetCurrentSceneCard();
                if (card == null) {
                    continue;
                }
                // If the card is not null, then we have a card present
                if (card.IsVisible()) {

                    images.put(gameSet.GetName(), card.GetImageName());
                    areas.put(gameSet.GetName(), gameSet.GetArea());
                }

                allSceneCards.put(gameSet.GetName(), card.GetName());
                allSceneCardAreas.put(gameSet.GetName(), gameSet.GetArea());
            }
        }

        info.activeCardImages = images;
        info.activeCardAreas  = areas;
        info.allPresentCards = allSceneCards;
        info.allPresentCardAreas = allSceneCardAreas;

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
        possibleActions.add("force");

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
        _viewport.DealCards(BuildTurnInfo());
    }

    // ================ UPDATE METHODS ===================

    public void UpdateBoardDisplay(){
        _viewport.UpdateCardDisplay(BuildTurnInfo());
    }

    /**
     * Use this for updating the Ui Elements as a whole.
     */
    public void UpdateViewport() {
        TurnDisplayInfo info = BuildTurnInfo();
        _viewport.Update(info);
    }

}