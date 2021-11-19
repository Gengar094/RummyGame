package JUnit.ParameterizedAppTest;

import Cucumber.supports.Public;
import game.Game;
import game.GameServer;
import game.Player;
import org.junit.jupiter.api.AfterAll;
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

public class splitMeldTest {
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
    public void test_player_split_a_meld(String meld, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(1, param);

        // Then
        String[] mm = meld.split("/");
        assertFalse(gs.getGame().getTable().contains(Arrays.asList(mm)));
        for (String p: pp) {
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_player_split_a_meld() {
        return List.of(
                Arguments.arguments("R1,R2,R3,R4", "R1,R2/R3,R4"),
                Arguments.arguments("R1,R2,R3", "R1/R2/R3"),
                Arguments.arguments("R4,B4,G4", "R4/B4,G4"),
                Arguments.arguments("R4,B4,G4,O4", "R4/B4/G4/O4")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_split_after_replacing_joker(String meld, String replace, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setDesiredAndUniqueTiles(replace.split(","));
        gs.setTable(table);
        gs.setInitial(false);
        gs.addToCurrentMeld(1, replace.split(","));

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(1, param);

        // Then
        String[] mm = meld.split("/");
        assertFalse(gs.getGame().getTable().contains(Arrays.asList(mm)));
        for (String p: pp) {
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_split_after_replacing_joker() {
        return List.of(
                Arguments.arguments("R3,R4,*", "R5", "R3,R5/R4,*"),
                Arguments.arguments("B1,B2,B3,B4,B5,B6,B7,B8,B9,B10,B11,B12,*", "B13", "B1,B2,B3/B4,B5,B6/B6,B7,B8,B9/B10,B11,B12,B13,*"),
                Arguments.arguments("R3,R5,R6,*", "R4", "R3,R4/R5,R6,*"),
                Arguments.arguments("R3,B3,*", "G3", "R3/B3,G3,*"),
                Arguments.arguments("R3,B3,G3,*", "O3", "R3,B3/G3,O3,*")
        );
    }


    // ******************************* invalid cases *************************** //
    @ParameterizedTest
    @MethodSource
    public void test_split_not_existing_meld(String meld, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(2, param);

        // Then
        String[] mm = meld.split("/");
        for (String m: mm) {
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m.split(","))));
        }
        for (String p: pp) {
            System.out.println(p);
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_split_not_existing_meld() {
        return List.of(
                Arguments.arguments("R1,R2,R3", "R1,R2/R3")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_split_with_tiles_that_meld_does_not_have(String meld, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(1, param);

        // Then
        String[] mm = meld.split("/");
        for (String m: mm) {
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m.split(","))));
        }
        for (String p: pp) {
            System.out.println(p);
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_split_with_tiles_that_meld_does_not_have() {
        return List.of(
                Arguments.arguments("R1,R2,R3", "R1,R2/R4")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_split_to_form_invalid_meld_at_the_end_of_turn(String meld, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(1, param);
        gs.endTurn();

        // Then
        String[] mm = meld.split("/");
        for (String m: mm) {
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m.split(","))));
        }
        for (String p: pp) {
            System.out.println(p);
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_split_to_form_invalid_meld_at_the_end_of_turn() {
        return List.of(
                Arguments.arguments("R1,R2,R3,R4,R5", "R1,R2,R3/R4,R5"),
                Arguments.arguments("R1,R2,R3,R4,R5,R6", "R1,R3,R4/R2,R5,R6"),
                Arguments.arguments("R3,B3,O3", "R3,B3/O3")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_split_before_initial_30(String meld, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setTable(table);

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(1, param);
        // Then
        String[] mm = meld.split("/");
        for (String m: mm) {
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m.split(","))));
        }
        for (String p: pp) {
            System.out.println(p);
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_split_before_initial_30() {
        return List.of(
                Arguments.arguments("R1,R2,R3,R4", "R1,R2/R3,R4"),
                Arguments.arguments("R1,R2,R3", "R1/R2/R3"),
                Arguments.arguments("B1,B2,B3,B4,B5,B6,B7,B8,B9,B10,B11,B12,B13", "B1,B2,B3/B4,B5,B6/B7,B8,B9/B10,B11,B12,B13"),
                Arguments.arguments("R4,B4,G4", "R4/B4,G4"),
                Arguments.arguments("R4,B4,G4,O4", "R4/B4/G4/O4")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_split_before_replacing_joker(String meld, String pieces) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();

        String[] ff = meld.split(",");
        for (String f: ff) {
            list.add(f);
        }
        table.add(list);

        gs.setTable(table);

        // When
        String[] pp = pieces.split("/");
        String[][] param = new String[pp.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = pp[i].split(",");
        }
        gs.splitMeld(1, param);

        // Then
        String[] mm = meld.split("/");
        for (String m: mm) {
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m.split(","))));
        }
        for (String p: pp) {
            System.out.println(p);
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_split_before_replacing_joker() {
        return List.of(
                Arguments.arguments("R1,R2,R3,*", "R1,R2/R3,*")
        );
    }
}
