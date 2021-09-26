import java.util.ArrayList;
import java.util.List;

/* Game logic, store current info about the game*/
public class Game {
    private Player[] players;
    private int[] score;
    private List<List<String>> table;
    private Player winner;
    private int curr;

    public Game(Player[] players) {
        this.players = players;
        this.table = new ArrayList<>();
        this.score = new int[3];
    }

    public int getCurr() {
        return curr;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean hasToDraw() {
        return players[curr % 3].hasToDraw();
    }

    public void setDraw(String draw) {
        players[curr % 3].getTiles().add(draw);
        players[curr % 3].sort();
        Config.tiles.remove(draw);
    }

    public int getTurn() {
        return curr / 3 + 1;
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

    public void setScore() {
        for (int i = 0; i < players.length; i++) {
            int s = players[i].calculateNetScore();
            score[i] = s;
        }
    }

    public void endTurn() {
        if (win()) {
            winner = players[curr % 3];
            setScore();
        }
        curr++;
    }

    public boolean win() {
        return players[curr % 3].getTiles().size() == 0;
    }
}
