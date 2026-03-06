package mothman.viewports;

import mothman.gui.ScoreBoardPanel;
import mothman.utils.TurnDisplayInfo;

public class ScoreLayer implements ViewLayer {

    private ScoreBoardPanel scoreBoardPanel;

    public ScoreLayer(ScoreBoardPanel scoreBoardPanel) {
        this.scoreBoardPanel = scoreBoardPanel;
    }

    @Override
    public void update(TurnDisplayInfo info) {
        scoreBoardPanel.update(info);
    }

}