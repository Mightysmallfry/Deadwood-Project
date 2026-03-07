package mothman.gui;

import mothman.utils.TurnDisplayInfo;
import mothman.turnactions.Move;

import javax.swing.*;

public class GameBoardPane extends JLayeredPane {

    private Move selectedMove;

    public GameBoardPane() {
    }

    public void update(TurnDisplayInfo info) {
        // TODO: draw board using info.activeCardImages, info.activeCardAreas, etc
    }

    public Move getMove() {
        return selectedMove;
    }

    public void setMove(Move move) {
        this.selectedMove = move;
    }
}