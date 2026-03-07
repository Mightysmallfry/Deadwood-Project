package mothman.utils;

import java.awt.*;

public class PlayerColor {

    private static PlayerColor _instance = new PlayerColor();
    private PlayerColor() {}

    private Color[] _colors = new Color[] {
            Color.CYAN,             // 0
            Color.GREEN,            // 1
            Color.MAGENTA,          // 2
            Color.ORANGE,           // 3
            Color.PINK,             // 4
            Color.RED,              // 5
            Color.YELLOW,           // 6
            Color.LIGHT_GRAY        // 7
    };

    private int _playerIterator;

    public static PlayerColor GetInstance() {
        if (_instance == null) {
            _instance = new PlayerColor();
        }
        return _instance;
    }

    public Color GetNextColor()
    {
        int colorIndex = _playerIterator % 8;
        _playerIterator++;
        return _colors[colorIndex];
    }

}
