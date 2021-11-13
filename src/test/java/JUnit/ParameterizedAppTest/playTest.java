package JUnit.ParameterizedAppTest;

import game.Game;
import game.GameServer;
import game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class playTest {
    public static GameServer gs = new GameServer();
    public static Player p1 = new Player("Harry");
    public static Player p3 = new Player("Sean");
    public static Player p2 = new Player("Chris");

    @BeforeEach
    public void setup() {
        // Simulate the join process
        System.out.println("before");
        gs.setPlayers(new Player[] {p1,p2,p3});
        gs.setGame(new Game(new Player[] {p1,p2,p3}));
    }

    @AfterEach
    public void reset() {
        System.out.println("after");
        p1.reset();
        p1.randomizeTiles();
        p2.reset();
        p2.randomizeTiles();
        p3.reset();
        p3.randomizeTiles();
    }



    @ParameterizedTest
    @ValueSource(strings = {"R6,R7,R8,R9",
            "B9,B10,B11",
            "R10,B10,G10",
            "R8,B8,G8,O8",
            "R1,R2,R3/R8,B8,G8",
            "R10,B10,*",
            "R11,R12,*"})
    public void test_play_for_initial_30(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertFalse(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }
}
