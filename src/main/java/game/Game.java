package game;
import java.util.*;

/* Game logic, store current info about the game*/
public class Game {
    private Player[] players;
    private boolean[] initial = {true, true, true};
    private boolean[] attemptFirstPlay = {false, false, false};
    private int[] initialScore = {0, 0, 0};
    private int[] score;
    private Map<Integer, List<String>> replaceMap; // <hashcode of melds that have jokers, list of replace tiles>
    private Map<Integer, Boolean> replaceable;     // <hashcode, whether the jokers have been replaced>
    private List<List<String>> table;
    private List<List<String>> updatedTable;
    private Player winner;
    private int curr;
    private List<List<String>> preTable;
    private List<String> preTiles;

    public Game(Player[] players) {
        this.players = players;
        this.table = new ArrayList<>();
        this.score = new int[3];
        replaceMap = new HashMap<>();
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

    public void setInitial(boolean i) {
        initial[curr % 3] = i;
    }

    public void setTable(List<List<String>> table) {
        this.table = table;
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

    public boolean draw() {
        if (Config.tiles.size() == 0) {
            System.out.println("Current deck has no tiles");
            return false;
        } else {
            players[curr % 3].drawTiles();
            return true;
        }
        // return players[curr % 3];
    }

    public List<List<String>> play(List<String> melds) {
        sort(melds);
        if (!isValid(melds)) {
            System.out.println("invalid play");
            return null;
        }
        if (!playerHasMelds(melds)) {
            System.out.println("player does not have melds");
            return null;
        }
        if (!attemptFirstPlay[getCurr() % 3]) {
            preTable = new ArrayList<>(table);
            preTiles = new ArrayList<>(players[getCurr() % 3].getTiles());
        }
        if (initial[getCurr() % 3]) {
            initialScore[getCurr() % 3] += scoreForMelds(melds);
            attemptFirstPlay[getCurr() % 3] = true;
        }
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

    public boolean endTurn() {
        if (attemptFirstPlay[getCurr() % 3] && initialScore[getCurr() % 3] < 30) {
            rollback();
            curr++;
            return false;
        } else {
            initial[getCurr() % 3] = false;
        }
        if (win()) {
            winner = players[curr % 3];
            setScore();
        }
        curr++;
        return true;
    }

    private void rollback() {
        System.out.println("ROLLBACK");
        table = new ArrayList<>(preTable);
        players[getCurr() % 3].setTiles(preTiles);
        attemptFirstPlay[getCurr() % 3] = false;
        initial[getCurr() % 3] = true;
        initialScore[getCurr() % 3] = 0;
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
            updatedTarget.remove(strings[i]);
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
            if (!updatedTarget.remove(moved[i])) {
                if (!updatedTarget.remove("*" + moved[i])) {
                    updatedTarget.remove("!" + moved[i]);
                }
            }
            des.add(moved[i]);
            updatedDes.remove(moved[i]);
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

    // ************************* check for invalid move && wildcards **************************//
    private boolean isValid(List<String> melds) {
        return isRun(melds) || isSet(melds);
    }

    public boolean isRun(List<String> check) {
        if (check.size() < 3 || check.size() > 13) {
            return false;
        }
        int wildcardCount = 0;
        List<String> newList = new ArrayList<>(check);
        sort(newList);
        while (newList.contains("*")) {
            wildcardCount++;
            newList.remove("*");
        }
        char color = newList.get(0).charAt(0);
        int currNum = Integer.parseInt(newList.get(0).substring(1));
        for (String s: newList) {
            if (s.charAt(0) != color) {
                return false;
            }
        }
        if (currNum - wildcardCount >= 2) {
            for (int i = 0; i < newList.size(); i++) {
                if (Integer.parseInt(newList.get(i).substring(1)) != currNum) {
                    if (wildcardCount != 0) {
                        wildcardCount--;
                        currNum++;
                        i--;
                        continue;
                    }
                    return false;
                }
                currNum++;
            }
        } else {
            int[] arr = new int[13];
            for (String s: newList) {
                int pos = Integer.parseInt(s.substring(1));
                if (arr[pos - 1] != 0) { // duplicate tiles
                    return false;
                }
                arr[pos - 1] = pos;
            }
            if (currNum != 1) {
                arr[0] = 1;
                wildcardCount--; // * R2 R3  ....  R12 R13
            }
            if (currNum != 1 && wildcardCount != 0 && arr[1] == 0 && arr[2] != 0) {  // * * R3 ... R12 R13
                arr[1] = 2;
                wildcardCount--;
            }
            if (newList.size() == 13) {
                return true;
            } else {
                int cursor = 0;
                while (arr[cursor] != 0) {
                    cursor++;
                }
                int end = cursor;
                while (arr[cursor] == 0) {
                    cursor++;
                    if (cursor == arr.length) {
                        return true; // e.g R1,R2,R3
                    }
                }
                currNum = arr[cursor];
                for (int i = cursor; i < arr.length + cursor; i++) {
                    if (i % arr.length == end) break;
                    if (arr[i % arr.length] != currNum) {
                        if (wildcardCount != 0) {
                            wildcardCount--;
                            currNum++;
                            if (currNum == 14) {
                                currNum = 1;
                            }
                            continue;
                        }
                        return false;
                    }
                    currNum++;
                    if (currNum == 14) {
                        currNum = 1;
                    }
                }
            }
        }
        return true;
    }

    public boolean isSet(List<String> check) {
        if (check.size() > 4 || check.size() < 3) {
            return false;
        }
        Set<String> set = new HashSet<>();
        int num = Integer.parseInt(check.get(0).substring(1));
        String[] table = {"R", "B", "G", "O"};
        for (String s: check) {
            if (s.equals("*")) {
                for (String color: table) {
                    if (!set.contains(color + num)) {
                        set.add(color + num);
                        break;
                    }
                }
                break;
            } else if (Integer.parseInt(s.substring(1)) != num) {
                return false;
            }
            if (set.contains(s)) {
                return false;
            }
            set.add(s);
        }
        return set.size() == 3 || set.size() == 4;
    }

    private void sort(List<String> check) {
        check.sort((o1, o2) -> {
            if (o1.charAt(0) == '*') {
                return 1;
            } else if (o2.charAt(0) == '*') {
                return -1;
            } else {
                return Integer.parseInt(o1.substring(1)) - Integer.parseInt(o2.substring(1));
            }
        });
    }

    private boolean playerHasMelds(List<String> melds) {
        for (String s: melds) {
            if (!players[getCurr() % 3].getTiles().contains(s)) {
                return false;
            }
        }
        return true;
    }

    private int scoreForMelds(List<String> melds) {
        int sum = 0;
        boolean isRunFlag = true;
        List<String> newList = new ArrayList<>(melds);
        if (isSet(melds)) {
            isRunFlag = false;
        }
         while (newList.contains("*")) {
            // find the largest possible value for joker
            newList.remove("*");
            for (int i = newList.size() - 1; i >= 0; i--) {
                if (newList.get(newList.size() - 1).equals("*")) {
                    continue;
                } else {
                    if (isRunFlag) {
                        // for run
                        String replace;
                        if (Integer.parseInt(newList.get(i).substring(1)) != 13) {
                            replace = String.valueOf(newList.get(i).charAt(0)) + (Integer.parseInt(newList.get(i).substring(1)) + 1);
                        } else {
                            replace = String.valueOf(newList.get(i).charAt(0)) + 1; // maybe improve here
                        }
                        newList.add(replace);
                        if (isRun(newList)) {
                            break;
                        } else {
                            newList.remove(replace);
                        }
                    } else {
                        // for set
                        newList.add("R" + newList.get(i).substring(1));  // the color does not have impact on the initial 30
                        break;
                    }
                }
            }
        }
        for (String s: newList) {
            sum += Integer.parseInt(s.substring(1));
        }
        return sum;
    }

}
