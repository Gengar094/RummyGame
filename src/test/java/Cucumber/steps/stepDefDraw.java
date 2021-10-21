package Cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import Cucumber.supports.*;
import game.*;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefDraw {
    int tiles = 0;

    // valid case
    @Given("A player has 14 tiles in the hand")
    public void player_has_14_tiles_in_hand() {
        // already has 14 tiles in hand by randomizeTiles()
    }

    @Given("The deck has some tiles")
    public void deck_has_some_tiles() {
        // as p1, p2, p3 have already 14 tiles, deck automatically has 64 tiles left
        this.tiles = Config.tiles.size();
    }

    @When("A player chooses to draw from a deck")
    public void player_draw() {
        Public.gs.draw(); // p1 draws a tile from deck
    }

    @Then("A player has 15 tiles in the hand")
    public void player_has_tiles_now() {
        assertEquals(Public.gs.getCurrentPlayer().getTiles().size(), 15);
    }

    @Then("The deck has 1 less tile")
    public void deck_has_remaining_tiles() {
        assertEquals(Config.tiles.size(), tiles - 1);
    }

    // invalid case
    @Given("The deck has 0 tiles remaining")
    public void deck_has_0_tiles() {
        System.out.println(Config.tiles.size());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 32; j++) {  // (14 + 32) * 2 + 14 = 106
                Public.gs.draw();
            }
            Public.gs.endTurn();
        }
    }


    @Then("The player has 14 tiles in the hand")
    public void player_still_has_14_tiles() {
        assertEquals(Public.gs.getCurrentPlayer().getTiles().size(), 14);
    }

}
