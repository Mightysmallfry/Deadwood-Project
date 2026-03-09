package mothman.gui;

import mothman.sets.UpgradeData;
import mothman.utils.TurnDisplayInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ActionMenuPanel {

    private final JPanel _panel;
    private final JLabel _turnInfoLabel;
    private final BlockingQueue<String> _inputQueue;

    public ActionMenuPanel(BlockingQueue<String> inputQueue) {
        _inputQueue = inputQueue;

        _panel = new JPanel();
        _panel.setBackground(new Color(40, 40, 40));
        _panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder("Actions");
        border.setTitleColor(Color.ORANGE);
        _panel.setBorder(border);

        _turnInfoLabel = new JLabel();
        _turnInfoLabel.setForeground(Color.LIGHT_GRAY);
        _turnInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _panel.add(_turnInfoLabel);
        _panel.add(Box.createVerticalStrut(10));
    }

    public JPanel getComponent() { return _panel; }

    public JLabel GetTurnInfoLabel() { return _turnInfoLabel; }

    public void update(TurnDisplayInfo info) {
        // Called each turn — buttons are rebuilt by showButtons()
    }

    public void showButtons(ArrayList<String> options, String header) {
        Runnable buildButtons = () -> {
            _inputQueue.clear();
            _panel.removeAll();
            _panel.add(_turnInfoLabel);
            _panel.add(Box.createVerticalStrut(10));

            JLabel headerLabel = new JLabel(header);
            headerLabel.setForeground(Color.GRAY);
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 11f));
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            _panel.add(headerLabel);
            _panel.add(Box.createVerticalStrut(6));

            for (String option : options) {
                JButton btn = makeButton(option);
                btn.addActionListener(e -> _inputQueue.offer(option));
                _panel.add(btn);
                _panel.add(Box.createVerticalStrut(4));
            }

            _panel.revalidate();
            _panel.repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            buildButtons.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(buildButtons);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void SetTurnInfoLabelText(String message) {
        SwingUtilities.invokeLater(() -> {
            _turnInfoLabel.setText("<html><body style='width:170px'>" + message + "</body></html>");
            _panel.revalidate();
            _panel.repaint();
        });
    }

    public String[] ShowUpgradeMenu(int currentRank, int maxRank, ArrayList<UpgradeData> upgrades) {
        ArrayList<String> rankButtons = new ArrayList<>();

        for (int rank = currentRank + 1; rank <= maxRank; rank++) {
            Integer dollarCost = null;
            Integer creditCost = null;

            for (UpgradeData upgrade : upgrades) {
                if (upgrade.GetRank() == rank) {
                    if ("dollar".equals(upgrade.GetCurrencyType())) dollarCost = upgrade.GetCostAmount();
                    if ("credit".equals(upgrade.GetCurrencyType())) creditCost = upgrade.GetCostAmount();
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
            SetTurnInfoLabelText("No upgrades available.");
            return null;
        }

        rankButtons.add("cancel");
        showButtons(rankButtons, "Upgrade  (current rank: " + currentRank + "):");
        String rankChoice = BlockForUpgradeInput();
        if (rankChoice.equals("cancel")) return null;

        int chosenRank;
        try {
            chosenRank = Integer.parseInt(
                    rankChoice.split("\\|")[0].replace("Rank", "").strip());
        } catch (NumberFormatException e) { return null; }

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
        String currencyChoice = BlockForUpgradeInput();
        if (currencyChoice.equals("cancel")) return null;

        return new String[]{ String.valueOf(chosenRank), currencyChoice };
    }

    private String BlockForUpgradeInput() {
        try {
            return _inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "cancel";
        }
    }

    private JButton makeButton(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(_panel.getPreferredSize().width, 32));
        btn.setBackground(new Color(70, 70, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

}