package mothman.utils;

import mothman.managers.PlayerManager;
import mothman.player.Player;

import java.util.Map;

public class TurnDisplayInfo {
    public String playerId;
    public String locationName;
    public String roleLine;
    public int budget;
    public int maxShots;
    public int currentShots;
    public int actionTokens;
    public boolean sceneComplete;
    public boolean isActingSet;
    public Map<String, String> activeCardImages;   // ActingSetName -> "01.png"
    public Map<String, Area>   activeCardAreas;    // ActingSetName -> Area(x,y,w,h)
    public String cardImageName;  // current player's set card, if any
    public Map<String, String> allPresentCards; // ActingSetName -> SceneCardName
    public Map<String, Area> allPresentCardAreas; // ActingSetName -> Area(x,y,w,h)
    public Player[] players;
}