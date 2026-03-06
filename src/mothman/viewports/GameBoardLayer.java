package mothman.viewports;

import mothman.gui.GameBoardPanel;
import mothman.turnactions.Move;
import mothman.utils.TurnDisplayInfo;

public class GameBoardLayer implements ViewLayer {

    private GameBoardPanel gameBoardPanel;

    public GameBoardLayer(GameBoardPanel gameBoardPanel) {
        this.gameBoardPanel = gameBoardPanel;
    }

    @Override
    public void update(TurnDisplayInfo info) {
        gameBoardPanel.update(info);
    }

    public Move getMove() {
        return gameBoardPanel.getMove();
    }

}