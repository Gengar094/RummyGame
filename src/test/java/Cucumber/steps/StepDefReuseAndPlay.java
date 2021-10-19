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
        List<List<String>> table = new ArrayList<>();
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
        System.out.println(Arrays.asList(meld.split(",")));
        System.out.println(Arrays.asList(tiles.split(",")));
        System.out.println(Public.gs.reuseAndPlay(num, meld.split(","), tiles.split("," )));
        System.out.println(Public.gs.getGame().getTable());
        Public.gs.endTurn();
    }


    @When("Player replace joker in meld {int} with {string}")
    public void replace_joker(int num, String string) {
        Public.gs.getGame().getTable().get(num - 1).add(string);
        Public.gs.getGame().getReplaceable().put(Public.gs.getGame().getTable().get(num - 1).hashCode(), true);
        Public.gs.getCurrentPlayer().getTiles().remove(string);
    }


    @When("Player does not replace joker with a tile")
    public void player_does_not_replace_joker() {
        // nothing to do
    }

}
