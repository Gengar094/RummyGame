import io.cucumber.java.bs.A;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class AppTest {
    private static GameServer gs;
    private static Player p1;
    private static Player p2;
    private static Player p3;

    @BeforeAll
    public static void setUp() throws InterruptedException {
        // Simulate the join process
        gs = new GameServer();
        p1 = new Player("Kris");
        p2 = new Player("Nick");
        p3 = new Player("Harry");
        gs.setPlayers(new Player[] {p1, p2, p3});
        gs.setGame(new Game(new Player[] {p1, p2, p3}));

    }
    @AfterEach
    public void reset() {
        gs.reset();
        p1.reset();
        p1.randomizeTiles();
        p2.reset();
        p2.randomizeTiles();
        p3.reset();
        p3.randomizeTiles();
    }

    @DisplayName("Test for each player get 14 random tiles")
    @Test
    public void testRandomTiles() {
        assertEquals(p1.getTiles().size(), 14);
        List<String> tiles = p1.getTiles();
        p1.reset();
        p1.randomizeTiles();
        assertFalse(p1.getTiles().equals(tiles)); // almost impossible for getting exactly same tiles
    }

    @DisplayName("Test for players get tiles from one suite (The number of each specific tile can only be 2)")
    @Test
    public void testOneSuite() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(Arrays.asList("R1", "R1", "R2", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R6", "R7", "R7"));
        p2.setTiles(Arrays.asList("B1", "B1", "B2", "B2", "B3", "B3", "B4", "B4", "B5", "B5", "B6", "B6", "B7", "B7"));
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("R1", "R1", "R2", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R6", "R7", "R7"));
        list.addAll(Arrays.asList("B1", "B1", "B2", "B2", "B3", "B3", "B4", "B4", "B5", "B5", "B6", "B6", "B7", "B7"));


        for (int i = 0; i < 20; i++) {
            p3.reset();
            p3.randomizeTiles();
            assertFalse(p3.getTiles().stream().anyMatch(list::contains));
        }
    }

    @DisplayName("Test for player sequence and UI updates")
    @Test
    public void testSequence() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R12", "B12", "G12", "O12")));
        p3.setTiles(new ArrayList<>(Arrays.asList("R2", "R13", "B2", "B3", "B3", "B4", "B4", "B5", "B5", "B6", "B13", "G2", "G4", "G13")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R11", "R12", "R13", "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10", "G11")));

        gs.draw();
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p1.getTiles().size(), 15);
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 1);
        assertFalse(p2.getTiles().contains("R11"));
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("R13"));
        gs.play(new String[] {"R13", "B13", "G13"});
        gs.play(new String[] {"R2", "B2", "G2"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 3);
        System.out.println(p3.getTiles());
        assertFalse(p3.getTiles().contains("R13"));
        assertFalse(p3.getTiles().contains("B13"));
        assertFalse(p3.getTiles().contains("G13"));
        assertFalse(p3.getTiles().contains("R2"));
        assertFalse(p3.getTiles().contains("B2"));
        assertFalse(p3.getTiles().contains("G2"));
        gs.play(new String[] {"R12", "B12", "O13"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 4);
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("B12"));
        assertFalse(p1.getTiles().contains("O13"));


    }
}
