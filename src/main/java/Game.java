import java.util.ArrayList;
import java.util.List;

/* Game logic, store current info about the game*/
public class Game {
    private Player[] players;
    private List<List<String>> table;
    private int curr;

    public Game(Player[] players) {
        this.players = players;
        this.table = new ArrayList<>();
    }

    public int getCurr() {
        return curr;
    }

    public List<List<String>> getTable() {
        return table;
    }

    public Player draw() {
        players[curr % 3].drawTiles();
        return players[curr % 3];
    }

    public List<List<String>> play(List<String> melds) {
        table.add(melds);
        for (String m: melds) {
            players[curr % 3].play(m);
        }
        return table;
    }

    public void endTurn() {
        curr++;
    }

    public boolean win() {
        return players[curr % 3].getTiles().size() == 0;
    }
}
