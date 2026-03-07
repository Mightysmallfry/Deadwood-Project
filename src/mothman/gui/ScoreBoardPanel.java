package mothman.gui;

import mothman.player.Player;
import mothman.utils.TurnDisplayInfo;
import mothman.utils.PlayerColor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

// Responsible for being the sidebar that will be filled with player profiles.
public class ScoreBoardPanel extends JPanel {

    private final String TITLE = "Score Board";
    private JLabel _label;

    // Observer of players?
    public ScoreBoardPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(40, 40, 40));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        TitledBorder scoreboardBorder = BorderFactory.createTitledBorder(TITLE);
        scoreboardBorder.setTitleColor(Color.CYAN);
        setBorder(scoreboardBorder);
    }

    // Called by ScoreLayer on each game update — refreshes the turn header label.
    public void update(TurnDisplayInfo info) {
        SwingUtilities.invokeLater(() -> {
            //just refresh the existing label if present
            // or do nothing until UpdatePlayerDisplay() is called with full data.
        });
    }

    // Update the entire list, keep order the same through each update.
    public void Update(Player[] players)
    {
        try {
            SwingUtilities.invokeAndWait(() -> {
                removeAll();

                // Create a ProfilePanel for each player and add it
                for (Player player : players) {
                    PlayerProfilePanel playerProfile = new PlayerProfilePanel(player);
                    playerProfile.SetColor(PlayerColor.GetInstance().GetNextColor());
                    add(playerProfile);
                    add(Box.createVerticalStrut(4));
                }

                // Repaint the canvas.
                revalidate();
                repaint();
            });
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }


}