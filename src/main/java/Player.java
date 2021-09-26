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
                if (o1.charAt(0) == o2.charAt(0)) {
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

    public int calculateNetScore() {
        int sum = 0;
        for (String t: tiles) {
            sum += Integer.parseInt(t.substring(1));
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
        System.out.println(index);
        tiles.add(Config.tiles.get(index));
        Config.tiles.remove(index);
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
    public int calculateRun(List<String> currTile) {
        int sum = 0;
        int temp = 0;
        int count = 1;
        while (currTile.size() != 0) {
            for (int i = 0; i < currTile.size(); i++) {
                String tile = currTile.get(i);
                temp += Integer.parseInt(tile.substring(1));
                currTile.remove(tile);
                String nextTile = findNext(tile);
                while (currTile.contains(nextTile)) {
                    temp += Integer.parseInt(nextTile.substring(1));
                    currTile.remove(nextTile);
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

    public String findNext(String tile) {
        int next = Integer.parseInt(tile.substring(1)) + 1;
        String nextTile = tile.charAt(0) + Integer.toString(next);
        return nextTile;
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
        while(true) {
            try {
                System.out.println("START");
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                System.out.println((String) in.readObject());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Fail to receive message from server");
            }
        }
    }

    public void startGame() {
        try {
            Socket socket = new Socket("localhost", Config.GAME_SERVER_PORT_NUMBER);
            ObjectOutputStream dOut = new ObjectOutputStream(socket.getOutputStream());
            dOut.writeObject(this);
            dOut.flush();
            run(socket);
        } catch (IOException e) {
            System.out.println("Fail to join the server");
        }
    }


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("What is your name?");
        String name = s.next();
        s.close();
        Player p = new Player(name);
        p.startGame();
    }

}