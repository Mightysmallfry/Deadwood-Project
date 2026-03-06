package mothman.viewports;

import mothman.managers.GameBoard;
import mothman.managers.GameManager;
import mothman.managers.PlayerManager;
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

public class ViewportGui extends JFrame implements Viewport {

    // Card images are exactly 205x115px — same as each set's <area> in board.xml.
    // No scaling is needed; we just position them at the set's x,y.
    private static final String CARD_IMAGE_PATH = "Assets/Card/";
    private static final String CARD_BACKING_IMAGE_PATH = "Assets/SceneCardBacking.png";

    // --- Layout ---
    private final Map<String, JLabel> _cardLabels = new java.util.HashMap<>();
    private Map<String, String> _pendingCardImages = null;  // [gameSet, SceneCardLabel]
    private Map<String, Area>   _pendingCardAreas  = null; // [gameSet, SceneCardImage]
    private JPanel _scoreboardPanel;
    private JPanel _rightContainer;
    private JPanel _pastLogPanel;
    private JPanel _actionsPanel;
    private JTextArea _pastLogArea;
    private JLayeredPane _gameLayeredPane;
    private JLabel _messageLabel;
    private int boardW = 1200;
    private int boardH = 900;

    // Bridges button clicks (EDT) to blocking Viewport calls (game thread).
    private final BlockingQueue<String> _inputQueue = new LinkedBlockingQueue<>();

    // Constructor
    public ViewportGui() {
        super("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // BOARD LAYER (CENTER)
        _gameLayeredPane = new JLayeredPane();
        _gameLayeredPane.setPreferredSize(new Dimension(boardW, boardH));
        _gameLayeredPane.setLayout(null); // absolute positioning for cards
        ImageIcon boardIcon = new ImageIcon("Assets/board.jpg");
        JLabel boardLabel = new JLabel(boardIcon);
        boardLabel.setBounds(0, 0, boardW, boardH);
        _gameLayeredPane.add(boardLabel, JLayeredPane.DEFAULT_LAYER);

        // MAIN CONTAINER (3 columns)
        JPanel mainContainer = new JPanel(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        // LEFT PANEL (Player Info)
        _scoreboardPanel = new JPanel();
        _scoreboardPanel.setPreferredSize(new Dimension(boardW/5, boardH));
        _scoreboardPanel.setBackground(new Color(35, 35, 35));
        _scoreboardPanel.setLayout(new BoxLayout(_scoreboardPanel, BoxLayout.Y_AXIS));
        TitledBorder scoreboardBorder = BorderFactory.createTitledBorder("Scoreboard");
        scoreboardBorder.setTitleColor(Color.CYAN);
        _scoreboardPanel.setBorder(scoreboardBorder);

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
        _pastLogPanel = new JPanel(new BorderLayout());
        _pastLogPanel.setPreferredSize(new Dimension(3 * boardW / 10, boardH / 3));
        _pastLogPanel.setBackground(new Color(50, 50, 50));
        TitledBorder pastLogBorder = BorderFactory.createTitledBorder("Past Turns Log");
        pastLogBorder.setTitleColor(Color.ORANGE);
        _pastLogPanel.setBorder(pastLogBorder);

        _pastLogArea = new JTextArea();
        _pastLogArea.setEditable(false);
        _pastLogArea.setLineWrap(true);
        _pastLogArea.setWrapStyleWord(true);

        JScrollPane logScrollPane = new JScrollPane(_pastLogArea);
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
        _messageLabel = new JLabel();
        _messageLabel.setForeground(Color.LIGHT_GRAY);
        _messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _actionsPanel.add(_messageLabel);
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
    public String GetName() {
        String name = JOptionPane.showInputDialog(
                this, "Enter your name:", "Welcome to Deadwood", JOptionPane.PLAIN_MESSAGE);
        return (name != null && !name.isBlank()) ? name.strip() : "Player";
    }

    @Override
    public String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info) {
        updateTurnHeader(info);
        _pendingCardImages = info.activeCardImages;
        _pendingCardAreas  = info.activeCardAreas;
        showButtons(possibleActions, "Actions:");
        return blockForInput();
    }

    @Override
    public String GetMove(HashMap<String, GameSet> neighbors) {
        ArrayList<String> locationNames = new ArrayList<>(neighbors.keySet());
        locationNames.add("cancel");
        showButtons(locationNames, "Move to:");
        return blockForInput();
    }

    @Override
    public String GetRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles) {
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
    public void DisplayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            _messageLabel.setText("<html><body style='width:170px'>" + message + "</body></html>");
            _actionsPanel.revalidate();
            _actionsPanel.repaint();
        });
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
        // Step 1 — rank selection
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

        // Step 2 — currency selection (only currencies valid for this rank)
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



    /**
     * Hides the card for a specific set immediately — call this from Act.java when a scene completes.
     */
    public void hideSceneCard(String setName) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = _cardLabels.get(setName);
            if (label != null) label.setVisible(false);
            _gameLayeredPane.repaint();
        });
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
                _actionsPanel.add(_messageLabel);
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

    //used to update the live log in the future in managers or smthn
    public void addToLog(String message) {
        SwingUtilities.invokeLater(() -> {
            _pastLogArea.append(message + "\n");
            _pastLogArea.setCaretPosition(_pastLogArea.getDocument().getLength());
        });
    }

    /** Parks the game thread until a button click arrives in the queue. */
    private String blockForInput() {
        try {
            return _inputQueue.take();
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
            _messageLabel.setText(text);
            _actionsPanel.revalidate();
            _actionsPanel.repaint();
        });
    }


    // =========== Card Layer =============

    /**
     * Used to redraw the card layer, drawing all cards, Called only after
     * Gameboard.Populate()
     */
    @Override
    public void DealCards(TurnDisplayInfo info)
    {
        // If the card is on the board
        for (Map.Entry<String, String> entry : info.allPresentCards.entrySet())
        {
            // Print that card face down.
            String setName = entry.getKey();
            String imgName = entry.getValue();
            Area   area    = info.allPresentCardAreas.get(setName);
            ImageIcon icon = new ImageIcon(CARD_BACKING_IMAGE_PATH);

            // If the card has not been made yet, create it
            JLabel label = _cardLabels.computeIfAbsent(setName, k -> {
                JLabel lable = new JLabel();
                _gameLayeredPane.add(lable, JLayeredPane.PALETTE_LAYER);
                return lable;
            });

            label.setIcon(icon);
            label.setBounds(area.GetX(), area.GetY(), area.GetWidth(), area.GetHeight());
            label.setVisible(true);
        }
        _gameLayeredPane.revalidate();
        _gameLayeredPane.repaint();
    }

    /**
     * Redraws only the active cards that have been discovered, should be called
     * after move, act (specifically on complete)
     */
    @Override
    public void UpdateCardDisplay(TurnDisplayInfo info) {
        Map<String, String> images = info.activeCardImages;
        Map<String, Area> areas = info.activeCardAreas;

        //TODO: Combine this with a DealCards and make the difference be the input maps?
        // They have the same usage but differing contexts.

        // Create a sceneCardLabel component if it does not already exist
        for (Map.Entry<String, String> entry : images.entrySet()) {

            String setName = entry.getKey();
            String imgName = entry.getValue();
            Area   area    = areas.get(setName);
            ImageIcon icon = new ImageIcon(CARD_IMAGE_PATH + imgName);

            // If invalid icon just skip
            if (icon.getIconWidth() <= 0) continue;

            // Create if the card is missing, Otherwise simply swap out the icon.
            JLabel label = _cardLabels.computeIfAbsent(setName, k -> {
                JLabel l = new JLabel();
                _gameLayeredPane.add(l, JLayeredPane.PALETTE_LAYER);
                return l;
            });
            label.setIcon(icon);
            label.setBounds(area.GetX(), area.GetY(), area.GetWidth(), area.GetHeight());
            label.setVisible(true);
        }

        // Redraw the layer, applying the buffer
        _gameLayeredPane.revalidate();
        _gameLayeredPane.repaint();
    }
}