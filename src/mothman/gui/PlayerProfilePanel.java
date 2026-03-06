package mothman.gui;

import mothman.player.Player;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PlayerProfilePanel extends JPanel{
    // TODO: Randomize and Queue 8 colors for player.

    JLabel playerInfo;

    public PlayerProfilePanel(Player player)
    {
        TitledBorder scoreboardBorder = BorderFactory.createTitledBorder(player.GetPersonalId());
        scoreboardBorder.setTitleColor(Color.GREEN);
        setBorder(scoreboardBorder);

        playerInfo = new JLabel(GetFormattedInfo(player));
    }

    private String GetFormattedInfo(Player player)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(player.GetPersonalId());
        sb.append(" - Rank: ");
        sb.append(player.GetCurrentRank()).append("\n");

        sb.append("[Score : ").append(player.GetScore()).append("]\n");
        sb.append("[Location : ").append(player.GetLocation()).append("]\n");

        return sb.toString();
    }


}