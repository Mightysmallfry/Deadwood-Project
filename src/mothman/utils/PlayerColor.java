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
            Color.BLUE
    };

    private Map<Color, String> _colorDicePrefix = new HashMap<>(){{
        put(Color.PINK, "p");
        put(Color.GREEN, "g");
        put(Color.MAGENTA, "v");    // Magenta/Violet
        put(Color.ORANGE, "o");
        put(Color.RED, "r");
        put(Color.YELLOW, "y");
        put(Color.CYAN, "c");
        put(Color.BLUE, "b");
    }};

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

    public String GetColorPrefix(Player player)
    {
        Color playerColor = GetColor(player);
        return _colorDicePrefix.get(playerColor);
    }


}
