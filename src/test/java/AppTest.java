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
        gs.reset(); // game reset
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

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints1() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R11", "R12", "R13", "O12")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 1);
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints2() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R12", "B12","G12", "O12")));
        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 1);
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("B12"));
        assertFalse(p1.getTiles().contains("G12"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints3() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R9", "R10", "R11", "R12", "R13", "G12", "O12")));
        gs.play(new String[] {"R9", "R10", "R11", "R12", "R13"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 1);
        assertFalse(p1.getTiles().contains("R9"));
        assertFalse(p1.getTiles().contains("R10"));
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints4() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R9", "R10", "R11", "R13", "B13", "G13", "O13")));
        gs.play(new String[] {"R13", "B13", "G13", "O13"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 1);
        assertFalse(p1.getTiles().contains("R13"));
        assertFalse(p1.getTiles().contains("B13"));
        assertFalse(p1.getTiles().contains("G13"));
        assertFalse(p1.getTiles().contains("O13"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints5() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "R9", "R10", "R11", "R13", "B7", "B8", "B9", "B10")));
        gs.play(new String[] {"R2", "R3", "R4"});
        gs.play(new String[] {"B7", "B8", "B9"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 2);
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("R4"));
        assertFalse(p1.getTiles().contains("B7"));
        assertFalse(p1.getTiles().contains("B8"));
        assertFalse(p1.getTiles().contains("B9"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints6() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "B2", "B4", "B5", "G4", "O2", "O4", "O5", "O6")));
        gs.play(new String[] {"R2", "B2", "O2"});
        gs.play(new String[] {"G4", "O4", "B4", "R4"});
        gs.play(new String[] {"O5", "B5", "R5"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 3);
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("R4"));
        assertFalse(p1.getTiles().contains("B7"));
        assertFalse(p1.getTiles().contains("B8"));
        assertFalse(p1.getTiles().contains("B9"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints7() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "R8", "G8", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R8", "G8", "O8"});
        gs.play(new String[] {"R2", "R3", "R4"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 2);
        assertFalse(p1.getTiles().contains("R8"));
        assertFalse(p1.getTiles().contains("G8"));
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("R4"));
    }

    //
    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints8() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "B2", "B3", "B5", "B6", "B7", "G2", "G3", "G4", "O2", "O3")));
        gs.play(new String[] {"R2", "O2", "B2"});
        gs.play(new String[] {"G2", "G3", "G4"});
        gs.play(new String[] {"R3", "B3", "O3"});
        gs.play(new String[] {"B5", "B6", "B7"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 4);
        assertFalse(p1.getTiles().contains("R8"));
        assertFalse(p1.getTiles().contains("G8"));
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("R4"));
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPointsAndWin() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "R8", "G8", "O8", "O9", "O10", "O11", "O12", "O13")));
        p2.setTiles(new ArrayList<>(Arrays.asList("B1", "B1", "B2", "B2", "B3", "B3", "B4", "B4", "B5", "B5", "B6", "B6", "B7", "B7")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O1", "O2", "O2", "O3", "O3", "O4", "O4", "O5", "O5", "O6", "O6", "O7", "O7")));
        gs.play(new String[] {"R2", "B2", "G2", "O2"});
        gs.play(new String[] {"G3", "G4", "G5", "G6", "G7"});
        gs.play(new String[] {"O4", "O5", "O6", "O7", "O8"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 3);
        assertEquals(p1.getTiles().size(), 0);
        assertTrue(gs.getGame().getWinner(), p1);
        assertEquals(gs.getGame().getWinner().getScore(), 112);
        assertEquals(p2.getScore(), -56);
        assertEquals(p3.getScore(), -56);
    }


}
