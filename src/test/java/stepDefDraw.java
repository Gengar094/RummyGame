import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class stepDefDraw {
    GameServer gs = new GameServer();
    Player p1 = new Player("Harry");
    Player p2 = new Player("Sean");
    Player p3 = new Player("Chris");
    Game g = new Game(new Player[] {p1,p2,p3});

    // valid case
    @Given("A player has 14 tiles in the hand")
    public void player_has_14_tiles_in_hand() {
        // already has 14 tiles in hand by randomizeTiles()
        gs.setPlayers(new Player[] {p1,p2,p3});
        gs.setGame(g);
        p1.randomizeTiles();
    }

    @Given("The deck has 64 tiles remaining")
    public void deck_has_64_tiles() {
        p2.randomizeTiles();
        p3.randomizeTiles();
    }

    @When("A player chooses to draw from a deck")
    public void player_draw() {
        gs.draw(); // p1 draws a tile from deck
    }

    @Then("A player has 15 in the hand now")
    public void player_has_tiles_now() {
        assertEquals(gs.getCurrentPlayer().getTiles().size(), 15);
    }

    @Then("The deck has 63 tiles remaining")
    public void deck_has_remaining_tiles() {
        assertEquals(Config.tiles.size(), 63);
    }

    // invalid case
    @Given("The deck has 0 tiles remaining")
    public void deck_has_0_tiles() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 32; j++) {  // (14 + 32) * 2 + 14 = 106
                gs.draw();
            }
            gs.endTurn();
        }
    }


    @Then("The player has 14 tiles in the hand")
    public void player_still_has_14_tiles() {
        assertEquals(gs.getCurrentPlayer().getTiles().size(), 14);
    }
}
