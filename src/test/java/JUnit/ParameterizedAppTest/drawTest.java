package JUnit.ParameterizedAppTest;

import Cucumber.supports.Public;
import game.Game;
import game.GameServer;
import game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class drawTest {
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

    @Test
    public void testSuccessfullyDraw() {
        int curr = gs.getGame().getDeck().size();
        // Given
        assertEquals(gs.getCurrentPlayer().getTiles().size(), 14);
        assertNotEquals(curr, 0);
        // When
        gs.draw();
        // Then
        assertEquals(gs.getCurrentPlayer().getTiles().size(), 15);
        assertEquals(gs.getGame().getDeck().size(), curr - 1);

        
    }

    @Test
    public void testFailToDraw() {
        // Given
        assertEquals(gs.getCurrentPlayer().getTiles().size(), 14);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 32; j++) {  // (14 + 32) * 2 + 14 = 106
                gs.draw();
            }
            gs.endTurn();
        }

        assertEquals(gs.getGame().getDeck().size(), 0);

        // When
        gs.draw();

        // Then
        assertEquals(gs.getCurrentPlayer().getTiles().size(), 14);
    }

}
