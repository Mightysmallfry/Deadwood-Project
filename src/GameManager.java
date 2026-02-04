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
    //This is used after Deadwood asks for players to change the rules depending on the player number.
    {

    }

    private void UpdateGame()
    //This plays after the player is done with a turn, rewarding the player and checking if the day/game is over.
    {

    }

    private void EndDay()
     //Gets called if there is only one scene card remaining UpdateGame. This removes the SceneCards from the GameSets.
    {

    }

    public int GetPlayerCount()
    {
        return(Get_PlayerLibrary().length);
    }

    public boolean IsEndDay()
    {
        return false;
    }
    //Is there only 1 SceneCard left?

    public boolean IsEndGame()
    {
        return false;
    }
    //Is it the last day?

    public void BasicPay()
    //Pays the players after they are done acting (this is NOT the Scene end pay)
    {

    }

    public int[] TallyScore(Player[] players)
    //as the last day finishes this tallys the score and displays it before the Deadwood ends.
    {
        int[] scores = new int[players.length];
        int credits;
        int coins;
        int rankScore;

        //for each player we count up their points
        for (int i = 0; i < players.length; i++)
        {
            Player p = players[i];
            coins = p.Get_Currency().Get_Coins();
            credits = p.Get_Currency().Get_Credits();
            rankScore = p.Get_CurrentRank() * 5;
            scores[i] = credits + coins + rankScore;
        }
        return scores;
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
