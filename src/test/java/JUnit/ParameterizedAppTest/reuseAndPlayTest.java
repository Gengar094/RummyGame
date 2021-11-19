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

public class reuseAndPlayTest {
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
        System.out.println(Config.tiles.size());
        p1.reset();
        p1.randomizeTiles();
        p2.reset();
        p2.randomizeTiles();
        p3.reset();
        p3.randomizeTiles();
        System.out.println(Config.tiles.size());
    }

    @AfterAll
    public static void finish() {
        System.out.println("final");
        p1.reset();
        p2.reset();
        p3.reset();
    }

    @ParameterizedTest
    @MethodSource
    public void test_reuse_the_meld_from_the_table(String tiles, String melds, String reuse, String newMeld, String left) {
        // Given
        String[] ss = tiles.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        String[] mm = melds.split("/");
        List<List<String>> table;
        if (gs.getGame().getTable().size() != 0) {
            table = gs.getGame().getTable();
        } else {
            table = new ArrayList<>();
        }
        for (String s: mm) {
            List<String> list = new ArrayList<>(Arrays.asList(s.split(",")));
            if (list.contains("*")) {
                gs.getGame().getReplaceable().put(list.hashCode(),false);
            }
            table.add(list);
        }
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, reuse.split(","), tiles.split(","));
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] tt = s.split(",");
            for (String t: tt) {
                assertFalse(gs.getPrevPlayer().getTiles().contains(t));
            }
        }

        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(newMeld.split(",")))));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(left.split(",")))));

    }

    static List<Arguments> test_reuse_the_meld_from_the_table() {
        return List.of(
                Arguments.arguments("R11,B11", "R11,B11,G11,O11",  "G11", "R11,B11,G11","R11,B11,O11" ),
                Arguments.arguments("R12,B12,G12", "R12,B12,G12,O12",  "O12", "R12,B12,G12,O12","R12,B12,G12"),

                Arguments.arguments("R11,R12,R13", "R10,B10,G10,O10",  "R10", "R10,R11,R12,R13","B10,G10,O10"),
                Arguments.arguments("R11,R12", "R13,B13,G13,O13",  "R13", "R11,R12,R13","B13,G13,O13" ),

                Arguments.arguments("B13,O13", "R10,R11,R12,R13",  "R13", "R13,B13,O13","R10,R11,R12" ),
                Arguments.arguments("R13,B13,O13", "G10,G11,G12,G13",  "G13", "R13,B13,G13,O13","G10,G11,G12"),

                Arguments.arguments("R11,R12", "R7,R8,R9,R10",  "R10", "R10,R11,R12", "R7,R8,R9"),
                Arguments.arguments("R11,R12,R13", "R7,R8,R9,R10",  "R10", "R10,R11,R12,R13", "R7,R8,R9")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_reuse_other_tiles_in_meld_after_replacing_joker(String tiles, String melds, String replace, String reuse, String play, String newMeld) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        String[] mm = melds.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(mm));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);
        gs.addToCurrentMeld(1, replace.split(","));

        // When
        gs.reuseAndPlay(1, reuse.split(","), play.split(","));
        gs.endTurn();

        // Then
        for (String d: tiles.split(",")) {
            assertFalse(gs.getPrevPlayer().getTiles().contains(d));
        }

        String[] parts = newMeld.split("/");
        for (String p: parts) {
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_reuse_other_tiles_in_meld_after_replacing_joker() {
        return List.of(
                Arguments.arguments("R3,R4,R7", "R5,R6,*", "R7", "R5", "R3,R4", "R3,R4,R5/R6,R7,*"),
                Arguments.arguments("R3,R4,R8", "R5,R6,R7,*", "R8", "R5", "R3,R4", "R3,R4,R5/R6,R7,R8,*"),

                Arguments.arguments("R2,R3,G4", "R4,B4,*", "G4", "R4", "R2,R3", "R2,R3,R4/B4,G4,*"),
                Arguments.arguments("R5,R6,R7,G4", "R4,B4,O4,*", "G4", "R4", "R5,R6,R7", "R4,R5,R6,R7/B4,G4,O4,*")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void test_reuse_the_joker_after_replacing_it(String tiles, String melds, String replace, String reuse, String play, String newMeld) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, replace.split(","));
        gs.reuseAndPlay(1, reuse.split(","), play.split(","));
        gs.endTurn();

        // Then
        for (String d: play.split(",")) {
            assertFalse(gs.getPrevPlayer().getTiles().contains(d));
        }

        String[] parts = newMeld.split("/");
        for (String p: parts) {
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(p.split(",")))));
        }
    }

    static List<Arguments> test_reuse_the_joker_after_replacing_it() {
        return List.of(
                Arguments.arguments("R3,R4,R8", "R5,R6,R7,*", "R8", "*", "R3,R4", "R3,R4,*"),
                Arguments.arguments("R3,R4,R7", "R5,R6,*", "R7", "*", "R3,R4", "R3,R4,*"),
                Arguments.arguments("R3,R4,R8,O6", "R6,B6,*", "O6", "*", "R3,R4", "R3,R4,*"),
                Arguments.arguments("R3,R4,R8,G7", "R7,B7,O7,*", "G7", "*", "R3,R4", "R3,R4,*")
        );
    }



    // ******************************* invalid cases *************************** //
    @ParameterizedTest
    @MethodSource
    public void test_reuse_tiles_that_player_does_not_have(String doesNotHave, String melds, String reuse, String doesNotExist) {
        // Given
        String[] dd = doesNotHave.split(",");
        for (String d: dd) {
            while (gs.getCurrentPlayer().getTiles().contains(doesNotHave)) {
                gs.getCurrentPlayer().getTiles().remove(doesNotHave);
                Config.tiles.add(doesNotHave);
            }
        }

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, reuse.split(","), dd);
        gs.endTurn();

        // Then
        String[] ee = doesNotExist.split("/");
        for (String e: ee) {
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(e.split(",")))));
        }
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(melds.split(",")))));
    }

    static List<Arguments> test_reuse_tiles_that_player_does_not_have() {
        return List.of(
                Arguments.arguments("R6", "R7,R8,R9,R10,R11", "R7,R8", "R6,R7,R8/R9,R10,R11")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_reuse_tiles_that_meld_does_not_have(String tiles, String melds, String reuse, String newMeld) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, reuse.split(","), tiles.split(","));
        gs.endTurn();

        // Then


        for (String d: tiles.split(",")) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }

        String[] ee = newMeld.split("/");
        for (String e: ee) {
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(e.split(",")))));
        }
    }

    static List<Arguments> test_reuse_tiles_that_meld_does_not_have() {
        return List.of(
                Arguments.arguments("R6,R7,R8,R9", "R2,R3,R4", "R5", "R2,R3,R4,R5/R6,R7,R8,R9")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_select_a_meld_that_does_not_on_the_table(String tiles, String melds, String reuse, String newMeld) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        String[] mm = melds.split("/");
        for (String m: mm) {
            String[] ss = m.split(",");
            List<String> list = new ArrayList<>(Arrays.asList(ss));
            table.add(list);
        }
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(3, reuse.split(","), tiles.split(","));
        gs.endTurn();

        // Then
        for (String d: tiles.split(",")) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(newMeld.split(",")))));
    }

    static List<Arguments> test_select_a_meld_that_does_not_on_the_table() {
        return List.of(
                Arguments.arguments("R4,R5", "R6,R7,R8,R9/R1,R2,R3", "R3", "R3,R4,R5")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_reuse_before_initial_30(String tiles, String melds, String reuse) {
        // Given
        String[] tt = tiles.split(",");
        gs.setDesiredAndUniqueTiles(tt);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String[] mm = melds.split(",");
        for (String m: mm) {
            list.add(m);
        }
        table.add(list);
        gs.setTable(table);

        // When
        gs.reuseAndPlay(1, reuse.split(","), tt);
        gs.endTurn();

        // Then
        for (String t: tt) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(t));
        }
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(mm))));

    }

    static List<Arguments> test_reuse_before_initial_30() {
        return List.of(
                Arguments.arguments("R11,B11", "R11,B11,G11,O11", "G11"),
                Arguments.arguments("R11,R12", "R13,B13,G13,O13", "R13"),
                Arguments.arguments("B13,O13", "R10,R11,R12,R13", "R13"),
                Arguments.arguments("R11,R12", "R7,R8,R9,R10" , "R10")
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_invalid_meld_at_the_end_of_turn(String tiles, String melds, String reuse, String newMeld) {
        // Given
        String[] tt = tiles.split(",");
        gs.setDesiredAndUniqueTiles(tt);

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
        gs.reuseAndPlay(1, reuse.split(","), tt);
        gs.endTurn();

        // Then
        for (String t: tt) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(t));
        }
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(newMeld.split(",")))));

    }

    static List<Arguments> test_invalid_meld_at_the_end_of_turn() {
        return List.of(
                Arguments.arguments("R10", "R9,R10,R11,R12", "R10,R11,R12", "R10,R10,R11,R12"),
                Arguments.arguments("O6", "R7,B7,G7", "R7,B7,G7" , "R7,B7,G7,O6"),
                Arguments.arguments("R10,B11", "R11,R12,R13" , "R12,R13", "R10,B11,R12,R13" ),
                Arguments.arguments("G7", "R7,B7,G7" , "R7,B7,G7", "R7,B7,G7,G7"),
                Arguments.arguments("R7", "R8,R9,R10,R11" , "R8", "R7,R8"),
                Arguments.arguments("R7", "R7,B7,G7,O7" , "B7", "R7,B7"),
                Arguments.arguments("O7", "R7,B7,G7,O7" , "R7,B7,G7,O7", "R7,B7,G7,O7,O7" )
        );
    }



    @ParameterizedTest
    @MethodSource
    public void test_leaving_meld_does_not_valid(String tiles, String melds, String reuse, String newMeld) {
        // Given
        String[] tt = tiles.split(",");
        gs.setDesiredAndUniqueTiles(tt);

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
        gs.reuseAndPlay(1, reuse.split(","), tt);
        gs.endTurn();

        // Then
        for (String t: tt) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(t));
        }
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(newMeld.split(",")))));

    }

    static List<Arguments> test_leaving_meld_does_not_valid() {
        return List.of(
                Arguments.arguments("R6,R7", "R8,R9,R10", "R8" , "R6,R7,R8" ),
                Arguments.arguments("R6,R7", "R4,R5,R6,R7,R8", "R5" , "R5,R6,R7"),
                Arguments.arguments("R7", "R7,B7,G7,O7" , "B7,G7", "R7,B7,G7" )
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_reuse_joker_before_replacing_it(String tiles, String melds, String reuse, String doesNotExist) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, reuse.split(","), tiles.split(","));
        gs.endTurn();

        // Then

        for (String d: tiles.split(",")) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(doesNotExist.split(",")))));
    }

    static List<Arguments> test_reuse_joker_before_replacing_it() {
        return List.of(
                Arguments.arguments("R3,R4", "R4,R5,R6,*", "*", "R3,R4,*"),
                Arguments.arguments("R4,R5", "R4,B4,G4,*", "*", "R4,R5,*")
        );
    }


    @ParameterizedTest
    @MethodSource
    public void test_reuse_other_tiles_with_joker_before_replacing_it_in_meld(String tiles, String melds, String reuse, String doesNotExist) {
        // Given
        gs.setDesiredAndUniqueTiles(tiles.split(","));

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList(melds.split(",")));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, reuse.split(","), tiles.split(","));
        gs.endTurn();

        // Then

        for (String d: tiles.split(",")) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }

        String[] dd = doesNotExist.split("/");
        for (String d: dd) {
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(d.split(",")))));
        }
    }

    static List<Arguments> test_reuse_other_tiles_with_joker_before_replacing_it_in_meld() {
        return List.of(
                Arguments.arguments("R3,R4", "R5,R6,R7,*", "R5", "R3,R4,R5/R6,R7,*"),
                Arguments.arguments("R4,O4", "R4,B4,G4,*", "B4", "R4,B4,O4/R4,O4,*")
        );
    }







}
