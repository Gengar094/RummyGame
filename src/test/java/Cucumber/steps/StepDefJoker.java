package Cucumber.steps;
import Cucumber.supports.*;
import game.*;
import io.cucumber.java.en.Then;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefJoker {

    @Then("The joker in meld {int} is replaceable")
    public void joker_replaceable(int index) {
        Map<Integer, Boolean> replaceable = Public.gs.getGame().getReplaceable();
        System.out.println(Public.gs.getGame().getReplaceMap());
        assertTrue(replaceable.get(index - 1));
    }

    @Then("The joker in meld {int} is not replaceable")
    public void joker_not_replaceable(int index) {
        Map<Integer, Boolean> replaceable = Public.gs.getGame().getReplaceable();
        assertFalse(replaceable.get(index - 1));
    }
}
