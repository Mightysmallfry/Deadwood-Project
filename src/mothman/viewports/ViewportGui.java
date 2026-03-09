package mothman.viewports;

import mothman.gui.ActionLogPanel;
import mothman.gui.ActionMenuPanel;
import mothman.gui.GameBoardPane;
import mothman.gui.ScoreBoardPanel;
import mothman.managers.PlayerManager;
import mothman.managers.ViewportController;
import mothman.player.Player;
import mothman.sets.*;
import mothman.utils.TurnDisplayInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ViewportGui extends JFrame implements Viewport {

    // --- Layout ---
    private ScoreBoardPanel _scoreboardPanel;
    private ActionLogPanel _pastLogPanel;
    private ActionMenuPanel _actionMenuPanel;

    private JPanel _rightContainer;
    private GameBoardPane _gameLayeredPane;
    private int boardW = 1200;
    private int boardH = 900;

    private ViewportController _viewportController;

    private final BlockingQueue<String> _inputQueue = new LinkedBlockingQueue<>();

    public ViewportGui() {
        super("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        _gameLayeredPane = new GameBoardPane(boardW, boardH);

        JPanel mainContainer = new JPanel(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        _scoreboardPanel = new ScoreBoardPanel(boardW / 5, boardH);
        mainContainer.add(_scoreboardPanel, BorderLayout.WEST);
        mainContainer.add(_gameLayeredPane, BorderLayout.CENTER);

        _rightContainer = new JPanel(new BorderLayout());
        _rightContainer.setPreferredSize(new Dimension(3 * boardW / 10, boardH));
        _rightContainer.setBackground(new Color(30, 30, 30));
        _rightContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainContainer.add(_rightContainer, BorderLayout.EAST);

        // PAST TURNS LOG (Top Right)
        _pastLogPanel = new ActionLogPanel();
        _pastLogPanel.setPreferredSize(new Dimension(3 * boardW / 10, boardH / 3));
        JScrollPane logScrollPane = new JScrollPane(_pastLogPanel.GetLogArea());
        _pastLogPanel.add(logScrollPane, BorderLayout.CENTER);
        _rightContainer.add(_pastLogPanel, BorderLayout.NORTH);

        // ACTIONS PANEL (Bottom Right) — delegated to ActionMenuPanel
        _actionMenuPanel = new ActionMenuPanel(_inputQueue);
        _rightContainer.add(_actionMenuPanel.getComponent(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Viewport Interface
    // -------------------------------------------------------------------------

    @Override
    public void SetController(ViewportController controller) {
        _viewportController = controller;
        InitializeObservers();
    }

    @Override
    public void ShowGameOver() {
        ArrayList<String> options = new ArrayList<>();
        options.add("quit");
        _actionMenuPanel.showButtons(options, "Game Over!");
        // Block here until quit is clicked, then exit
        try {
            _inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }

    public void InitializeObservers() {}

    @Override
    public String GetName() {
        String name = JOptionPane.showInputDialog(
                this, "Enter your name:", "Welcome to Deadwood", JOptionPane.PLAIN_MESSAGE);
        return (name != null && !name.isBlank()) ? name.strip() : "Player";
    }

    @Override
    public String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info) {
        updateTurnHeader(info);
        _actionMenuPanel.showButtons(possibleActions, "Actions:");
        String input = blockForInput();
        DisplayMessage(info.playerId + " : " + input);
        return input;
    }

    @Override
    public String GetMove(HashMap<String, GameSet> neighbors, Player player) {
        ArrayList<String> locationNames = new ArrayList<>(neighbors.keySet());
        locationNames.add("cancel");
        _actionMenuPanel.showButtons(locationNames, "Move to:");
        String input = blockForInput();
        DisplayMessage(player.GetPersonalId() + " : " + input);
        return input;
    }

    @Override
    public String GetRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles, Player player) {
        ArrayList<String> roleOptions = new ArrayList<>();

        if (sceneCard != null) {
            for (ActingRole role : sceneCard.GetAvailableRoles()) {
                roleOptions.add(role.GetName() + " (rank " + role.GetRank() + ")");
            }
        }
        if (localRoles != null) {
            for (ActingRole role : localRoles) {
                roleOptions.add("[Extra] " + role.GetName() + " (rank " + role.GetRank() + ")");
            }
        }
        roleOptions.add("cancel");

        String cardName = sceneCard != null ? sceneCard.GetName() : "Unknown Scene";
        _actionMenuPanel.showButtons(roleOptions, "Roles — " + cardName + ":");

        String raw = blockForInput();
        DisplayMessage(player.GetPersonalId() + " : " + raw);

        if (raw.equals("cancel")) return "cancel";
        return raw.replaceAll("\\s*\\(rank \\d+\\)$", "").replaceAll("^\\[Extra\\] ", "").strip();
    }

    @Override
    public int GetUpgradeRank() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this, "Enter the rank you want to purchase:", "Upgrade", JOptionPane.PLAIN_MESSAGE);
            if (input == null) continue;
            try { return Integer.parseInt(input.strip()); }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public String GetUpgradeCurrency() {
        ArrayList<String> options = new ArrayList<>();
        options.add("dollar");
        options.add("credit");
        options.add("cancel");
        _actionMenuPanel.showButtons(options, "Pay with:");
        String choice = blockForInput();
        return choice.equals("cancel") ? "dollar" : choice;
    }

    @Override
    public void DisplayActionList(ArrayList<String> actionList) {
        // Button panel IS the action list
    }

    @Override
    public int[] AskUpgrade(int currentRank, int maxRank, ArrayList<UpgradeData> upgrades) {
        String[] result = _actionMenuPanel.ShowUpgradeMenu(currentRank, maxRank, upgrades);
        if (result == null) return null;
        int rank = Integer.parseInt(result[0]);
        String currency = result[1];
        for (UpgradeData u : upgrades) {
            if (u.GetRank() == rank && u.GetCurrencyType().equals(currency)) {
                return new int[]{ rank, u.GetCostAmount() };
            }
        }
        return null;
    }

    @Override
    public void DisplayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            _pastLogPanel.AddToLog(message);
            revalidate();
            repaint();
        });
    }

    @Override
    public void DealCards(TurnDisplayInfo info) {
        SwingUtilities.invokeLater(() -> {
            _gameLayeredPane.Update(info);
            _scoreboardPanel.Update(PlayerManager.GetInstance().GetPlayerLibrary());
        });
    }

    @Override
    public void Update(TurnDisplayInfo info) {
        SwingUtilities.invokeLater(() -> {
            updateTurnHeader(info);
            _scoreboardPanel.Update(info.players);
            _gameLayeredPane.Update(info);
            _pastLogPanel.Update();
        });
    }
    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String blockForInput() {
        try {
            return _inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "pass";
        }
    }

    private void updateTurnHeader(TurnDisplayInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width:170px; color:#cccccc'>");
        sb.append("<b>").append(info.playerId).append("'s Turn</b><br>");
        sb.append("Location: ").append(info.locationName).append("<br>");

        if (info.isActingSet) {
            if (!info.sceneComplete) {
                sb.append("Budget: ").append(info.budget).append("<br>");
                sb.append("Shots: ").append(info.currentShots)
                        .append("/").append(info.maxShots).append("<br>");
                sb.append("Rehearsals: ").append(info.rehearsals).append("<br>");
                if (info.roleLine != null) {
                    sb.append("<i>\"").append(info.roleLine).append("\"</i><br>");
                }
            } else {
                sb.append("<i>Scene complete</i><br>");
            }
        }

        sb.append("</body></html>");
        _actionMenuPanel.displayMessage(sb.toString());
    }
}