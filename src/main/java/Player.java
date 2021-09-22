import java.io.*;
import java.net.Socket;
import java.util.*;

public class Player implements Serializable {
    private String name;
    private List<String> tiles;
    private Map<Character, Integer> map;

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
    }

    public String getName() {
        return name;
    }

    public List<String> getTiles() {
        return tiles;
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