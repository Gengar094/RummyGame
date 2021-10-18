package Cucumber.steps;

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
import Cucumber.supports.*;
import java.util.List;
import game.*;

import static org.junit.jupiter.api.Assertions.*;

public class stepDefPlay {

    @Given("Player has {string} in his hand")
    public void player_has_in_his_hand(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            Public.gs.setDesiredAndUniqueTiles(s.split(","));
        }
    }

    @Given("Player has not played any tile yet")
    public void player_has_not_played_any_tile_yet() {
        // initially none of the players has played yet
    }

    @When("Player plays {string} during his turn")
    public void player_plays_during_his_turn(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            Public.gs.play(s.split(","));
        }
        Public.gs.endTurn();
    }

    @Then("the table has {string} now")
    public void the_table_has_now(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            assertTrue(Public.gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(s.split(",")))));
        }
    }
    @Then("Player does not have {string} in his hand")
    public void player_does_not_have_this_meld(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            String[] tt = s.split(",");
            for (String t: tt) {
                assertFalse(Public.gs.getPrevPlayer().getTiles().contains(t));
            }
        }
    }

    @Given("Player has played tiles before this turn")
    public void player_has_played_tiles_before_this_turn() {
        Public.gs.setInitial(false);
    }

    @Then("the table does not have {string}")
    public void theTableDoesNotHaveTiles(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            assertFalse(Public.gs.getGame().getTable().contains(new ArrayList<>(Arrays.asList(s.split(",")))));
        }
    }

    @And("Player still has {string} in his hand")
    public void playerStillHasMeldsInHisHand(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            String[] tt = s.split(",");
            System.out.println(Public.gs.getGame().getCurr());
            System.out.println(Public.gs.getPrevPlayer().getTiles());
            for (String t: tt) {
                assertTrue(Public.gs.getPrevPlayer().getTiles().contains(t));
            }
        }
    }



    @Given("Player does not have R5 in his hand")
    public void playerDoesNotHaveR5InHisHand() {
        while (Public.gs.getCurrentPlayer().getTiles().contains("R5")) {
            Public.gs.getCurrentPlayer().getTiles().remove("R5");
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

}
