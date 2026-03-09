package mothman.viewports;

import mothman.gui.ActionLogPanel;
import mothman.gui.GameBoardPane;
import mothman.gui.ScoreBoardPanel;
import mothman.managers.PlayerManager;
import mothman.managers.ViewportController;
import mothman.player.Player;
import mothman.sets.*;
import mothman.utils.Area;
import mothman.utils.TurnDisplayInfo;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO: Direct ViewportGui to becoming a ui-ui interaction manager
public class ViewportGui extends JFrame implements Viewport {

    // --- Layout ---
    private ScoreBoardPanel _scoreboardPanel;
    private ActionLogPanel _pastLogPanel;

    private JPanel _rightContainer;
    private JPanel _actionsPanel;
    private GameBoardPane _gameLayeredPane;
    private JLabel _turnInfoLabel;
    private int boardW = 1200;
    private int boardH = 900;

    private ViewportController _viewportController;

    // Bridges button clicks (EDT) to blocking Viewport calls (game thread).
    private final BlockingQueue<String> _inputQueue = new LinkedBlockingQueue<>();

    // Constructor
    public ViewportGui() {
        super("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // BOARD LAYER (CENTER)
        _gameLayeredPane = new GameBoardPane(boardW, boardH);

        // MAIN CONTAINER (3 columns)
        JPanel mainContainer = new JPanel(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        // LEFT PANEL (Player Info and Scoreboard)
        _scoreboardPanel = new ScoreBoardPanel(boardW/5, boardH);

        mainContainer.add(_scoreboardPanel, BorderLayout.WEST);

        // CENTER (Board)
        mainContainer.add(_gameLayeredPane, BorderLayout.CENTER);

        // RIGHT CONTAINER
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

        // ACTIONS PANEL (Bottom Right)
        _actionsPanel = new JPanel();
        _actionsPanel.setBackground(new Color(40, 40, 40));
        _actionsPanel.setLayout(new BoxLayout(_actionsPanel, BoxLayout.Y_AXIS));
        TitledBorder actionBorder = BorderFactory.createTitledBorder("Actions");
        actionBorder.setTitleColor(Color.ORANGE);
        _actionsPanel.setBorder(actionBorder);

        // Message label (turn info)
        _turnInfoLabel = new JLabel();
        _turnInfoLabel.setForeground(Color.LIGHT_GRAY);
        _turnInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _actionsPanel.add(_turnInfoLabel);
        _actionsPanel.add(Box.createVerticalStrut(10));

        _rightContainer.add(_actionsPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Viewport Interface
    // -------------------------------------------------------------------------

    @Override
    public void SetController(ViewportController controller){
        _viewportController = controller;
        InitializeObservers();
    }

    public void InitializeObservers(){

    }


    @Override
    public String GetName() {
        String name = JOptionPane.showInputDialog(
                this, "Enter your name:", "Welcome to Deadwood", JOptionPane.PLAIN_MESSAGE);
        return (name != null && !name.isBlank()) ? name.strip() : "Player";
    }

    @Override
    public String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info) {
        updateTurnHeader(info);
        showButtons(possibleActions, "Actions:");
        String input = blockForInput();
        DisplayMessage(info.playerId + " : " + input);
        return input;
    }

    @Override
    public String GetMove(HashMap<String, GameSet> neighbors, Player player) {
        ArrayList<String> locationNames = new ArrayList<>(neighbors.keySet());
        locationNames.add("cancel");
        showButtons(locationNames, "Move to:");
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
        showButtons(roleOptions, "Roles — " + cardName + ":");

        String raw = blockForInput();
        DisplayMessage(player.GetPersonalId() + " : " + raw);

        if (raw.equals("cancel")) return "cancel";
        return raw.replaceAll("\\s*\\(rank \\d+\\)$", "").replaceAll("^\\[Extra\\] ", "").strip();
    }

    /** Fallback for text path — GUI uses ShowUpgradeMenu() via ViewportController. */
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

    /** Fallback for text path — GUI uses ShowUpgradeMenu() via ViewportController. */
    @Override
    public String GetUpgradeCurrency() {
        ArrayList<String> options = new ArrayList<>();
        options.add("dollar");
        options.add("credit");
        options.add("cancel");
        showButtons(options, "Pay with:");
        String choice = blockForInput();
        return choice.equals("cancel") ? "dollar" : choice;
    }

    @Override
    public void DisplayActionList(ArrayList<String> actionList) {
        // Button panel IS the action list
    }

    @Override
    public int[] AskUpgrade(int currentRank, int maxRank, ArrayList<UpgradeData> upgrades) {
        String[] result = ShowUpgradeMenu(currentRank, maxRank, upgrades);
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

    /**
     * Two-step upgrade flow entirely on the right panel.
     *   Step 1: one button per purchasable rank, showing both costs.
     *   Step 2: dollar / credit buttons for the chosen rank only.
     *
     * @return String[]{ rankString, currencyString }, or null if the player cancels.
     */
    public String[] ShowUpgradeMenu(int currentRank, int maxRank,
                                    ArrayList<UpgradeData> upgrades) {
        // Step 1: rank selection
        ArrayList<String> rankButtons = new ArrayList<>();

        for (int rank = currentRank + 1; rank <= maxRank; rank++) {
            Integer dollarCost = null;
            Integer creditCost = null;

            for (UpgradeData u : upgrades) {
                if (u.GetRank() == rank) {
                    if ("dollar".equals(u.GetCurrencyType())) dollarCost = u.GetCostAmount();
                    if ("credit".equals(u.GetCurrencyType())) creditCost = u.GetCostAmount();
                }
            }

            if (dollarCost != null || creditCost != null) {
                StringBuilder lbl = new StringBuilder("Rank " + rank + " |");
                if (creditCost != null) lbl.append("  ").append(creditCost).append(" cr");
                if (dollarCost  != null) lbl.append("  / $").append(dollarCost);
                rankButtons.add(lbl.toString());
            }
        }

        if (rankButtons.isEmpty()) {
            DisplayMessage("No upgrades available.");
            return null;
        }

        rankButtons.add("cancel");
        showButtons(rankButtons, "Upgrade  (current rank: " + currentRank + "):");
        String rankChoice = blockForInput();
        if (rankChoice.equals("cancel")) return null;

        int chosenRank;
        try {
            chosenRank = Integer.parseInt(
                    rankChoice.split("\\|")[0].replace("Rank", "").strip());
        } catch (NumberFormatException e) { return null; }

        // Step 2: currency selection (only currencies valid for this rank)
        boolean hasDollar = false, hasCredit = false;
        for (UpgradeData u : upgrades) {
            if (u.GetRank() == chosenRank) {
                if ("dollar".equals(u.GetCurrencyType())) hasDollar = true;
                if ("credit".equals(u.GetCurrencyType())) hasCredit = true;
            }
        }

        ArrayList<String> currencyOptions = new ArrayList<>();
        if (hasDollar) currencyOptions.add("dollar");
        if (hasCredit) currencyOptions.add("credit");
        currencyOptions.add("cancel");

        showButtons(currencyOptions, "Pay with:");
        String currencyChoice = blockForInput();
        if (currencyChoice.equals("cancel")) return null;

        return new String[]{ String.valueOf(chosenRank), currencyChoice };
    }

    // Private helpers
    /**
     * Clears the dynamic button area (indices 4+) and rebuilds it.
     * invokeAndWait ensures buttons exist before blockForInput() is called.
     */
    private void showButtons(ArrayList<String> options, String header) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                _actionsPanel.removeAll();
                _actionsPanel.add(_turnInfoLabel);
                _actionsPanel.add(Box.createVerticalStrut(10));

                JLabel headerLabel = new JLabel(header);
                headerLabel.setForeground(Color.GRAY);
                headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 11f));
                headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                _actionsPanel.add(headerLabel);
                _actionsPanel.add(Box.createVerticalStrut(6));

                for (String option : options) {
                    JButton btn = makeActionButton(option);
                    btn.addActionListener(e -> _inputQueue.offer(option));
                    _actionsPanel.add(btn);
                    _actionsPanel.add(Box.createVerticalStrut(4));
                }

                _actionsPanel.revalidate();
                _actionsPanel.repaint();
            });
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void DisplayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            _pastLogPanel.AddToLog(message);
            revalidate();
            repaint();
        });
    }

    /** Parks the game thread until a button click arrives in the queue. */
    private String blockForInput() {
        try {
            String input = _inputQueue.take();
            return input;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "pass";
        }
    }

    private JButton makeActionButton(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(_actionsPanel.getPreferredSize().width, 32));
        btn.setBackground(new Color(70, 70, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
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

        final String text = sb.toString();
        SwingUtilities.invokeLater(() -> {
            _turnInfoLabel.setText(text);
            _actionsPanel.revalidate();
            _actionsPanel.repaint();
        });
    }

    // -------------------------------------------------------------------------
    // Cards and the main game board.
    // -------------------------------------------------------------------------

    // =========== Card Layer =============

    /**
     * Used to redraw the card layer, drawing all cards, Called only after
     * Gameboard.Populate()
     */
    @Override
    public void DealCards(TurnDisplayInfo info)
    {
        // This viewport is really the final container, all the other containers
        // need to be aware of their changes so they redraw themselves.
        _gameLayeredPane.Update(info);
        _scoreboardPanel.Update(PlayerManager.GetInstance().GetPlayerLibrary());
    }


    @Override
    public void Update(TurnDisplayInfo info) {
        updateTurnHeader(info);
        _scoreboardPanel.Update(info.players);
        _gameLayeredPane.Update(info);
        _pastLogPanel.Update(); // Does nothing ATM
    }

}