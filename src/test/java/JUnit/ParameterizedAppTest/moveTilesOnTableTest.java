package JUnit.ParameterizedAppTest;

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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class moveTilesOnTableTest {
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
    public void test_player_moves_tiles_from_one_meld_to_another(String from, String to, String move, String newMeld) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        String[] meld = newMeld.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_player_moves_tiles_from_one_meld_to_another() {
        return List.of(
                Arguments.arguments("R1,R2,R3,R4", "R5,R6", "R4", "R1,R2,R3/R4,R5,R6"),
                Arguments.arguments("R1,R2,R3,R4", "R5,R6,R7", "R4", "R1,R2,R3/R4,R5,R6,R7"),
                Arguments.arguments("R1,R2,R3,R4", "B1,G1", "R1", "R1,B1,G1"),
                Arguments.arguments("R1,R2,R3,R4", "B4,G4,O4", "R4", "R1,R2,R3/R4,B4,G4,O4"),

                Arguments.arguments("R3,B3,G3,O3", "B4,B5", "B3", "R3,G3,O3/B3,B4,B5"),
                Arguments.arguments("R3,B3,G3,O3", "B4,B5,B6", "B3", "R3,G3,O3/B3,B4,B5,B6"),
                Arguments.arguments("R3,B3,G3,O3", "B3,G3", "O3", "R3,B3,G3/B3,G3,O3"),
                Arguments.arguments("R3,B3,G3,O3", "B3,G3,O3", "R3", "B3,G3,O3/R3,B3,G3,O3")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_player_moves_normal_tiles_after_replacing_joker(String from, String to, String tile, String move, String newMeld) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setInitial(false);

        gs.setDesiredAndUniqueTiles(tile.split(","));
        gs.addToCurrentMeld(1, tile.split(","));

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        String[] meld = newMeld.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_player_moves_normal_tiles_after_replacing_joker() {
        return List.of(
                Arguments.arguments("R1,R2,R3,R4,R5,*", "R3,R4", "R6", "R5", "R1,R2,R3,R4,R6,*/R3,R4,R5"),
                Arguments.arguments("R1,R2,R3,R4,*", "R2,R3,R4", "R5", "R1", "R2,R3,R4,R5,*/R1,R2,R3,R4"),
                Arguments.arguments("R1,R2,R3,R4,*", "G1,O1", "R5", "R1", "R2,R3,R4,R5,*/R1,G1,O1"),
                Arguments.arguments("R1,R2,R3,R4,*", "B1,G1,O1", "R5", "R1", "R2,R3,R4,R5,*/R1,B1,G1,O1"),

                Arguments.arguments("R5,B5,O5,*", "R3,R4", "G5", "R5", "B5,G5,O5,*/R3,R4,R5"),
                Arguments.arguments("R2,B2,G2,*", "R3,R4,R5", "O2", "R2", "B2,G2,O2,*/R2,R3,R4,R5"),
                Arguments.arguments("R2,B2,G2,*", "G2,O2", "O2", "B2", "R2,G2,O2,*/B2,G2,O2"),
                Arguments.arguments("R2,B2,G2,*", "B2,G2,O2", "O2", "R2", "B2,G2,O2,*/B2,G2,O2,*")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_select_tiles_that_meld_does_not_have(String from, String to, String move, String stillHas) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        String[] meld = stillHas.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_select_tiles_that_meld_does_not_have() {
        return List.of(
                Arguments.arguments("R8,R9,R10,R11", "R4,R5,R6", "R7", "R8,R9,R10,R11/R4,R5,R6")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_from_a_meld_that_is_not_on_the_table(String from, String to, String move, String stillHas) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        gs.moveTilesOnTable(3, move.split(","), 2);

        // Then
        String[] meld = stillHas.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_from_a_meld_that_is_not_on_the_table() {
        return List.of(
                Arguments.arguments("R8,B8,G8,O8", "O9,O10,O11", "R10", "O9,O10,O11/R8,B8,G8,O8")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_to_a_meld_that_is_not_on_the_table(String from, String to, String move, String stillHas) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        gs.moveTilesOnTable(1, move.split(","), 3);

        // Then
        String[] meld = stillHas.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_to_a_meld_that_is_not_on_the_table() {
        return List.of(
                Arguments.arguments("R6,R7,R8,R9,R10", "R11,R12,R13", "R10", "R6,R7,R8,R9,R10/R11,R12,R13")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_move_before_initial_30(String from, String to, String move) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(ff)));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(tt)));
    }

    static List<Arguments> test_move_before_initial_30() {
        return List.of(
                Arguments.arguments("R1,R2,R3,R4", "R5,R6", "R4"),
                Arguments.arguments("R1,R2,R3,R4", "R5,R6,R7", "R4"),
                Arguments.arguments("R1,R2,R3,R4", "B1,G1", "R1"),
                Arguments.arguments("R1,R2,R3,R4", "B4,G4,O4", "R4"),

                Arguments.arguments("R3,B3,G3,O3", "B4,B5", "B3"),
                Arguments.arguments("R3,B3,G3,O3" , "B4,B5,B6", "B3"),
                Arguments.arguments("R3,B3,G3,O3", "B3,G3" , "O3"),
                Arguments.arguments("R3,B3,G3,O3", "B3,G3,O3", "R3")


        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_move_to_form_invalid_meld_at_the_end_of_turn(String from, String to, String move) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(ff)));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(tt)));
    }

    static List<Arguments> test_move_to_form_invalid_meld_at_the_end_of_turn() {
        return List.of(
                Arguments.arguments("R5,R6,R7,R8", "R7,R8,R9", "R8"),
                Arguments.arguments("R10,B10,G10", "R7,R8,R9", "B10"),
                Arguments.arguments("R3,R4,R5,R6", "R7,R8,R9", "R3"),
                Arguments.arguments("O10,O11,O12,O13", "R7,B7,G7", "O10"),
                Arguments.arguments("R7,R8,R9,R10", "R7,B7,G7", "R7"),
                Arguments.arguments("R7,R8,R9,R10", "R7,B7,G7,O7", "R7")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_leaving_meld_does_not_valid_at_the_end_of_turn(String from, String to, String move) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(ff)));
        assertTrue(gs.getGame().getTable().contains(Arrays.asList(tt)));
    }

    static List<Arguments> test_leaving_meld_does_not_valid_at_the_end_of_turn() {
        return List.of(
                Arguments.arguments("R5,R6,R7", "R8,R9,R10", "R7"),
                Arguments.arguments("R5,R6,R7,R8", "R8,R9,R10", "R6,R7"),
                Arguments.arguments("R5,R6,R7,R8", "R8,R9,R10", "R7"),
                Arguments.arguments("R8,B8,G8", "R5,R6,R7", "R8"),
                Arguments.arguments("R8,B8,G8,O8", "G8,O8", "R8,B8")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_move_tiles_includes_joker_to_another_meld_after_replacing_joker(String from, String to, String tiles, String move, String stillHas) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setDesiredAndUniqueTiles(tiles.split(","));
        gs.setInitial(false);
        gs.addToCurrentMeld(1, tiles.split(","));

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        String[] meld = stillHas.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_move_tiles_includes_joker_to_another_meld_after_replacing_joker() {
        return List.of(
                Arguments.arguments("R3,R4,R5,R6,*", "R7,B7,G7", "R8", "*", "R3,R4,R5,R6,R8,*/R7,B7,G7"),
                Arguments.arguments("R4,R5,*", "R7,B7,G7", "R6", "*", "R4,R5,R6,*/R7,B7,G7"),

                Arguments.arguments("R4,B4,*", "R7,R8,R9", "G4", "*", "R4,B4,G4,*/R7,R8,R9"),
                Arguments.arguments("R4,B4,G4,*", "R7,R8,R9", "O4", "*", "R4,B4,G4,O4,*/R7,R8,R9")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_move_tiles_before_replacing_joker(String from, String to, String move, String stillHas) {
        // Given
        List<List<String>> table = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        String[] ff = from.split(",");
        for (String f: ff) {
            fromList.add(f);
        }

        String[] tt = to.split(",");
        for (String t: tt) {
            toList.add(t);
        }

        table.add(fromList);
        table.add(toList);

        gs.setTable(table);
        gs.setInitial(false);

        // When
        gs.moveTilesOnTable(1, move.split(","), 2);

        // Then
        String[] meld = stillHas.split("/");
        for (String mm: meld) {
            String[] m = mm.split(",");
            assertTrue(gs.getGame().getTable().contains(Arrays.asList(m)));
        }
    }

    static List<Arguments> test_move_tiles_before_replacing_joker() {
        return List.of(
                Arguments.arguments("R3,R4,R5,R6,*", "B3,G3,O3", "R3", "R3,R4,R5,R6,*/B3,G3,O3"),
                Arguments.arguments("R3,R4,*", "B3,G3,O3", "R3", "R3,R4,*/B3,G3,O3"),

                Arguments.arguments("R3,B3,O3,*", "B3,G3,O3", "R3", "R3,B3,O3,*/B3,G3,O3"),
                Arguments.arguments("R3,B3,*", "B3,G3,O3", "R3", "R3,B3,*/B3,G3,O3")
        );
    }
}
