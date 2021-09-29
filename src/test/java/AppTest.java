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
        assertTrue(gs.getGame().getTable().size() == 0);


        gs.draw();
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p1.getTiles().size(), 15);
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertFalse(p2.getTiles().contains("R11"));
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("R13"));
        gs.play(new String[] {"R13", "B13", "G13"});
        gs.play(new String[] {"R2", "B2", "G2"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R13", "B13", "G13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "B2", "G2")));
        assertFalse(p3.getTiles().contains("R13"));
        assertFalse(p3.getTiles().contains("B13"));
        assertFalse(p3.getTiles().contains("G13"));
        assertFalse(p3.getTiles().contains("R2"));
        assertFalse(p3.getTiles().contains("B2"));
        assertFalse(p3.getTiles().contains("G2"));
        gs.play(new String[] {"R12", "B12", "O13"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "O13")));
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
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        assertEquals(p1.getTiles().size(), 11);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints2() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R12", "B12","G12", "O12")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "G12")));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("B12"));
        assertFalse(p1.getTiles().contains("G12"));
        assertEquals(p1.getTiles().size(), 11);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints3() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R9", "R10", "R11", "R12", "R13", "G12", "O12")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R9", "R10", "R11", "R12", "R13"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R9", "R10", "R11", "R12", "R13")));
        assertFalse(p1.getTiles().contains("R9"));
        assertFalse(p1.getTiles().contains("R10"));
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        assertEquals(p1.getTiles().size(), 9);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints4() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R3", "R4", "R4", "R9", "R10", "R11", "R13", "B13", "G13", "O13")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R13", "B13", "G13", "O13"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R13", "B13", "G13", "O13")));
        assertFalse(p1.getTiles().contains("R13"));
        assertFalse(p1.getTiles().contains("B13"));
        assertFalse(p1.getTiles().contains("G13"));
        assertFalse(p1.getTiles().contains("O13"));
        assertEquals(p1.getTiles().size(), 10);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints5() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "R9", "R10", "R11", "R13", "B7", "B8", "B9", "B10")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R2", "R3", "R4"});
        gs.play(new String[] {"B7", "B8", "B9"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "R3", "R4")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B7", "B8", "B9")));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("R4"));
        assertFalse(p1.getTiles().contains("B7"));
        assertFalse(p1.getTiles().contains("B8"));
        assertFalse(p1.getTiles().contains("B9"));
        assertEquals(p1.getTiles().size(), 8);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints6() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "B2", "B4", "B5", "G4", "O2", "O4", "O5", "O6")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R2", "B2", "O2"});
        gs.play(new String[] {"G4", "O4", "B4", "R4"});
        gs.play(new String[] {"O5", "B5", "R5"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "B2", "O2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G4", "O4", "B4", "R4")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O5", "B5", "R5")));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("B2"));
        assertFalse(p1.getTiles().contains("O2"));
        assertFalse(p1.getTiles().contains("G4"));
        assertFalse(p1.getTiles().contains("O4"));
        assertFalse(p1.getTiles().contains("B4"));
        assertFalse(p1.getTiles().contains("R4"));
        assertFalse(p1.getTiles().contains("O5"));
        assertFalse(p1.getTiles().contains("B5"));
        assertFalse(p1.getTiles().contains("R5"));
        assertEquals(p1.getTiles().size(), 4);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints7() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "R4", "R5", "R8", "G8", "O8", "O9", "O10", "O11", "O12", "O13")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R8", "G8", "O8"});
        gs.play(new String[] {"R2", "R3", "R4"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R8", "G8", "O8")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "R3", "R4")));
        assertFalse(p1.getTiles().contains("R8"));
        assertFalse(p1.getTiles().contains("G8"));
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("R4"));
        assertEquals(p1.getTiles().size(), 8);
    }

    //
    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPoints8() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R1", "R2", "R3", "B2", "B3", "B5", "B6", "B7", "G2", "G3", "G4", "O2", "O3")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R2", "O2", "B2"});
        gs.play(new String[] {"G2", "G3", "G4"});
        gs.play(new String[] {"R3", "B3", "O3"});
        gs.play(new String[] {"B5", "B6", "B7"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "O2", "B2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "G3", "G4")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R3", "B3", "O3")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B5", "B6", "B7")));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("O2"));
        assertFalse(p1.getTiles().contains("B2"));
        assertFalse(p1.getTiles().contains("G2"));
        assertFalse(p1.getTiles().contains("G3"));
        assertFalse(p1.getTiles().contains("G4"));
        assertFalse(p1.getTiles().contains("R3"));
        assertFalse(p1.getTiles().contains("B3"));
        assertFalse(p1.getTiles().contains("O3"));
        assertFalse(p1.getTiles().contains("B5"));
        assertFalse(p1.getTiles().contains("B6"));
        assertFalse(p1.getTiles().contains("B7"));
        assertEquals(p1.getTiles().size(), 2);
    }

    @DisplayName("Tests for initial 30 points")
    @Test
    public void testInitialPointsAndWin() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "B2", "G2", "G3", "G4", "G5", "G6", "G7", "O2", "O4", "O5", "O6", "O7", "O8")));
        p2.setTiles(new ArrayList<>(Arrays.asList("B1", "B1", "B2", "B2", "B3", "B3", "B4", "B4", "B5", "B5", "B6", "B6", "B7", "B7")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O1", "O2", "O2", "O3", "O3", "O4", "O4", "O5", "O5", "O6", "O6", "O7", "O7")));
        assertEquals(0, gs.getGame().getTable().size());

        gs.play(new String[] {"R2", "B2", "G2", "O2"});
        gs.play(new String[] {"G3", "G4", "G5", "G6", "G7"});
        gs.play(new String[] {"O4", "O5", "O6", "O7", "O8"});
        gs.endTurn();
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "B2", "G2", "O2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G3", "G4", "G5", "G6", "G7")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O4", "O5", "O6", "O7", "O8")));
        assertEquals(p1.getTiles().size(), 0); // no tiles in hand
        assertEquals(gs.getGame().getWinner(), p1);
        assertEquals(gs.getGame().getWinner().getScore(), 0);
        assertEquals(p2.getScore(), -56);
        assertEquals(p3.getScore(), -56);
    }

    @DisplayName("Tests for playing melds after initial 30")
    @Test
    public void testAfterInitial30_63() {
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "R3", "R8", "R11", "R12", "R13", "G2", "G3", "G4", "G8", "G9", "G10", "G11", "G12")));
        p2.reset();
        p2.setTiles(new ArrayList<>(Arrays.asList("B11", "B12", "B13", "G1", "G1", "G5", "G5", "G6", "G6", "G7", "G7", "G8", "G8", "G9")));
        p3.reset();
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O2", "O2", "O3", "O4", "O5", "O6", "O7", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        gs.play(new String[] {"B11", "B12", "B13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("B11"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("B13"));
        gs.play(new String[] {"O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("O11"));
        assertFalse(p2.getTiles().contains("O12"));
        assertFalse(p2.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B11", "B12", "B13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O11", "O12", "O13")));

        // Turn 2
        gs.play(new String[] {"G2", "G3", "G4"});
        gs.endTurn();

        assertEquals(gs.getGame().getTurn(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "G3", "G4")));
        assertFalse(p1.getTiles().contains("G2"));
        assertFalse(p1.getTiles().contains("G3"));
        assertFalse(p1.getTiles().contains("G4"));
        assertEquals(p1.getTiles().size(), 8);
    }

    @DisplayName("Tests for playing melds after initial 30")
    @Test
    public void testAfterInitial30_65() {
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "R3", "R8", "R11", "R12", "R13", "G2", "G3", "G4", "G8", "G9", "G10", "G11", "O2")));
        p2.reset();
        p2.setTiles(new ArrayList<>(Arrays.asList("B11", "B12", "B13", "G1", "G1", "G5", "G5", "G6", "G6", "G7", "G7", "G8", "G8", "G9")));
        p3.reset();
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O2", "O3", "O3", "O4", "O5", "O6", "O7", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        gs.play(new String[] {"B11", "B12", "B13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("B11"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("B13"));
        gs.play(new String[] {"O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("O11"));
        assertFalse(p2.getTiles().contains("O12"));
        assertFalse(p2.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B11", "B12", "B13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O11", "O12", "O13")));

        // Turn 2
        gs.play(new String[] {"G2", "R2", "O2"});
        gs.endTurn();

        assertEquals(gs.getGame().getTurn(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "R2", "O2")));
        assertFalse(p1.getTiles().contains("G2"));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("O2"));
        assertEquals(p1.getTiles().size(), 8);

    }


    @DisplayName("Tests for playing melds after initial 30")
    @Test
    public void testAfterInitial30_64() {
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R11", "R12", "R13", "G2", "G3", "G4", "O1", "O2", "O3", "O4", "O5", "O8", "O9", "O10")));
        p2.reset();
        p2.setTiles(new ArrayList<>(Arrays.asList("B11", "B12", "B13", "G1", "G1", "G5", "G5", "G6", "G6", "G7", "G7", "G8", "G8", "G9")));
        p3.reset();
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O2", "O3", "O3", "O4", "O5", "O6", "O7", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        gs.play(new String[] {"B11", "B12", "B13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("B11"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("B13"));
        gs.play(new String[] {"O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("O11"));
        assertFalse(p2.getTiles().contains("O12"));
        assertFalse(p2.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B11", "B12", "B13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O11", "O12", "O13")));

        // Turn 2
        gs.play(new String[] {"G2", "G3", "G4"});
        gs.play(new String[] {"O8", "O9", "O10"});
        gs.endTurn();

        assertEquals(gs.getGame().getTurn(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "G3", "G4")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O8", "O9", "O10")));
        assertFalse(p1.getTiles().contains("G2"));
        assertFalse(p1.getTiles().contains("G3"));
        assertFalse(p1.getTiles().contains("G4"));
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("O9"));
        assertFalse(p1.getTiles().contains("O10"));
        assertEquals(p1.getTiles().size(), 5);
    }


    @DisplayName("Tests for playing melds after initial 30")
    @Test
    public void testAfterInitial30_66() {
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "R3", "R8", "R11", "R12", "R13", "B8", "G2", "G4", "G8", "O2", "O8", "O9", "O10")));
        p2.reset();
        p2.setTiles(new ArrayList<>(Arrays.asList("B1", "B11", "B12", "B13", "G1", "G1", "G5", "G5", "G6", "G6", "G7", "G7", "G8", "G9")));
        p3.reset();
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O2", "O3", "O3", "O4", "O5", "O6", "O7", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        gs.play(new String[] {"B11", "B12", "B13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("B11"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("B13"));
        gs.play(new String[] {"O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("O11"));
        assertFalse(p2.getTiles().contains("O12"));
        assertFalse(p2.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B11", "B12", "B13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O11", "O12", "O13")));

        // Turn 2
        gs.play(new String[] {"G2", "R2", "O2"});
        gs.play(new String[] {"O8", "R8", "B8", "G8"});
        gs.endTurn();

        assertEquals(gs.getGame().getTurn(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "R2", "O2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O8", "R8", "B8", "G8")));
        assertFalse(p1.getTiles().contains("G2"));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("O2"));
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("R8"));
        assertFalse(p1.getTiles().contains("B8"));
        assertFalse(p1.getTiles().contains("G8"));
        assertEquals(p1.getTiles().size(), 4);
    }

    @DisplayName("Tests for playing melds after initial 30")
    @Test
    public void testAfterInitial30_67() {
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "R3", "R8", "R11", "R12", "R13", "G2", "G3", "G4", "G8", "O2", "O8", "O9", "O10")));
        p2.reset();
        p2.setTiles(new ArrayList<>(Arrays.asList("B11", "B12", "B13", "G1", "G1", "G5", "G5", "G6", "G6", "G7", "G7", "G8", "G8", "G9")));
        p3.reset();
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O2", "O3", "O3", "O4", "O5", "O6", "O7", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        gs.play(new String[] {"B11", "B12", "B13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("B11"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("B13"));
        gs.play(new String[] {"O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("O11"));
        assertFalse(p2.getTiles().contains("O12"));
        assertFalse(p2.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "R12", "R13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("B11", "B12", "B13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O11", "O12", "O13")));

        // Turn 2
        gs.play(new String[] {"G2", "R2", "O2"});
        gs.play(new String[] {"O8", "O9", "O10"});
        gs.endTurn();

        assertEquals(gs.getGame().getTurn(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "R2", "O2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O8", "O9", "O10")));
        assertFalse(p1.getTiles().contains("G2"));
        assertFalse(p1.getTiles().contains("R2"));
        assertFalse(p1.getTiles().contains("O2"));
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("O9"));
        assertFalse(p1.getTiles().contains("O10"));
        assertEquals(p1.getTiles().size(), 5);

    }

    // TODO
    @DisplayName("Tests for playing melds after initial 30")
    @Test
    public void testAfterInitial30_68() {
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "R3", "R11", "R12", "R13", "G2", "G3", "G8", "G9", "G10", "G11", "G12", "O2", "O3")));
        p2.reset();
        p2.setTiles(new ArrayList<>(Arrays.asList("B11", "B12", "B13", "G1", "G1", "G5", "G5", "G6", "G6", "G7", "G7", "G8", "G9", "G11")));
        p3.reset();
        p3.setTiles(new ArrayList<>(Arrays.asList("O1", "O2", "O3", "O4", "O4", "O5", "O6", "O7", "O8", "O9", "O10", "O11", "O12", "O13")));
        gs.play(new String[] {"R11", "R12", "R13"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        gs.play(new String[] {"B11", "B12", "B13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("B11"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("B13"));
        gs.play(new String[] {"O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("O11"));
        assertFalse(p2.getTiles().contains("O12"));
        assertFalse(p2.getTiles().contains("O13"));

        // Turn 2
        gs.play(new String[] {"G2", "R2", "O2"});
        gs.play(new String[] {"G3", "R3", "O3"});
        gs.play(new String[] {"G8", "G9", "G10", "G11", "G12"});
        gs.endTurn();

        assertEquals(gs.getGame().getTurn(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G2", "R2", "O2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G3", "R3", "O3")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G8", "G9", "G10", "G11", "G12")));
        assertEquals(p1.getTiles().size(), 0); // no tiles for p1
        assertEquals(gs.getGame().getWinner(), p1);

    }

    @DisplayName("Test for a player having or choosing to draw a tile -- choose to draw")
    @Test
    public void testAPlayerChooseToDraw() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R2", "R3", "R7", "R8", "R9", "R10", "G2", "G3", "G12", "O2", "O3", "O8", "O9", "O10")));
        assertFalse(gs.getGame().hasToDraw());
        gs.draw();
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p1.getTiles().size(), 15);
    }

    @DisplayName("Test for a player having or choosing to draw a tile -- choose to draw")
    @Test
    public void testChooseToDrawOwnCase() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R1", "R2", "R3", "O3", "R9", "R10", "G2", "G3", "G12", "O2", "O3", "O8", "R12", "R13"))); // R12,R13,R1,R2,R3
        assertFalse(gs.getGame().hasToDraw());
        gs.draw();
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p1.getTiles().size(), 15);
    }

    @DisplayName("Test for a player having or choosing to draw a tile -- has to draw")
    @Test
    public void testAPlayerHasToDraw() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R3", "R5", "R9", "R10", "B3", "B3", "B6", "B12", "B13", "G2", "G2", "G12", "O2", "O7")));
        assertTrue(gs.getGame().hasToDraw());
        gs.draw();
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p1.getTiles().size(), 15);
    }

    @DisplayName("Test for declaring a winner upon a player playing all tiles and reporting correct scores")
    @Test
    public void testWinnerAndScores() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R3", "R5", "R9", "R10", "B3", "B3", "B6", "B12", "B13", "G2", "G2", "G11", "O2", "O7")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R2", "B2", "G2", "G3", "G4", "G6", "G7", "O2", "O4", "O5", "O6", "O7", "O8", "O9")));
        p3.setTiles(new ArrayList<>(Arrays.asList("R4", "R7", "R10", "R11", "R12", "R13", "B7", "B10", "B11", "B12", "B13", "G8", "O6", "O6")));
        gs.setDraw("R2");
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p1.getTiles().size(), 15);
        gs.setDraw("G5");
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 0);
        assertEquals(p2.getTiles().size(), 15);
        gs.play(new String[] {"R10", "R11", "R12", "R13"});
        gs.play(new String[] {"B10", "B11", "B12", "B13"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 2);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R10", "R11", "R12", "R13")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R10", "R11", "R12", "R13")));
        assertFalse(p3.getTiles().contains("R10"));
        assertFalse(p3.getTiles().contains("R11"));
        assertFalse(p3.getTiles().contains("R12"));
        assertFalse(p3.getTiles().contains("R13"));
        assertFalse(p3.getTiles().contains("B10"));
        assertFalse(p3.getTiles().contains("B11"));
        assertFalse(p3.getTiles().contains("B12"));
        assertFalse(p3.getTiles().contains("B13"));
        gs.play(new String[] {"R2", "G2", "O2"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 3);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "G2", "O2")));
        assertFalse(p1.getTiles().contains("R2"));
        assertEquals(p1.getTiles().indexOf("G2"), p1.getTiles().lastIndexOf("G2")); // to ensure p1 has only 1 G2 left after playing another G2
        assertFalse(p1.getTiles().contains("O2"));
        gs.play(new String[] {"R2", "B2", "G2", "O2"});
        gs.play(new String[] {"G3", "G4", "G5", "G6", "G7"});
        gs.play(new String[] {"O4", "O5", "O6", "O7", "O8", "O9"});
        gs.endTurn();
        assertEquals(gs.getGame().getTable().size(), 6);
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R2", "B2", "G2", "O2")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("G3", "G4", "G5", "G6", "G7")));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O4", "O5", "O6", "O7", "O8", "O9")));
        assertEquals(p2.getTiles().size(), 0);
        assertEquals(gs.getGame().getWinner(), p2);
        assertEquals(gs.getGame().getWinner().getScore(), 0);
        System.out.println(p1.getTiles());
        assertEquals(p1.getScore(), -78);
        assertEquals(p3.getScore(), -38);
    }


    @DisplayName("Test for completing a partial set from a hand by reusing from a set of 4 of the table")
    @Test
    public void test_87() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R11", "B11", "G11", "O11", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R12", "B12", "G12", "R11", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "G11", "B11")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13", "B1", "B2", "B3", "B4", "B5", "B6", "B7")));
        gs.play(new String[] {"R11", "O11", "B11", "G11"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("O11"));
        assertFalse(p1.getTiles().contains("B11"));
        assertFalse(p1.getTiles().contains("G11"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "O11", "B11", "G11")));
        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("G12"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "G12")));
        gs.play(new String[] {"O7", "O8", "O9", "O10", "O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p3.getTiles().contains("O7"));
        assertFalse(p3.getTiles().contains("O8"));
        assertFalse(p3.getTiles().contains("O9"));
        assertFalse(p3.getTiles().contains("O10"));
        assertFalse(p3.getTiles().contains("O11"));
        assertFalse(p3.getTiles().contains("O12"));
        assertFalse(p3.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13")));


        // 2
        gs.draw();
        gs.endTurn();
        gs.reuseAndPlay(1, new String[] {"B11"}, new String[] {"R11", "G11"});
        gs.endTurn();
        assertEquals(p2.getTiles().size(), 9);
        assertFalse(p2.getTiles().contains("R11"));
        assertFalse(p2.getTiles().contains("G11"));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("B11", "R11", "G11"))));
    }



    @DisplayName("Test for completing a partial run from a hand by reusing from a set of 4 of the table")
    @Test
    public void test_91() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R11", "B11", "G11", "O11", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R12", "B12", "G12", "R12", "R13", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "B13")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13", "B1", "B2", "B3", "B4", "B5", "B6", "B7")));
        gs.play(new String[] {"R11", "O11", "B11", "G11"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("O11"));
        assertFalse(p1.getTiles().contains("B11"));
        assertFalse(p1.getTiles().contains("G11"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "O11", "B11", "G11")));
        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertEquals(p2.getTiles().indexOf("R12"), p2.getTiles().lastIndexOf("R12"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("G12"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "G12")));
        gs.play(new String[] {"O7", "O8", "O9", "O10", "O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p3.getTiles().contains("O7"));
        assertFalse(p3.getTiles().contains("O8"));
        assertFalse(p3.getTiles().contains("O9"));
        assertFalse(p3.getTiles().contains("O10"));
        assertFalse(p3.getTiles().contains("O11"));
        assertFalse(p3.getTiles().contains("O12"));
        assertFalse(p3.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13")));


        // 2
        gs.draw();
        gs.endTurn();
        gs.reuseAndPlay(1, new String[] {"R11"}, new String[] {"R12", "R13"});
        gs.endTurn();
        assertEquals(p2.getTiles().size(), 9);
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("R13"));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R11", "R12", "R13"))));
    }


    @DisplayName("Test for completing a partial set from a hand by reusing from a run of the table")
    @Test
    public void test_96() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R11", "B11", "G11", "O11", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "B7", "R13", "B13")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R12", "B12", "G12", "R7", "R13", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "B13")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13", "B1", "B2", "B3", "B4", "B5", "B6", "B8")));
        gs.play(new String[] {"R11", "O11", "B11", "G11"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("O11"));
        assertFalse(p1.getTiles().contains("B11"));
        assertFalse(p1.getTiles().contains("G11"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "O11", "B11", "G11")));
        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("G12"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "G12")));
        gs.play(new String[] {"O7", "O8", "O9", "O10", "O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p3.getTiles().contains("O7"));
        assertFalse(p3.getTiles().contains("O8"));
        assertFalse(p3.getTiles().contains("O9"));
        assertFalse(p3.getTiles().contains("O10"));
        assertFalse(p3.getTiles().contains("O11"));
        assertFalse(p3.getTiles().contains("O12"));
        assertFalse(p3.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13")));


        // 2
        gs.reuseAndPlay(3, new String[] {"O7"}, new String[] {"R7", "B7"});
        gs.endTurn();
        assertEquals(p1.getTiles().size(), 8);
        assertFalse(p1.getTiles().contains("R7"));
        assertFalse(p1.getTiles().contains("B7"));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("O7", "R7", "B7"))));

    }


    @DisplayName("Test for completing a partial set from a hand by reusing from a run of the table")
    @Test
    public void test_98() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R11", "B11", "G11", "O11", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "B7", "R13", "B13")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R12", "B12", "G12", "R7", "R13", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "B13")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13", "B1", "B2", "B3", "B4", "B5", "B6", "B8")));
        gs.play(new String[] {"R11", "O11", "B11", "G11"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("O11"));
        assertFalse(p1.getTiles().contains("B11"));
        assertFalse(p1.getTiles().contains("G11"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "O11", "B11", "G11")));
        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("G12"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "G12")));
        gs.play(new String[] {"O7", "O8", "O9", "O10", "O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p3.getTiles().contains("O7"));
        assertFalse(p3.getTiles().contains("O8"));
        assertFalse(p3.getTiles().contains("O9"));
        assertFalse(p3.getTiles().contains("O10"));
        assertFalse(p3.getTiles().contains("O11"));
        assertFalse(p3.getTiles().contains("O12"));
        assertFalse(p3.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13")));


        // 2
        gs.reuseAndPlay(3, new String[] {"O13"}, new String[] {"R13", "B13"});
        gs.endTurn();
        assertEquals(p1.getTiles().size(), 8);
        assertFalse(p1.getTiles().contains("R12"));
        assertFalse(p1.getTiles().contains("R13"));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("O13", "R13", "B13"))));

    }



    @DisplayName("Test for completing a partial run from a hand by reusing from run of the table")
    @Test
    public void test_101() {
        p1.reset();
        p2.reset();
        p3.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R11", "B11", "G11", "O11", "O8", "O9", "R3", "R4", "R5", "R6", "R7", "B7", "R13", "B13")));
        p2.setTiles(new ArrayList<>(Arrays.asList("R12", "B12", "G12", "R7", "R13", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "B13")));
        p3.setTiles(new ArrayList<>(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13", "B1", "B2", "B3", "B4", "B5", "B6", "B8")));
        gs.play(new String[] {"R11", "O11", "B11", "G11"});
        gs.endTurn();
        assertFalse(p1.getTiles().contains("R11"));
        assertFalse(p1.getTiles().contains("O11"));
        assertFalse(p1.getTiles().contains("B11"));
        assertFalse(p1.getTiles().contains("G11"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R11", "O11", "B11", "G11")));
        gs.play(new String[] {"R12", "B12", "G12"});
        gs.endTurn();
        assertFalse(p2.getTiles().contains("R12"));
        assertFalse(p2.getTiles().contains("B12"));
        assertFalse(p2.getTiles().contains("G12"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("R12", "B12", "G12")));
        gs.play(new String[] {"O7", "O8", "O9", "O10", "O11", "O12", "O13"});
        gs.endTurn();
        assertFalse(p3.getTiles().contains("O7"));
        assertFalse(p3.getTiles().contains("O8"));
        assertFalse(p3.getTiles().contains("O9"));
        assertFalse(p3.getTiles().contains("O10"));
        assertFalse(p3.getTiles().contains("O11"));
        assertFalse(p3.getTiles().contains("O12"));
        assertFalse(p3.getTiles().contains("O13"));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList("O7", "O8", "O9", "O10", "O11", "O12", "O13")));


        // 2
        gs.reuseAndPlay(3, new String[] {"O10"}, new String[] {"O8", "O9"});
        gs.endTurn();
        assertEquals(p1.getTiles().size(), 8);
        assertFalse(p1.getTiles().contains("O8"));
        assertFalse(p1.getTiles().contains("O9"));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("O10", "O8", "O9"))));

    }

}
