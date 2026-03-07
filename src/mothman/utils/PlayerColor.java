package mothman.utils;

import mothman.player.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerColor {

    private static PlayerColor _instance = new PlayerColor();
    private PlayerColor() {}

    private Map<Player, Color> _playerColors = new HashMap<>();
    private Color[] _colors = new Color[] {
            Color.PINK,
            Color.GREEN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.RED,
            Color.YELLOW,
            Color.CYAN,
            Color.LIGHT_GRAY
    };

    private int _colorIndex = 0;

    public static PlayerColor GetInstance() {
        if (_instance == null) {
            _instance = new PlayerColor();
        }
        return _instance;
    }

    public Color GetColor(Player player)
    {
        if (!_playerColors.containsKey(player)) {
            _playerColors.put(player, _colors[_colorIndex % 8]);
            _colorIndex++;
        }

        return _playerColors.get(player);
    }

}
