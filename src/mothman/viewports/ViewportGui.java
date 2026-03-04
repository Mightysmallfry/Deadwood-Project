package mothman.viewports;

import mothman.sets.ActingRole;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.utils.TurnDisplayInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewportGui extends JFrame implements Viewport {

    // Main game window
    private JLayeredPane _gameLayeredPane;

    // Labels - images and menus
    private JLabel boardLabel;

    private JLabel curPlayerLabel; // Current Player Display
    private JLabel actionMenuLabel;

//    private JLabel playerLabel; // Player profiles
//    private JLabel cardLabel; // SceneCard images

    // Buttons - actions,
    private JButton bAct;
    private JButton bAcquire;
    private JButton bMove;
    private JButton bRehearse;
    private JButton bUpgrade;
    private JButton bPass;


    // 9 Dice Colors, Reserve white for actual dice rolling.
    // We can then have the colored dice be the players,
    // with the visible face being their rank

    // TODO: Solve how the upgrade menu works
    // TODO: Notify on end turn?
        // On end turn we need to update the current player label
    // TODO: After performing an action
        // update the board and player scores


    public ViewportGui(){
        super("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the board image
        boardLabel = new JLabel();
        ImageIcon ourBoard = new ImageIcon("Assets/board.jpg");
        boardLabel.setIcon(ourBoard);
        boardLabel.setBounds(0, 0, ourBoard.getIconWidth(), ourBoard.getIconHeight());

        // Add the board image to the default layer, it shall be the only one here.
        _gameLayeredPane = getLayeredPane();
        _gameLayeredPane.add(boardLabel, JLayeredPane.DEFAULT_LAYER);

        // TODO: May want to scale the image

        // Set the size of our main window to have some excess room
        setSize(ourBoard.getIconWidth() + 200, ourBoard.getIconHeight() + 50);

        setVisible(true);
    }

    @Override
    public String GetName() {
        return "";
    }

    @Override
    public int GetUpgradeRank() {
        return 0;
    }

    @Override
    public String GetUpgradeCurrency() {
        return "";
    }

    @Override
    public String GetMove(HashMap<String, GameSet> neighbors) {
        return "";
    }

    @Override
    public String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info) {
        return "";
    }

    @Override
    public String GetRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles) {
        return "";
    }

    @Override
    public void DisplayActionList(ArrayList<String> actionList) {

    }

    @Override
    public void DisplayMessage(String message) {

    }
}
