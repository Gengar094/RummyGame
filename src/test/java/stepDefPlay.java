import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class stepDefPlay {
    GameServer gs;
    Player p1;
    Player p2;
    Player p3;


    @Before
    public void setup(Scenario scenario) {
        System.out.println("AA");
        gs = new GameServer();
        p1 = new Player("Harry");
        p3 = new Player("Sean");
        p2 = new Player("Chris");
        gs.setPlayers(new Player[] {p1,p2,p3});
        gs.setGame(new Game(new Player[] {p1,p2,p3}));
        p1.reset();
        p1.setTiles(new ArrayList<>(Arrays.asList("R10","B11","R12","R13","*","R7","B7","G7","G7","R8","B10","G10","R1","R2","R3","O7","R11","O7","R6", "G11", "G12", "G13", "B6","G6","O6")));
        p2.reset();
        p2.randomizeTiles();
        p3.reset();
        p3.randomizeTiles();

    }

    @Given("Player has {string} in his hand")
    public void player_has_in_his_hand(String string) {
        // Already has required tiles in setup
    }

    @Given("Player has not played any tile yet")
    public void player_has_not_played_any_tile_yet() {
        // initially none of the players has played yet
    }

    @When("Player plays {string} during his turn")
    public void player_plays_during_his_turn(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            gs.play(s.split(","));
        }
    }

    @Then("the table has {string} now")
    public void the_table_has_now(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            assertTrue(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(s.split(",")))));
        }
    }
    @Then("Player does not have {string} in his hand")
    public void player_does_not_have_this_meld(String string) {
        String[] ss = string.split("/");
        System.out.println(gs.getCurrentPlayer().getTiles());
        for (String s: ss) {
            String[] tt = s.split(",");
            for (String t: tt) {
                assertFalse(gs.getCurrentPlayer().getTiles().contains(t));
            }
        }
    }

    @Given("Player has played tiles before this turn")
    public void player_has_played_tiles_before_this_turn() {
        // Write code here that turns the phrase above into concrete actions
        gs.play(new String[]{"G11", "G12", "G13"});
        gs.endTurn();
        gs.draw();
        gs.endTurn();
        gs.draw();
        gs.endTurn();
    }

    @Then("the table does not have {string}")
    public void theTableDoesNotHaveTiles(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            assertFalse(gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(s.split(",")))));
        }
    }

    @And("Player still has {string} in his hand")
    public void playerStillHasMeldsInHisHand(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            String[] tt = s.split(",");
            for (String t: tt) {
                assertTrue(gs.getCurrentPlayer().getTiles().contains(t));
            }
        }
    }



    @Given("Player does not have R5 in his hand")
    public void playerDoesNotHaveR5InHisHand() {
        while (gs.getCurrentPlayer().getTiles().contains("R5")) {
            gs.getCurrentPlayer().getTiles().remove("R5");
            Config.tiles.add("R5");
        }
    }

    @When("Player plays tiles R5,R6,R7 during his turn")
    public void playerPlaysTilesR5R6R7DuringHisTurn() {
        player_plays_during_his_turn("R5,R6,R7");
    }

    @Then("the table does not have meld R5,R6,R7")
    public void theTableDoesNotHaveMeldR5R6R7() {
        theTableDoesNotHaveTiles("R5,R6,R7");
    }

    @And("Player still has R6,R7 tiles in his hand")
    public void playerStillHasRRTilesInHisHand() {
        player_has_in_his_hand("R6,R7");
    }

    @After
    public void afterHook(Scenario scenario) {
        gs.reset();
        p1.reset();
        p2.reset();
        p3.reset();
    }
}
