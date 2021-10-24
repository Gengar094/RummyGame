package Cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.Arrays;
import Cucumber.supports.*;
import game.*;

import static org.junit.jupiter.api.Assertions.*;

public class stepDefPlay {
    int tiles;

    @Given("Player has {string} in his hand")
    public void player_has_in_his_hand(String string) {
        String[] ss = string.split("/");
        for (String s: ss) {
            Public.gs.setDesiredAndUniqueTiles(s.split(","));
        }
        tiles = Public.gs.getCurrentPlayer().getTiles().size();
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
        System.out.println(Public.gs.getGame().getTable());
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
        System.out.println(Public.gs.getGame().getTable());
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
            for (String t: tt) {
                assertTrue(Public.gs.getPrevPlayer().getTiles().contains(t));
            }
        }
    }



    @Given("Player does not have tiles {string} in his hand")
    public void playerDoesNotHaveInHisHand(String s) {
        while (Public.gs.getCurrentPlayer().getTiles().contains(s)) {
            Public.gs.getCurrentPlayer().getTiles().remove(s);
            Config.tiles.add(s);
        }
    }


}
