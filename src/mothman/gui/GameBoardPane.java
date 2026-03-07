package mothman.gui;

import mothman.managers.PlayerManager;
import mothman.utils.Area;
import mothman.utils.TurnDisplayInfo;
import mothman.turnactions.Move;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

// Goal is to show the entire board in a layered fashion
//  - Background (Bottom)
//  - Cards and Shots
//  - Players (Top)
public class GameBoardPane extends JLayeredPane {

    // Card images are exactly 205x115px - What an annoying resolution
    private static final String CARD_IMAGE_PATH = "Assets/Card/";
    private static final String CARD_BACKING_IMAGE_PATH = "Assets/SceneCardBacking.png";

    ImageIcon boardIcon = new ImageIcon("Assets/board.jpg");
    private JLabel boardLabel;
    private Move selectedMove;

    private Map<String, JLabel> _cardLabels = new java.util.HashMap<>();

    public GameBoardPane(int boardWidth, int boardHeight) {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        // Absolute positioning for cards
        setLayout(null);
        boardLabel = new JLabel(boardIcon);
        boardLabel.setBounds(0, 0, boardWidth, boardHeight);
        add(boardLabel, JLayeredPane.DEFAULT_LAYER);
    }

    public void Update(TurnDisplayInfo info) {
        // These data types don't help at all understand what these hold lol.

        // Active is revealed
        Map<String, String> visitedCardImages = info.activeCardImages;
        Map<String, Area> visitedAreas = info.activeCardAreas;

        // All cards on the board
        Map<String, String> presentCardImages = info.allPresentCards;
        Map<String, Area> presentCardAreas = info.allPresentCardAreas;

        for (Map.Entry<String, String> presentCard : presentCardImages.entrySet())
        {
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

        revalidate();
        repaint();
    }

    /**
     * Gets the label for a scene card if possible, if one does not yet exist
     * it will create one return the new label
     */
    private JLabel GetCardLabel(String setName) {
        return _cardLabels.computeIfAbsent(setName, k -> {
            JLabel label = new JLabel();
            add(label, JLayeredPane.PALETTE_LAYER);
            return label;
        });
    }

    /**
     * Removes all components from a layer, the components will still exist.
     * @param layer
     */
    private void ClearLayer(int layer)
    {
        Component[] ToRemoveLayer = getComponentsInLayer(layer);
        for (Component component : ToRemoveLayer)
        {
            remove(component);
        }
    }

    public Move getMove() {
        return selectedMove;
    }

    public void setMove(Move move) {
        this.selectedMove = move;
    }

    /**
     * Hides the card for a specific set immediately — call this from Act.java when a scene completes.
     */
    public void hideSceneCard(String setName) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = _cardLabels.get(setName);
            if (label != null) label.setVisible(false);
            repaint();
        });
    }
}