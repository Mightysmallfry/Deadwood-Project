package mothman.utils;

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
    public Map<String, String> activeCardImages;   // setName -> "01.png"
    public Map<String, Area>   activeCardAreas;    // setName -> Area(x,y,w,h)
    public String cardImageName;  // current player's set card, if any
}