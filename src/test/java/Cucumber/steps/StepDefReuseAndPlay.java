package Cucumber.steps;
import Cucumber.supports.Public;
import game.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepDefReuseAndPlay {

    @Given("Table has {string}")
    public void table_has_meld(String string) {
        String[] ss = string.split("/");
        List<List<String>> table;
        if (Public.gs.getGame().getTable().size() != 0) {
            table = Public.gs.getGame().getTable();
        } else {
            table = new ArrayList<>();
        }
        for (String s: ss) {
            List<String> list = new ArrayList<>(Arrays.asList(s.split(",")));
            if (list.contains("*")) {
                Public.gs.getGame().getReplaceable().put(list.hashCode(),false);
            }
            table.add(list);
        }
        Public.gs.setTable(table);
    }

    @When("Player reuses {string} from {int} meld, and play {string}")
    public void player_reuses(String meld, int num, String tiles) {
        Public.gs.reuseAndPlay(num, meld.split(","), tiles.split("," ));
    }


    @When("Player replace joker in meld {int} with {string}")
    public void replace_joker(int num, String string) {
        Public.gs.addToCurrentMeld(num, string.split(","));
    }


    @When("Player does not replace joker with a tile")
    public void player_does_not_replace_joker() {
        // nothing to do
    }

}
