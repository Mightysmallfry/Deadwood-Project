package mothman.viewports;

import mothman.sets.ActingRole;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.sets.UpgradeData;
import mothman.utils.Area;
import mothman.utils.TurnDisplayInfo;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ViewportGui extends JFrame implements Viewport {

    // Card images are exactly 205x115px — same as each set's <area> in board.xml.
    // No scaling is needed; we just position them at the set's x,y.
    private static final String CARD_IMAGE_PATH = "Assets/cards/";

    // --- Layout ---
    private final JLayeredPane _gameLayeredPane;
    private final JPanel       _rightPanel;
    private final JLabel       _messageLabel;
    private final JLabel       _boardLabel;

    // One overlay label per acting set, keyed by set name. All active cards stay visible simultaneously.
    private final Map<String, JLabel> _cardLabels = new java.util.HashMap<>();
    private Map<String, String> _pendingCardImages = null;
    private Map<String, Area>   _pendingCardAreas  = null;

    // Bridges button clicks (EDT) to blocking Viewport calls (game thread).
    private final BlockingQueue<String> _inputQueue = new LinkedBlockingQueue<>();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ViewportGui() {
        super("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Board
        _boardLabel = new JLabel();
        ImageIcon ourBoard = new ImageIcon("Assets/board.jpg");
        _boardLabel.setIcon(ourBoard);
        int boardW = ourBoard.getIconWidth();
        int boardH = ourBoard.getIconHeight();

        _gameLayeredPane = new JLayeredPane();
        _gameLayeredPane.setPreferredSize(new Dimension(boardW, boardH));
        _boardLabel.setBounds(0, 0, boardW, boardH);
        _gameLayeredPane.add(_boardLabel, JLayeredPane.DEFAULT_LAYER);

        // Card labels are created on demand in updateSceneCards() as sets become active.

        add(_gameLayeredPane, BorderLayout.CENTER);

        // Right panel — dark sidebar
        _rightPanel = new JPanel();
        _rightPanel.setLayout(new BoxLayout(_rightPanel, BoxLayout.Y_AXIS));
        _rightPanel.setPreferredSize(new Dimension(200, boardH));
        _rightPanel.setBackground(new Color(40, 40, 40));
        _rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Permanent header components — indices 0-3, never removed by showButtons().
        _messageLabel = new JLabel("<html><body style='width:170px'>&nbsp;</body></html>");
        _messageLabel.setForeground(Color.LIGHT_GRAY);
        _messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _messageLabel.setMaximumSize(new Dimension(180, 150));
        _rightPanel.add(_messageLabel);               // 0
        _rightPanel.add(Box.createVerticalStrut(10)); // 1
        _rightPanel.add(new JSeparator());            // 2
        _rightPanel.add(Box.createVerticalStrut(10)); // 3

        add(_rightPanel, BorderLayout.EAST);

        pack();
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
            _rightPanel.revalidate();
            _rightPanel.repaint();
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

    // Scene card overlay

    private void applySceneCards(Map<String, String> images, Map<String, Area> areas) {
        for (Map.Entry<String, JLabel> entry : _cardLabels.entrySet()) {
            if (!images.containsKey(entry.getKey())) {
                entry.getValue().setVisible(false);
            }
        }
        for (Map.Entry<String, String> entry : images.entrySet()) {
            String setName = entry.getKey();
            String imgName = entry.getValue();
            Area   area    = areas.get(setName);
            ImageIcon icon = new ImageIcon("Assets/Card/" + imgName);

            if (icon.getIconWidth() <= 0) continue;
            JLabel label = _cardLabels.computeIfAbsent(setName, k -> {
                JLabel l = new JLabel();
                _gameLayeredPane.add(l, JLayeredPane.PALETTE_LAYER);
                return l;
            });
            label.setIcon(icon);
            label.setBounds(area.GetX(), area.GetY(), area.GetWidth(), area.GetHeight());
            label.setVisible(true);
        }
        _gameLayeredPane.revalidate();
        _gameLayeredPane.repaint();
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
                while (_rightPanel.getComponentCount() > 4) {
                    _rightPanel.remove(_rightPanel.getComponentCount() - 1);
                }

                JLabel headerLabel = new JLabel(header);
                headerLabel.setForeground(Color.GRAY);
                headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 11f));
                headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                _rightPanel.add(headerLabel);
                _rightPanel.add(Box.createVerticalStrut(6));

                for (String option : options) {
                    JButton btn = makeActionButton(option);
                    btn.addActionListener(e -> _inputQueue.offer(option));
                    _rightPanel.add(btn);
                    _rightPanel.add(Box.createVerticalStrut(4));
                }

                _rightPanel.revalidate();
                _rightPanel.repaint();
                if (_pendingCardImages != null && _pendingCardAreas != null) {
                    applySceneCards(_pendingCardImages, _pendingCardAreas);
                    _pendingCardImages = null;
                    _pendingCardAreas  = null;
                }
            });
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
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
        btn.setMaximumSize(new Dimension(180, 32));
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
            _rightPanel.revalidate();
            _rightPanel.repaint();
        });
    }
}