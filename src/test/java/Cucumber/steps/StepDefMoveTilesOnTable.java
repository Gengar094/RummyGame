package Cucumber.steps;
import Cucumber.supports.*;
import game.*;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefMoveTilesOnTable {

    @When("Player moves {string} from {int} meld to {int} meld")
    public void player_move_tiles_on_the_table(String s, int from, int to) {
        String[] ss = s.split(",");
        Public.gs.moveTilesOnTable(from, ss, to);
    }

    @Then("the table still has {string}")
    public void the_table_still_has(String s) {
        System.out.println(Public.gs.getGame().getTable());
        String[] ss = s.split("/");
        for (String meld: ss) {
            assertTrue(Public.gs.getGame().getTable().contains(Arrays.asList(meld.split(","))));
        }
    }
}
