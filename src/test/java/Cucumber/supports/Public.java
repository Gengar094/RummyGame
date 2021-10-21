package Cucumber.supports;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import game.*;

public class Public {
    public static GameServer gs = new GameServer();
    public static Player p1 = new Player("Harry");
    public static Player p3 = new Player("Sean");
    public static Player p2 = new Player("Chris");

    @Before
    public void setup() {
        System.out.println("before");
        gs.setPlayers(new Player[] {p1,p2,p3});
        gs.setGame(new Game(new Player[] {p1,p2,p3}));
        System.out.println(Config.tiles.size());
        System.out.println(p1.getTiles().size());
        System.out.println(p2.getTiles().size());
        System.out.println(p3.getTiles().size());
    }

    @After
    public void after() {
        System.out.println("after");
        p1.reset();
        p1.randomizeTiles();
        p2.reset();
        p2.randomizeTiles();
        p3.reset();
        p3.randomizeTiles();
        System.out.println(Config.tiles.size());
    }
}
