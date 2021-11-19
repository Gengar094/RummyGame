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
                Arguments.arguments("R11,R12", "R13,B13,G13,O13",  "R13", "R11,R12,R13","B13,G13,O13" ),
                Arguments.arguments("B13,O13", "R10,R11,R12,R13",  "R13", "R13,B13,O13","R10,R11,R12" ),
                Arguments.arguments("R11,R12", "R7,R8,R9,R10",  "R10", "R10,R11,R12", "R7,R8,R9" )
        );
    }



    @Test
    public void test_reuse_other_tiles_in_meld_after_replacing_joker() {
        // Given
        String[] desired = {"R3", "R4", "R8"};
        gs.setDesiredAndUniqueTiles(desired);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R5", "R6", "R7", "*"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, new String[]{"R8"});
        gs.reuseAndPlay(1, new String[]{"R5"}, new String[]{"R3","R4"});
        gs.endTurn();

        // Then
        for (String d: desired) {
            assertFalse(gs.getPrevPlayer().getTiles().contains(d));
        }

        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R3","R4","R5"))));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R6","R7","R8","*"))));
    }

    @Test
    public void test_reuse_the_joker_after_replacing_it() {
        // Given
        String[] desired = {"R3", "R4", "R8"};
        gs.setDesiredAndUniqueTiles(desired);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R5", "R6", "R7", "*"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.addToCurrentMeld(1, new String[]{"R8"});
        gs.reuseAndPlay(1, new String[]{"*"}, new String[]{"R3","R4"});
        gs.endTurn();

        // Then
        for (String d: desired) {
            assertFalse(gs.getPrevPlayer().getTiles().contains(d));
        }

        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R3","R4","*"))));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R5","R6","R7","R8"))));
    }



    // ******************************* invalid cases *************************** //
    @Test
    public void test_reuse_tiles_that_player_does_not_have() {
        // Given
        while (gs.getCurrentPlayer().getTiles().contains("R6")) {
            gs.getCurrentPlayer().getTiles().remove("R6");
            Config.tiles.add("R6");
        }

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R7", "R8", "R9", "R10","R11"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, new String[]{"R7","R8"}, new String[]{"R6"});
        gs.endTurn();

        // Then

        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R6","R7","R8"))));
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R9","R10","R11"))));
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R7", "R8", "R9", "R10","R11"))));
    }


    @Test
    public void test_reuse_tiles_that_meld_does_not_have() {
        // Given
        String[] desired = {"R6", "R7", "R8", "R9"};
        gs.setDesiredAndUniqueTiles(desired);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R2", "R3", "R4"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, new String[]{"R5"}, new String[]{"R6", "R7", "R8", "R9"});
        gs.endTurn();

        // Then

        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R5", "R6", "R7", "R8", "R9"))));
        for (String d: desired) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R2", "R3", "R4"))));
    }


    @Test
    public void test_select_a_meld_that_does_not_on_the_table() {
        // Given
        String[] desired = {"R6", "R7", "R8", "R9"};
        gs.setDesiredAndUniqueTiles(desired);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R2", "R3", "R4"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, new String[]{"R5"}, new String[]{"R6", "R7", "R8", "R9"});
        gs.endTurn();

        // Then

        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R5", "R6", "R7", "R8", "R9"))));
        for (String d: desired) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }
        assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R2", "R3", "R4"))));
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


    @Test
    public void test_reuse_joker_before_replacing_it() {
        // Given
        String[] desired = {"R3","R4"};
        gs.setDesiredAndUniqueTiles(desired);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R4,R5,R6,*"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, new String[]{"*"}, new String[]{"R3","R4"});
        gs.endTurn();

        // Then

        for (String d: desired) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R3", "R4", "*"))));
    }


    @Test
    public void test_reuse_other_tiles_with_joker_before_replacing_it_in_meld() {
        // Given
        String[] desired = {"R3","R4"};
        gs.setDesiredAndUniqueTiles(desired);

        List<List<String>> table = new ArrayList<>();
        List<String> list = new ArrayList<>(Arrays.asList("R4,R5,R6,*"));
        table.add(list);
        gs.setTable(table);

        gs.setInitial(false);

        // When
        gs.reuseAndPlay(1, new String[]{"R5"}, new String[]{"R3","R4"});
        gs.endTurn();

        // Then

        for (String d: desired) {
            assertTrue(gs.getPrevPlayer().getTiles().contains(d));
        }
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R3", "R4", "R5"))));
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R6", "R7", "*"))));

    }







}
