public class Area {
    // Members
    private int _x = 0;
    private int _y = 0;
    private int _width = 16;
    private int _height = 16;

    // Constructors
    public Area() {}

    public Area(int x, int y) {
        _x = x;
        _y = y;
        _width = 16;
        _height = 16;
    }

    public Area(int x, int y, int width, int height) {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
    }

    // Methods
    public int GetX(){
        return _x;
    }
    public int GetY(){
        return _y;
    }

    public int GetWidth(){
        return _width;
    }

    public int GetHeight(){
        return _height;
    }

    @Override
    public String toString() {
        return "[" + _x + ", " + _y + ", " + _width + ", " + _height + "]";
    }
}
