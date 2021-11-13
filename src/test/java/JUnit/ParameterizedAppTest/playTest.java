package JUnit.ParameterizedAppTest;

import game.Config;
import game.Game;
import game.GameServer;
import game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class playTest {
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
    @ValueSource(strings =
            {"R6,R7,R8,R9",
            "B9,B10,B11",
            "R10,B10,G10",
            "R8,B8,G8,O8",
            "R1,R2,R3/R8,B8,G8",
            "R10,B10,*",
            "R11,R12,*"})
    public void test_play_for_initial_30(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertFalse(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }


    @ParameterizedTest
    @ValueSource(strings =
            {"R1,R2,R3",
            "R1,R2,R3,R4",
            "R5,B5,G5",
            "R6,B6,G6,O6",
            "R1,R2,*",
            "R7,B7,*"})
    public void test_play_valid_meld_after_initial_30(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        gs.setInitial(false);

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertFalse(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }

    // ******************************* invalid cases *************************** //
    @ParameterizedTest
    @ValueSource(strings =
            {"R10,R11,R11,R13",
            "R7,B7,G7,O6",
            "R7,B6,*" ,
            "R10,R11,R11,*"})
    public void test_play_an_invalid_meld_general(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertTrue(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }


    @ParameterizedTest
    @ValueSource(strings =
            {"R10,B11,R12,R13",
            "R7,B7,G7,G7"})
    public void test_play_an_invalid_meld_wrong_color(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertTrue(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }


    @ParameterizedTest
    @ValueSource(strings =
            {"R7,R8" ,
            "R7,B7",
            "R7,B7,G7,O7,O7",
            "R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,*"})
    public void test_play_an_invalid_meld_with_wrong_number_of_tiles(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertTrue(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }


    @Test
    public void test_a_tile_that_player_does_not_have() {
        // Given
        while (gs.getCurrentPlayer().getTiles().contains("R5")) {
            gs.getCurrentPlayer().getTiles().remove("R5");
            Config.tiles.add("R5");
        }

        gs.setDesiredAndUniqueTiles(new String[] {"R6", "R7"});

        // When
        gs.play(new String[] {"R5", "R6", "R7"});

        // Then
        assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList("R5","R6","R7"))));
        assertTrue(gs.getCurrentPlayer().getTiles().contains("R6"));
        assertTrue(gs.getCurrentPlayer().getTiles().contains("R7"));
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"R8,R9,R10" ,
            "R5,R6,R7,R8" ,
            "R9,B9,O9",
            "R7,B7,G7,O7",
            "R5,R6,R7,*",
            "R7,B7,G7,*",
            "R1,B1,G1/R1,R2,R3,R4"})
    public void test_play_valid_meld_but_invalid_for_initial_30(String melds) {
        // Given
        String[] ss = melds.split("/");
        for (String s: ss) {
            String[] desired = s.split(",");
            gs.setDesiredAndUniqueTiles(desired);
            for (String e: desired) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(e));
            }
        }

        // When
        for (String s: ss) {
            gs.play(s.split(","));
        }
        gs.endTurn();

        // Then
        for (String s: ss) {
            String[] desired = s.split(",");
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(desired))));
            for (String e: desired) {
                assertTrue(gs.getPrevPlayer().getTiles().contains(e));
            }
        }
    }

}
