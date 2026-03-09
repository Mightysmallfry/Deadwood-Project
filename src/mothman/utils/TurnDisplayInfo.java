package mothman.utils;

import mothman.player.Player;
import mothman.sets.ActingSet;

import java.util.ArrayList;
import java.util.Map;

public class TurnDisplayInfo {
    public String playerId;
    public String locationName;
    public String roleLine;
    public int budget;
    public int maxShots;
    public int currentShots;
    public int actionTokens;
    public int rehearsals;
    public boolean sceneComplete;
    public boolean isActingSet;
    public Map<String, String> visibleCardImages;   // ActingSetName -> "01.png", holds the images of all visible card images
    public Map<String, Area> visibleCardAreas;    // ActingSetName -> Area(x,y,w,h), holds the map of all visible card areas
    public Map<String, ActingSet> presentActingSets; // ActingSetName -> ActingSet, holds all acting sets with cards currently on the board (revealed or not)
    public Map<String, Area> actingSetCardAreas; // ActingSetName -> Area(x,y,w,h), Holds all Card Areas for acting sets on the board.
    public Player[] players;
    public ArrayList<ActingSet> allActingSets;
}