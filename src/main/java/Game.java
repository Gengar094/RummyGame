import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Game logic, store current info about the game*/
public class Game {
    private Player[] players;
    private boolean[] initial = {true, true, true};
    private int[] score;
    private List<List<String>> table;
    private List<List<String>> updatedTable;
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

    public boolean getInitial() {
        return initial[curr % 3];
    }

    public List<List<String>> getTable() {
        return table;
    }

    public List<List<String>> getUpdatedTable() {
        return updatedTable;
    }

    public String getTableString(List<List<String>> table) {
        StringBuilder sb = new StringBuilder();
        if (table == null || table.size() == 0) return "{}";
        for (int i = 0; i < table.size(); i++) {
            sb.append("{");
            for (int j = 0; j < table.get(i).size(); j++) {
                sb.append(table.get(i).get(j));
                if (j != table.get(i).size() - 1) {
                    sb.append(" ");
                }
            }
            sb.append("} ");
        }
        return sb.toString();
    }

    public Player draw() {
        players[curr % 3].drawTiles();
        return players[curr % 3];
    }

    public List<List<String>> play(List<String> melds) {
        if (updatedTable == null) {
            creatUpdatedTable();
        }
        table.add(melds);
        List<String> newList = new ArrayList<>();
        for (String s: melds) {
            newList.add("*" + s);
        }
        updatedTable.add(newList);
        for (String m: melds) {
            players[curr % 3].play(m);
        }
        initial[curr % 3] = false;
        return table;
    }

    public void creatUpdatedTable() {
        updatedTable = new ArrayList<>(table);
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

    public void reuseAndPlay(int meldNum, String[] reuse, String[] play) {
        if (updatedTable == null) {
            creatUpdatedTable();
        }
        List<String> target = table.get(meldNum - 1);
        List<String> updatedTarget = updatedTable.get(meldNum - 1);
        List<String> newMeld = new ArrayList<>();
        List<String> updatedMeld = new ArrayList<>();
        for (int i = 0; i < reuse.length; i++) {
            target.remove(reuse[i]);
            if (!updatedTarget.remove(reuse[i])) {
                if (!updatedTarget.remove("*" + reuse[i])) {
                    updatedTarget.remove("!" + reuse[i]);
                }
            }
            if (target.isEmpty()) {
                table.remove(target);
                updatedTable.remove(updatedTarget);
            }
            newMeld.add(reuse[i]);
            updatedMeld.add("!" + reuse[i]);
        }
        for (int i = 0; i < play.length; i++) {
            players[curr % 3].play(play[i]);
            newMeld.add(play[i]);
            updatedMeld.add(("*" + play[i]));
        }
        table.add(newMeld);
        updatedTable.add(updatedMeld);
    }

    public void addToCurrentMeld(int index, String[] strings) {
        if (updatedTable == null) {
            creatUpdatedTable();
        }
        List<String> target = table.get(index - 1);
        List<String> updatedTarget = updatedTable.get(index - 1);
        for (int i = 0; i < strings.length; i++) {
            players[curr % 3].play(strings[i]);
            target.add(strings[i]);
            updatedTarget.add("*" + strings[i]);
        }
    }

    public void moveTilesOnTable(int from, String[] moved, int to) {
        if (updatedTable == null) {
            creatUpdatedTable();
        }
        List<String> target = table.get(from - 1);
        List<String> updatedTarget = updatedTable.get(from - 1);
        List<String> des = table.get(to - 1);
        List<String> updatedDes = updatedTable.get(to - 1);

        for (int i = 0; i < moved.length; i++) {
            target.remove(moved[i]);
            updatedTarget.remove(moved[i]);
            des.add(moved[i]);
            updatedDes.add("!" + moved[i]);
        }
        if (target.isEmpty()) {
            table.remove(target);
        }
        if (updatedTarget.isEmpty()) {
            updatedTable.remove(updatedTarget);
        }
    }

    public void splitMeld(int index, String[]... args) {
        if (updatedTable == null) {
            creatUpdatedTable();
        }
        table.remove(index - 1);
        updatedTable.remove(index - 1);

        for (int i = 0; i < args.length; i++) {
            table.add(new ArrayList<>(Arrays.asList(args[i])));
            List<String> update = new ArrayList<>();
            for (int j = 0; j < args[i].length; j++) {
                update.add("!" + args[i][j]);
            }
            updatedTable.add(update);
        }

    }

    public void tableRefresh() {
        updatedTable = null;
    }
}
