import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class AppTest {
    @AfterEach
    public void reset() {
        Config.tiles = new LinkedList<String>(Arrays.asList(Config.GAME_TILES));
    }
    @DisplayName("Test for each player get 14 random tiles")
    @Test
    public void testSequence() {
        Player p1 = new Player("Bruce");
        assertEquals(p1.getTiles().size(), 14);
        List<String> tiles = p1.getTiles();
        p1 = new Player("Bruce");
        assertFalse(p1.getTiles().equals(tiles)); // almost impossible for getting exactly same tiles
    }

    @DisplayName("Test for players get tiles from one suite (The number of each specific tile can only be 2)")
    @Test
    public void testOneSuite() {
        Player p1 = new Player(), p2 = new Player(), p3 = new Player(), p4 = new Player(), p5 = new Player(), p6 = new Player();
        String[] ss1 = {"R1", "R1", "R2", "R2", "R3", "R3", "R4", "R4", "R5", "R5", "R6", "R6", "R7", "R7"};
        String[] ss2 = {"B1", "B1", "B2", "B2", "B3", "B3", "B4", "B4", "B5", "B5", "B6", "B6", "B7", "B7"};
        String[] ss3 = {"G1", "G1", "G2", "G2", "G3", "G3", "G4", "G4", "G5", "G5", "G6", "G6", "G7", "G7"};
        String[] ss4 = {"O1", "O1", "O2", "O2", "O3", "O3", "O4", "O4", "O5", "O5", "O6", "O6", "O7", "O7"};

        String[] ss5 = {"R8", "R8", "R9", "R9", "R10", "R10", "R11", "R11","R12", "R12", "R13", "R13", "B8", "B8"};
        String[] ss6 = {"B9", "B9","B10", "B10", "B11", "B11", "B12", "B12", "B13", "B13", "G8", "G8", "G9", "G9"};

        p1.setTiles(Arrays.asList(ss1));
        p2.setTiles(Arrays.asList(ss2));
        p3.setTiles(Arrays.asList(ss3));
        p4.setTiles(Arrays.asList(ss4));
        p5.setTiles(Arrays.asList(ss5));
        p6.setTiles(Arrays.asList(ss6));
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(ss1));
        list.addAll(Arrays.asList(ss2));
        list.addAll(Arrays.asList(ss3));
        list.addAll(Arrays.asList(ss4));
        list.addAll(Arrays.asList(ss5));
        list.addAll(Arrays.asList(ss6));

        Player p7 = new Player("new Player");
        assertFalse(p7.getTiles().stream().anyMatch(list::contains));

    }
}
