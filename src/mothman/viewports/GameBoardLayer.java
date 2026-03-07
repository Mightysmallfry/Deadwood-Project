package mothman.viewports;

import mothman.gui.GameBoardPane;
import mothman.turnactions.Move;
import mothman.utils.TurnDisplayInfo;

public class GameBoardLayer implements ViewLayer {

    private GameBoardPane gameBoardPane;

    public GameBoardLayer(GameBoardPane gameBoardPanel) {
        this.gameBoardPane = gameBoardPane;
    }

    @Override
    public void update(TurnDisplayInfo info) {
        gameBoardPane.update(info);
    }

    public Move getMove() {
        return gameBoardPane.getMove();
    }

}