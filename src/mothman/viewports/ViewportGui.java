package mothman.viewports;

import mothman.sets.ActingRole;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.utils.TurnDisplayInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewportGui implements Viewport {
    private JFrame _canvas = new JFrame("Deadwood");
    // 9 Dice Colors, Reserve white for actual dice rolling.
    // We can then have the colored dice be the players,
    // with the visible face being their rank

    // All of these getters are probably going to be listeners for buttons
    // Or other things

    // TODO: Solve how we want present but not active players presented
    // TODO: Solve how the upgrade menu works

    public ViewportGui(){
        _canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _canvas.setSize(800, 600);
        _canvas.setVisible(true);

        // This pauses the program while displaying, Could be useful for starting game
        JOptionPane.showMessageDialog(_canvas,
                "This is a deadwood Message Dialogue, Start Game?",
                "Woah",
                JOptionPane.QUESTION_MESSAGE);
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
