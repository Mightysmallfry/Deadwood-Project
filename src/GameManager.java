public class GameManager {

    // Members
    private GameBoard _gameBoard;
    private Player[] _playerLibrary;
    private Player _currentPlayer;

    // Constructors
    public GameManager(){

    }

    public GameManager(GameBoard gameBoard)
    {

    }

    public GameManager(GameBoard gameBoard, Player[] players)
    {

    }

    // Getters

    public Player Get_CurrentPlayer() {return _currentPlayer;}

    public GameBoard Get_GameBoard() {return _gameBoard;}

    public Player[] Get_PlayerLibrary() {return _playerLibrary;}

    // Setters

    public void Set_CurrentPlayer(Player _currentPlayer) {this._currentPlayer = _currentPlayer;}

    public void Set_GameBoard(GameBoard _gameBoard) {this._gameBoard = _gameBoard;}

    public void Set_PlayerLibrary(Player[] _playerLibrary) {this._playerLibrary = _playerLibrary;}

    // Methods
    private void UpdateRules()
    {

    }

    private void UpdateGame()
    {

    }

    private void EndDay()
    {

    }

    public int GetPlayerCount()
    {
        return(0); //Temp int so it will work
    }

    public boolean IsEndDay()
    {
        return false;
    }

    public boolean IsEndGame()
    {
        return false;
    }

    public void BasicPay()
    {

    }

    public void StartGame()
    {

    }

    public void EndGame()
    {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("GameManager {\n");

        sb.append("  GameBoard: ");
        sb.append(_gameBoard != null ? _gameBoard.toString() : "null");
        sb.append("\n");

        sb.append("  Current Player: ");
        sb.append(_currentPlayer != null ? _currentPlayer.toString() : "null");
        sb.append("\n");

        sb.append("  Player Count: ");
        sb.append(_playerLibrary != null ? _playerLibrary.length : 0);
        sb.append("\n");

        sb.append("  Players:\n");

        if (_playerLibrary != null) {
            for (int i = 0; i < _playerLibrary.length; i++) {
                sb.append("    [")
                        .append(i)
                        .append("] ")
                        .append(_playerLibrary[i] != null ? _playerLibrary[i].toString() : "null")
                        .append("\n");
            }
        } else {
            sb.append("    null\n");
        }

        sb.append("}");

        return sb.toString();
    }


}
