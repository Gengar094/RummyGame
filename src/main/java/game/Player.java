package game;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Player implements Serializable {
    private String name;
    private List<String> tiles;
    private Map<Character, Integer> map;
    private int score;

    public Player(String name) {
        this.name = name;
        this.tiles = new ArrayList<>();
        this.map = new HashMap();
        map.put('R', 1);
        map.put('B', 2);
        map.put('G', 3);
        map.put('O', 4);
        randomizeTiles();
    }

    public void randomizeTiles() {
        Random r = new Random();
        for (int i = 0; i < 14; i++) {
            int index = r.nextInt(Config.tiles.size());
            tiles.add(Config.tiles.get(index));
            Config.tiles.remove(index);
        }
        sort();
    }

    public void sort() {
        tiles.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.charAt(0) == '*') {
                    return 1;
                } else if (o2.charAt(0) == '*') {
                    return -1;
                } else if (o1.charAt(0) == o2.charAt(0)) {
                    return Integer.parseInt(o1.substring(1)) - Integer.parseInt(o2.substring(1));
                } else {
                    return map.get(o1.charAt(0)) - map.get(o2.charAt(0));
                }
            }
        });
    }

    public void setTiles(List<String> tiles) {
        Config.tiles.addAll(this.tiles);
        for (String t: tiles) {
            Config.tiles.remove(t);
        }
        this.tiles = tiles;
        sort();
    }

    public String getName() {
        return name;
    }

    public List<String> getTiles() {
        return tiles;
    }

    public String getTilesString() {
        StringBuilder sb = new StringBuilder();
        if (tiles.size() == 0) return "{}";
        sb.append("{");
        for (int i = 0; i < tiles.size(); i++) {
            sb.append(tiles.get(i));
            if (i != tiles.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public int calculateNetScore() {
        int sum = 0;
        for (String t: tiles) {
            if (t.equals("*")) {
                sum += 30;
            } else {
                int num = Integer.parseInt(t.substring(1));
                if (num >= 10) {
                    sum += 10;
                } else {
                    sum += num;
                }
            }
        }
        score = -sum;
        return score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void drawTiles() {
        int index = new Random().nextInt(Config.tiles.size());
        tiles.add(Config.tiles.get(index));
        Config.tiles.remove(index);
        sort();
    }

    public void play(String card) {
        tiles.remove(card);
    }

    public boolean hasToDraw() {
        List<String> currTile1 = new ArrayList<>(tiles);
        List<String> currTile2 = new ArrayList<>(tiles);
        int sum1 = calculateSet(currTile1) + calculateRun(currTile1);
        int sum2 = calculateRun(currTile2) + calculateSet(currTile2);
        return Math.max(sum1, sum2) <= 30;
    }

    // "R7" "R8" "R9" "R10"
    public int calculateRun(List<String> currTiles) {
        int sum = 0;
        int temp = 0;
        int count = 1;
        while (currTiles.size() != 0) {
            for (int i = 0; i < currTiles.size(); i++) {
                String tile = currTiles.get(i);
                if (tile.substring(1).equals("1")) {
                    sum += runFor1(currTiles, tile);
                    currTiles.remove(tile);
                    --i;
                    continue;
                }
                temp += Integer.parseInt(tile.substring(1));
                currTiles.remove(tile);
                String nextTile = findNext(tile);
                while (currTiles.contains(nextTile)) {
                    temp += Integer.parseInt(nextTile.substring(1));
                    currTiles.remove(nextTile);
                    nextTile = findNext(nextTile);
                    count++;
                }
                if (count >= 3) {
                    sum += temp;
                }
                temp = 0;
                count = 1;
                --i;
            }
        }
        return sum;
    }

    private int runFor1(List<String> currTiles, String tile) {
        String nextTile = findNext(tile);
        int count = 0;
        int sum = 1;
        while (currTiles.contains(nextTile)) {
            sum += Integer.parseInt(nextTile.substring(1));
            currTiles.remove(nextTile);
            nextTile = findNext(nextTile);
            count++;
        }
        String prevTile = findPrev(tile);
        while (currTiles.contains(prevTile)) {
            sum += Integer.parseInt(prevTile.substring(1));
            currTiles.remove(prevTile);
            prevTile = findPrev(prevTile);
            count++;
        }
        if (count >= 3) {
            return sum;
        }

        return 0;
    }

    public String findPrev(String tile) {
        // Since tile can only be R1
        String prev;
        if (tile.substring(1).equals("1")) {
            prev = tile.charAt(0) + "13";
        } else {
            int p = Integer.parseInt(tile.substring(1)) - 1;
            prev = tile.charAt(0) + Integer.toString(p);
        }
        return prev;
    }

    public String findNext(String tile) {
        int next = Integer.parseInt(tile.substring(1)) + 1;
        return tile.charAt(0) + Integer.toString(next);
    }

    // "R8" "B8" "G8" "O8"
    public int calculateSet(List<String> tiles) {
        Map<Integer, HashSet<Character>> map = new HashMap<>();
        int sum = 0;
        for (String tile: tiles) {
            int target = Integer.parseInt(tile.substring(1));
            if (!map.containsKey(target)) {
                map.put(target, new HashSet<>());
            }
            map.get(target).add(tile.charAt(0));
        }
        for (int i = 1; i < 14; i++) {
            if (map.containsKey(i) && (map.get(i).size() >= 3)) {
                sum += i * map.get(i).size();
            }
        }
        return sum;
    }

    public void reset() {
        Config.tiles.addAll(this.tiles);
        this.tiles = new ArrayList<>();
    }

    //  ********************* Networking Code below ****************************
    private void run(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String str;
                while ((str = reader.readLine()) != null && str.length() != 0) {
                    if (!str.equals("END")) {
                        System.out.println(str);
                    } else {
                        break;
                    }
                }
                if (str.equals("END")) {
                    break;
                }
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Scanner s = new Scanner(System.in);
                String choice = s.next();
                writer.write(choice);
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Fail to receive message from server");
            e.printStackTrace();
        }
    }

    public void startGame() {
        try {
            Socket socket = new Socket("localhost", Config.GAME_SERVER_PORT_NUMBER);
            ObjectOutputStream dOut = new ObjectOutputStream(socket.getOutputStream());
            dOut.writeObject(this);
            dOut.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println((String) in.readObject());
            run(socket);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Fail to join the server");
        }
    }


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("What is your name?");
        String name = s.next();
        Player p = new Player(name);
        p.startGame();
        System.out.println("Game Over");
    }

}