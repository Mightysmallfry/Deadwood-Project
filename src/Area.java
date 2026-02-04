public class Area {
    // Members
    private int _x;
    private int _y;
    private int _width;
    private int _height;

    // Constructors
    public Area()
    {
        _x = 0;
        _y = 0;
        _width = 16;
        _height = 16;
    }

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

    public String ToString() {
        return "Area{" +
                "_x=" + _x +
                ", _y=" + _y +
                ", _width=" + _width +
                ", _height=" + _height +
                '}';
    }
}
