import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class AppTest {
    @DisplayName("Test for each player get 14 random tiles")
    @Test
    public void testSequence() {
        Player p1 = new Player("Bruce");
        assertEquals(p1.getTiles().size(), 14);
        List<String> tiles = p1.getTiles();
        p1 = new Player("Bruce");
        assertFalse(p1.getTiles().equals(tiles)); // almost impossible for getting exactly same tiles
    }
}
