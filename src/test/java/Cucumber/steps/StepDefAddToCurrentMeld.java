package Cucumber.steps;
import Cucumber.supports.*;
import game.*;
import io.cucumber.java.en.When;

public class StepDefAddToCurrentMeld {
    @When("Player adds {string} to {int} meld")
    public void player_add_tiles(String s, int i) {
        String[] ss = s.split(",");
        Public.gs.addToCurrentMeld(i, ss);
        Public.gs.endTurn();
    }
}
