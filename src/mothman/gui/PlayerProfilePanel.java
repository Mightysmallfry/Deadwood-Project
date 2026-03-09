package mothman.gui;

import mothman.player.Player;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

// Responsible for being a self contained profile panel to be added elsewhere
public class PlayerProfilePanel extends JPanel{

    private String _playerName;
    private JLabel _playerInfo;
    private Color _playerColor = Color.GREEN;

    public PlayerProfilePanel(Player player)
    {
        _playerName = player.GetPersonalId();

        TitledBorder scoreboardBorder = BorderFactory.createTitledBorder(player.GetPersonalId());
        scoreboardBorder.setTitleColor(Color.GREEN);
        setBackground(new Color(40, 40, 40));
        setBorder(scoreboardBorder);

        _playerInfo = new JLabel();
        _playerInfo.setText(GetFormattedInfo(player));
        _playerInfo.setForeground(_playerColor);
        add(_playerInfo);
    }

    private String GetFormattedInfo(Player player)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width:170px'>");
        sb.append(" - Rank: ").append(player.GetCurrentRank()).append("<br>");
        sb.append("Coin: ").append(player.GetCurrency().GetCoins()).append("<br>");
        sb.append("Credits: ").append(player.GetCurrency().GetCredits()).append("<br>");
        sb.append("Score: ").append(player.GetScore()).append("<br>");
        sb.append("Location: ").append(player.GetLocation().GetCurrentGameSet().GetName());

        if (player.HasRole())
        {
            sb.append("<br>").append("Role: ").append(player.GetLocation().GetCurrentRole().GetName()).append("<br>");
            sb.append("<em>\"").append(player.GetLocation().GetCurrentRole().GetLine()).append("\"</em><br>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }

    public void SetColor(Color playerColor){
        _playerColor = playerColor;
        _playerInfo.setForeground(_playerColor);
        UpdateBorder();
    }

    private void UpdateBorder(){
        TitledBorder scoreboardBorder = BorderFactory.createTitledBorder(_playerName);
        scoreboardBorder.setTitleColor(_playerColor);
        setBackground(new Color(40, 40, 40));
        setBorder(scoreboardBorder);
    }
}