package JUnit.ParameterizedAppTest;

import game.Game;
import game.GameServer;
import game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class replaceJokerTest {
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
    @MethodSource
    public void test_player_adds_tiles_to_existing_meld(String meld, String hand, String tiles) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String[] mm = meld.split(",");
        for (String m: mm) {
            list.add(m);
        }
        table.add(list);
        gs.setTable(table);

        String[] ss = hand.split(",");
        gs.setDesiredAndUniqueTiles(ss);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, tiles.split(","));

        // Then
        assertTrue(gs.getGame().getReplaceable().get(0));
    }

    static List<Arguments> test_player_adds_tiles_to_existing_meld() {
        return List.of(
                Arguments.arguments("R2,R3,R4,*", "R5,R6,R7", "R5,R6"),
                Arguments.arguments("R2,R3,R4,*", "R1,R2,B7", "R1"),
                Arguments.arguments("R1,R2,*", "R3,G6,O8", "R3"),
                Arguments.arguments("R1,R2,*", "R13,G4,G8", "R13"),

                Arguments.arguments("R5,B5,G5,*", "O5,G7" , "O5"),
                Arguments.arguments("R5,B5,*", "O5,O7", "O5"),
                Arguments.arguments("R5,B5,*", "G5,O7", "G5"),
                Arguments.arguments("R5,B5,*", "G5,O5", "G5,O5")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_trying_to_replace_joker_using_tiles_from_table(String melds, String move) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String[] mm = melds.split("/");
        for (String m: mm) {
           String[] ss = m.split(",");
           for (String s: ss) {
               list.add(s);
           }
            table.add(list);
        }
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.moveTilesOnTable(2, move.split(","), 1);

        // Then
        assertFalse(gs.getGame().getReplaceable().get(0));
    }

    static List<Arguments> test_trying_to_replace_joker_using_tiles_from_table() {
        return List.of(
                Arguments.arguments("R2,R3,R4,*/R5,R6,R7", "R5,R6"),
                Arguments.arguments("R2,R3,R4,*/R1,R2,B7", "R1"),
                Arguments.arguments("R1,R2,*/R3,G6,O8", "R3"),
                Arguments.arguments("R1,R2,*/R13,G4,G8", "R13"),

                Arguments.arguments("R5,B5,G5,*/O5,G7" , "O5"),
                Arguments.arguments("R5,B5,*/O5,O7", "O5"),
                Arguments.arguments("R5,B5,*/G5,O7", "G5"),
                Arguments.arguments("R5,B5,*/G5,O5", "G5,O5")
        );
    }
}
