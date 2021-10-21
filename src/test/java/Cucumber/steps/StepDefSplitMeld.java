package Cucumber.steps;
import Cucumber.supports.Public;
import io.cucumber.java.en.When;

import java.util.Arrays;

public class StepDefSplitMeld {

    @When("Player splits the {int} meld into {string}")
    public void player_split(int index, String s) {
        String[] ss = s.split("/");
        String[][] param = new String[ss.length][];
        for (int i = 0; i < param.length; i++) {
            param[i] = ss[i].split(",");
            System.out.println(Arrays.asList(param[i]));
        }
        Public.gs.splitMeld(index, param);
    }

    @When("Player does not end his turn")
    public void player_does_not_end_his_turn() {
        // do nothing
    }

    @When("Player ends his turn")
    public void player_ends_his_turn() {
        Public.gs.endTurn();
    }
}
