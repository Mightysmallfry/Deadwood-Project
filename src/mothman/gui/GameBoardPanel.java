package mothman.gui;

import mothman.utils.TurnDisplayInfo;
import mothman.turnactions.Move;

public class GameBoardPanel {

    private Move selectedMove;

    public GameBoardPanel() {
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