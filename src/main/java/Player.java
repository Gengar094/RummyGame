import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player implements Serializable {
    private String name;
    private List<String> tiles;

    public Player(String name) {
        this.name = name;
        this.tiles = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getTiles() {
        return tiles;
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

    private void startGame() {
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