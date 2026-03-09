package mothman.gui;

import mothman.player.Player;
import mothman.sets.ActingSet;
import mothman.utils.Area;
import mothman.utils.PlayerColor;
import mothman.utils.TurnDisplayInfo;
import mothman.turnactions.Move;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

// Goal is to show the entire board in a layered fashion
//  - Background (Bottom)
//  - Cards and Shots
//  - Players (Top)
public class GameBoardPane extends JLayeredPane {

    private static final String CARD_IMAGE_PATH = "Assets/Card/";
    private static final String CARD_BACKING_IMAGE_PATH = "Assets/SceneCardBacking.png";
    private static final String PLAYER_ICON_IMAGE_PATH = "Assets/Dice/";
    private static final String SHOT_ICON_IMAGE_PATH = "Assets/Hat.png";

    private static final Integer CARD_LAYER = PALETTE_LAYER;  // Equivalent to PaletteLayer
    private static final Integer SHOT_LAYER = PALETTE_LAYER + 50;
    private static final Integer PLAYER_LAYER = MODAL_LAYER;


    ImageIcon boardIcon = new ImageIcon("Assets/board.jpg");
    private JLabel boardLabel;
    private Move selectedMove;

    private Map<String, JLabel> _cardLabels = new java.util.HashMap<>();
    private Map<String, JLabel> _shotLabels = new java.util.HashMap<>();

    public GameBoardPane(int boardWidth, int boardHeight) {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        // Absolute positioning for cards
        setLayout(null);
        boardLabel = new JLabel(boardIcon);
        boardLabel.setBounds(0, 0, boardWidth, boardHeight);
        add(boardLabel, JLayeredPane.DEFAULT_LAYER);

    }

    public void Update(TurnDisplayInfo info) {
        SwingUtilities.invokeLater(() -> {
            DrawCards(info);
            DrawShots(info);
            DrawPlayers(info);
            revalidate();
            repaint();
        });
    }

    /**
     * Draws the cards, both present and flipped onto the board.
     *
     * Does not revalidate or repaint
     * @param info
     */
    private void DrawCards(TurnDisplayInfo info){
        // Hide only the completed cards
        Map<String, ActingSet> allCards = info.presentActingSets;

        // If the card is not visible hide the card
        for (Map.Entry<String, ActingSet> entry : allCards.entrySet()) {
            if (entry.getValue().IsComplete() && _cardLabels.get(entry.getKey()).isVisible()){
                HideCard(entry.getKey());
            }
        }
        // These data types don't help at all understand what these hold lol.

        // Active is revealed
        Map<String, String> visitedCardImages = info.visibleCardImages;
        Map<String, Area> presentCardAreas = info.actingSetCardAreas;

        for (Map.Entry<String, ActingSet> presentCard : allCards.entrySet())
        {
            if (!presentCard.getValue().IsComplete()){
                // Get the labeled card creating on not finding it.
                String setName = presentCard.getKey();
                JLabel label = GetCardLabel(setName);

                // No matter what, if the card exists, we first paint it face down
                Area area = presentCardAreas.get(setName);
                label.setIcon(new ImageIcon(CARD_BACKING_IMAGE_PATH));

                // If the card has actually been visited, print the revealed card instead.
                if (visitedCardImages.containsKey(presentCard.getKey()))
                {
                    label.setIcon(new ImageIcon(CARD_IMAGE_PATH + visitedCardImages.get(presentCard.getKey())));
                }

                label.setBounds(area.GetX(), area.GetY(), area.GetWidth(), area.GetHeight());

                    label.setVisible(true);
                }
        }
    }

    private void DrawShots(TurnDisplayInfo info) {
        HideLayer(SHOT_LAYER);

        for (ActingSet set : info.allActingSets) {
            int shotTally = set.GetMaxProgress();
            for (Area shotArea : set.GetShotAreas()) {
                int shotProgress = set.GetMaxProgress() - set.GetCurrentProgress();
                String key = set.GetName() + "_" + shotTally;

                JLabel shotLabel = GetShotLabel(key);
                shotLabel.setBounds(shotArea.GetX(), shotArea.GetY(), shotArea.GetWidth(), shotArea.GetHeight());
                shotLabel.setVisible(shotTally <= shotProgress);

                shotTally--;
            }
        }
    }

    //TODO: Fixed offsets, now we need to add areas for people to be in
    // trailer and casting office.
    private void DrawPlayers(TurnDisplayInfo info){
        HideLayer(PLAYER_LAYER);

        Player[] players = info.players;

        for (Player player : players){
            if (player.HasRole())
            {
                Icon playerIcon = GetPlayerIcon(player);
                Area playerArea = player.GetLocation().GetCurrentRole().GetArea();

                // We only want to get make a new one if we have not made it before.
                // Then we can just reuse the already existing ones, swapping icons
                JLabel playerLabel = new JLabel(playerIcon);
                add(playerLabel, PLAYER_LAYER);

                if (player.GetLocation().GetOnCard())
                {
                    Area cardOffset = player.GetLocation().GetCurrentGameSet().GetArea();

                    playerArea = new Area(playerArea.GetX() + cardOffset.GetX(),
                            playerArea.GetY() + cardOffset.GetY(),
                            playerArea.GetWidth(),
                            playerArea.GetHeight()
                    );
                }

                playerLabel.setBounds(playerArea.GetX(),
                        playerArea.GetY(),
                        playerArea.GetWidth(),
                        playerArea.GetHeight());
                playerLabel.setVisible(true);
            }
        }
    }

    private Icon GetPlayerIcon(Player player)
    {
        // Get Player Color
        String playerImageName = PlayerColor.GetInstance().GetColorPrefix(player);
        int playerRank = player.GetCurrentRank();
        playerImageName = playerImageName + String.valueOf(playerRank) + ".png";

        return new ImageIcon(PLAYER_ICON_IMAGE_PATH + playerImageName);
    }

    private JLabel GetShotLabel(String key) {
        return _shotLabels.computeIfAbsent(key, k -> {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(SHOT_ICON_IMAGE_PATH));
            add(label, SHOT_LAYER);
            return label;
        });
    }


    /**
     * Gets the label for a scene card if possible, if one does not yet exist
     * it will create one return the new label
     */
    private JLabel GetCardLabel(String setName) {
        return _cardLabels.computeIfAbsent(setName, k -> {
            JLabel label = new JLabel();
            add(label, GameBoardPane.CARD_LAYER);
            return label;
        });
    }

    /**
     * Removes all components from a layer, the components will still exist.
     * @param layer
     */
    private void ClearLayer(Integer layer)
    {
        Component[] ToRemoveLayer = getComponentsInLayer(layer);
        for (Component component : ToRemoveLayer)
        {
            remove(component);
        }
    }

    private void HideLayer(Integer layer){
        Component[] ToRemoveLayer = getComponentsInLayer(layer);
        for (Component component : ToRemoveLayer)
        {
            component.setVisible(false);
        }
    }

    private void HideCard(String setName) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = _cardLabels.get(setName);
            if (label != null) {
                label.setIcon(null);
                label.setVisible(false);
            }
            repaint();
        });
    }


    public Move getMove() {
        return selectedMove;
    }

    public void setMove(Move move) {
        this.selectedMove = move;
    }

}