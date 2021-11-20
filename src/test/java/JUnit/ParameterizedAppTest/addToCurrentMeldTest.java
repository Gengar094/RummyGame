package JUnit.ParameterizedAppTest;

import game.Config;
import game.Game;
import game.GameServer;
import game.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class addToCurrentMeldTest {
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

    @AfterAll
    public static void finish() {
        p1.reset();
        p2.reset();
        p3.reset();
    }

    @ParameterizedTest
    @MethodSource
    public void test_player_adds_tiles_to_existing_meld(String tiles, String melds, String newMeld) {
        // Given
        String[] ss = tiles.split(",");
        gs.setDesiredAndUniqueTiles(ss);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String[] mm = melds.split(",");
        for (String m: mm) {
            list.add(m);
        }
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, ss);
        gs.endTurn();

        // Then
        for (String s: ss) {
            assertFalse(gs.getPrevPlayer().getTiles().contains(s));
        }
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(newMeld.split(","))));
    }

    static List<Arguments> test_player_adds_tiles_to_existing_meld() {
        return List.of(
                Arguments.arguments("R5", "R6,R7", "R5,R6,R7"),
                Arguments.arguments("R5", "R6,R7,R8", "R5,R6,R7,R8"),
                Arguments.arguments("R5", "B5,O5", "R5,B5,O5"),
                Arguments.arguments("R5", "G5,B5,O5", "R5,B5,G5,O5"),
                Arguments.arguments("R4,R5", "R3", "R3,R4,R5"),
                Arguments.arguments("R4,B4", "G4,O4", "R4,B4,G4,O4"),
                Arguments.arguments("*", "R6,R7,R8", "R6,R7,R8,*"),
                Arguments.arguments("*", "R5,B5", "R5,B5,*")
        );
    }


    // ******************************* invalid cases *************************** //
    @ParameterizedTest
    @MethodSource
    public void test_player_adds_tiles_that_he_does_not_have(String doesNotHave, String melds, String doesNotExist) {
        // When
        String[] dd = doesNotHave.split(",");
        for (String d: dd) {
            while (gs.getCurrentPlayer().getTiles().contains(d)) {
                gs.getCurrentPlayer().getTiles().remove(d);
                Config.tiles.add(d);
            }
        }

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, doesNotHave.split(","));
        gs.endTurn();

        // Then
        String[] ss = doesNotExist.split(",");
        assertFalse(gs.getGame().getTable().contains(Arrays.asList(ss)));
    }

    static List<Arguments> test_player_adds_tiles_that_he_does_not_have() {
        return List.of(
                Arguments.arguments("R5", "R6,R7,R8", "R5,R6,R7,R8")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void test_player_adds_tiles_that_form_invalid_meld_at_the_turn_end(String tiles, String melds, String newMeld) {
        // Given
        String[] ss = tiles.split(",");
        gs.setDesiredAndUniqueTiles(ss);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String[] mm = melds.split(",");
        for (String m: mm) {
            list.add(m);
        }
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, ss);
        gs.endTurn();

        // Then
        for (String s: ss) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(s));
        }
        assertFalse(gs.getGame().getTable().contains(Arrays.asList(newMeld)));
    }

    static List<Arguments> test_player_adds_tiles_that_form_invalid_meld_at_the_turn_end() {
        return List.of(
                Arguments.arguments("R3","R2","R2,R3"),
                Arguments.arguments("*","R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13","R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,*"),
                Arguments.arguments("R9","R4,R5,R6","R4,R5,R6,R9"),
                Arguments.arguments("B3","R4,R5,R6","B3,R4,R5,R6"),
                Arguments.arguments("R3","R3,B3,G3","R3,R3,B3,G3"),
                Arguments.arguments("O4","R3,B3,G3","R3,B3,G3,O4"),
                Arguments.arguments("R3","B3","R3,B3"),
                Arguments.arguments("R3","R3,B3,G3,O3","R3,R3,B3,G3,O3")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_select_a_meld_that_is_not_on_the_table(String tiles, String melds, String doesNotExist) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(2, tiles.split(","));
        gs.endTurn();

        // Then
        String[] ss = doesNotExist.split(",");
        assertFalse(gs.getGame().getTable().contains(Arrays.asList(ss)));

        for (String s: tiles.split(",")) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(s));
        }

    }

    static List<Arguments> test_select_a_meld_that_is_not_on_the_table() {
        return List.of(
                Arguments.arguments("R5", "R6,R7,R8", "R5,R6,R7,R8")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_add_to_existing_meld_before_initial_30(String tiles, String melds, String newMeld) {
        // Given
        String[] ss = tiles.split(",");
        gs.setDesiredAndUniqueTiles(ss);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String[] mm = melds.split(",");
        for (String m: mm) {
            list.add(m);
        }
        table.add(list);
        gs.setTable(table);

        // When
        gs.addToCurrentMeld(1, ss);
        gs.endTurn();

        // Then
        for (String s: ss) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(s));
        }
        assertFalse(gs.getGame().getTable().contains(Arrays.asList(newMeld.split(","))));
    }

    static List<Arguments> test_add_to_existing_meld_before_initial_30() {
        return List.of(
                Arguments.arguments("R5", "R6,R7", "R5,R6,R7"),
                Arguments.arguments("R5", "R6,R7,R8", "R5,R6,R7,R8"),
                Arguments.arguments("R5", "B5,O5", "R5,B5,O5"),
                Arguments.arguments("R5", "G5,B5,O5", "R5,B5,G5,O5"),
                Arguments.arguments("R4,R5", "R3", "R3,R4,R5"),
                Arguments.arguments("R4,B4", "G4,O4", "R4,B4,G4,O4"),
                Arguments.arguments("*", "R6,R7,R8","R6,R7,R8,*"),
                Arguments.arguments("*", "R5,B5", "R5,B5,*")
        );
    }



}
