package mothman.gui;

import mothman.utils.TurnDisplayInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ActionLogPanel {

    private final JPanel _panel;
    private final JTextArea _logArea;

    public ActionLogPanel() {
        _panel = new JPanel(new BorderLayout());
        _panel.setBackground(new Color(50, 50, 50));
        TitledBorder border = BorderFactory.createTitledBorder("Past Turns Log");
        border.setTitleColor(Color.ORANGE);
        _panel.setBorder(border);

        _logArea = new JTextArea();
        _logArea.setEditable(false);
        _logArea.setLineWrap(true);
        _logArea.setWrapStyleWord(true);
        _logArea.setBackground(new Color(50, 50, 50));
        _logArea.setForeground(Color.LIGHT_GRAY);

        _panel.add(new JScrollPane(_logArea), BorderLayout.CENTER);
    }

    public JPanel getComponent() { return _panel; }

    public void addToLog(String message) {
        SwingUtilities.invokeLater(() -> {
            _logArea.append(message + "\n");
            _logArea.setCaretPosition(_logArea.getDocument().getLength());
        });
    }

    public void update(TurnDisplayInfo info) {
        // Called each turn — hook here for any turn-based log updates
    }

}