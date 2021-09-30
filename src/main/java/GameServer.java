import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameServer {
    private Player[] players = new Player[3];
    private Socket[] sockets = new Socket[3];
    private ServerSocket server;
    private String[] table = {"1st", "2nd", "3rd"};
    private Game game;

    private int numPlayers;


    public void reset() {
        this.game = new Game(players);
    }

    public void endTurn() {
        game.endTurn();
    }

    public void draw() {
        game.draw();
    }

    public void play(String[] melds) {
        game.play(new ArrayList<>(Arrays.asList(melds)));
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setDraw(String draw) {
        game.setDraw(draw);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void reuseAndPlay(int meldNum, String[] reuse, String[] play) {
        game.reuseAndPlay(meldNum, reuse, play);
    }

    public void addToCurrentMeld(int i, String[] strings) {
        game.addToCurrentMeld(i, strings);
    }

    public void moveTilesOnTable(int from, String[] moved, int to) {
        game.moveTilesOnTable(from, moved, to);
    }

    public void splitMeld(int index, String[]... args) {
        game.splitMeld(index, args);
    }

    //  ********************* Networking Code below ****************************
    private void start() {
        while (true) {
            for (Socket s: sockets) {
                BufferedWriter writer;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    writer.write("Player " + players[game.getCurr() % 3].getName() + "'s turn" + "\r\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Player " + players[game.getCurr() % 3].getName() + "'s turn");
            // write info to specific player
            Socket currPlayer = sockets[game.getCurr() % 3];
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(currPlayer.getOutputStream()));
                writer.write("You can do following operations :\r\n");
                writer.write("Type 1: Draw from the deck and end this turn\r\n");
                if (!players[game.getCurr() % 3].hasToDraw() || !game.getInitial()) {
                    writer.write("Type 2: Play melds from hand and end this turn\r\n");
                }
                writer.write("Current Table: " + game.getTableString(game.getTable()) + "\r\n");
                writer.write("Current tiles in hand: " + players[game.getCurr() % 3].getTilesString() + "\r\n");
                writer.write("\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // receive decision from player
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(currPlayer.getInputStream()));
                String str = reader.readLine();
                doOperation(Integer.parseInt(str));
                if (Integer.parseInt(str) == 1) {
                    writer = new BufferedWriter(new OutputStreamWriter(currPlayer.getOutputStream()));
                    writer.write("Current tiles in hand: " + players[(game.getCurr() - 1) % 3].getTilesString() + "\r\n");
                    writer.flush();
                }
                if (game.getWinner() != null) {
                    System.out.println("We have a winner: " + game.getWinner().getName());
                    for (int i = 0; i < sockets.length; i++) {
                        writer = new BufferedWriter(new OutputStreamWriter(sockets[i].getOutputStream()));
                        if (players[i] == game.getWinner()) {
                            writer.write("You are the winner" + "\r\n");
                        } else {
                            writer.write("Player: " + game.getWinner().getName() + " is the winner" + "\r\n");
                        }
                        writer.write("The score for each player: " + "\r\n");
                        writer.write("Player " + players[0].getName() + " has got " + players[0].getScore() + " points" + "\r\n");
                        writer.write("Player " + players[1].getName() + " has got " + players[1].getScore() + " points" + "\r\n");
                        writer.write("Player " + players[2].getName() + " has got " + players[2].getScore() + " points" + "\r\n");
                        writer.write("END");
                        writer.write("\n");
                        writer.flush();
                    }
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // little bug when reporting
    private void doOperation(int op) {
        switch (op) {
            case 1:
                game.draw();
                game.endTurn();
                break;
            case 2:
                play();
                break;
        }
    }

    // need to be checked
    private void play() {
        Socket currPlayer = sockets[game.getCurr() % 3];
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(currPlayer.getOutputStream()));
            int count = 0;
            while (true) {
                writer.write("Type 1: play a set or run from your hand" + "\r\n");
                writer.write("Type 2: reuse the tiles on the table" + "\r\n");
                writer.write("Type 3: split a meld into smaller melds" + "\r\n");
                writer.write("Type 4: Move a tile in a meld into another meld on the table" + "\r\n");
                if (count > 0) {
                    writer.write("Type end: If you want to end this turn" + "\r\n");
                }
                if (count == 0) {
                    writer.write("Current table: " + game.getTableString(game.getTable()) + "\r\n");
                } else {
                    writer.write("Current table: " + game.getTableString(game.getUpdatedTable()) + "\r\n");
                }
                writer.write("Your current hand: " + players[game.getCurr() % 3].getTilesString() + "\r\n");
                writer.write("\n");
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(currPlayer.getInputStream()));
                String str = reader.readLine();
                if (str.equals("end")) {
                    game.endTurn();
                    game.tableRefresh();
                    break;
                } else if (str.equals("1")) {
                    writer.write("Please place a comma between each tile which you want to play (e.g R1,R2,R3)" + "\r\n");
                    writer.write("\n");
                    writer.flush();
                    str = reader.readLine();
                    game.play(new ArrayList<>(Arrays.asList(str.split(","))));
                } else if (str.equals("2")) {
                    writer.write("Please typing things like 1,R7|R9,R6|R8 (Reuse tiles R7 R9 from 1st meld on the table, and play R6 R8 from hand to form a new meld)" + "\r\n");
                    writer.write("\n");
                    writer.flush();
                    str = reader.readLine();
                    List<String> choices = new ArrayList<>(Arrays.asList(str.split(",")));
                    int index = Integer.parseInt(choices.get(0));
                    String[] reuse = choices.get(1).split("\\|");
                    String[] play = choices.get(2).split("\\|");
                    game.reuseAndPlay(index, reuse, play);
                } else if (str.equals("3")) {
                    writer.write("Please typing things like 1,R2|R3,R4|R5 (Split the 1st meld {R2, R3, R4, R5} on the table into 2 new \"melds\" {R2, R3}, {R4, R5}" + "\r\n");
                    writer.write("\n");
                    writer.flush();
                    str = reader.readLine();
                    List<String> choices = new ArrayList<>(Arrays.asList(str.split(",")));
                    int index = Integer.parseInt(choices.get(0));
                    String[][] param = new String[choices.size() - 1][];
                    for (int i = 1; i < choices.size(); i++) {
                        String[] smallMeld = choices.get(i).split("\\|");
                        param[i - 1] = smallMeld;
                    }
                    game.splitMeld(index, param);
                } else if (str.equals("4")) {
                    writer.write("Please typing things like 1,R2|R3,4 (Move tiles R2, R3 in the 1st meld into 4th meld on the table"  + "\r\n");
                    writer.write("\n");
                    writer.flush();
                    str = reader.readLine();
                    List<String> choices = new ArrayList<>(Arrays.asList(str.split(",")));
                    int from = Integer.parseInt(choices.get(0));
                    int to = Integer.parseInt(choices.get(2));
                    String[] moved = choices.get(1).split("\\|");
                    game.moveTilesOnTable(from, moved, to);
                }
                count++;
                for (int i = 0; i < sockets.length; i++) {
                    if (i != (game.getCurr() % 3)) {
                        BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(sockets[i].getOutputStream()));
                        writer2.write("Current table after playing by Player " + players[i].getName() + ": " + game.getTableString(game.getTable()));
                        writer2.newLine();
                        writer2.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        this.game = new Game(players);
        start();
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
        System.out.println("End of the game");
        gs.close();
    }

}

