import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private Player[] players = new Player[3];
    private Socket[] sockets = new Socket[3];
    private ServerSocket server;
    private String[] table = {"1st", "2nd", "3rd"};

    private int numPlayers;


    // read each player in array, send current table to them, receive operations from player
    private void start() {
        System.out.println("To do");
    }


    // Networking Code
    public GameServer() {
        System.out.println("Starting the game server");
        this.numPlayers = 0;

        try {
            this.server = new ServerSocket(Config.GAME_SERVER_PORT_NUMBER);
        } catch (IOException e) {
            System.out.println("Server fails to open");
        }
    }

    public void acceptConnections() {
        System.out.println("Waiting for players...");
        while (numPlayers < 3) {
            try {
                Socket s = server.accept();
                sockets[numPlayers] = s;
                ObjectInputStream dIn = new ObjectInputStream(s.getInputStream());
                players[numPlayers] = (Player) dIn.readObject();
                System.out.println("Player " + players[numPlayers].getName() + " has joined the game");
                ObjectOutputStream dOut = new ObjectOutputStream(s.getOutputStream());
                dOut.writeObject("You are " + table[numPlayers] + " player in this game");
                dOut.flush();
                numPlayers++;
            } catch (IOException e) {
                System.out.println("Fail to have 3 players");
            } catch (ClassNotFoundException e) {
                System.out.println("Player cannot connect");
            }
        }

        System.out.println("We have three connected players, now let's get started");
    }


    private void close() {
        try {
            server.close();
        } catch (IOException e) {
            System.out.println("Error happened when closing the game");
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
        gs.start();
        System.out.println("End of the game");
        gs.close();
    }

}

