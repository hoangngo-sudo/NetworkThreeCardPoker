import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player =  new Player();
    }

    @Test
    void defaultConstructorWorksCorrectly() {

        assertNotNull(player.getHand(), "Hand should be initialized");
        assertTrue(player.getHand().isEmpty(), "Hand should be empty");
        assertEquals(0, player.getAnteBet());
        assertEquals(0, player.getPlayBet());
        assertEquals(0, player.getPairPlusBet());
        assertEquals(0, player.getTotalWinnings());
    }

    @Test
    void setHandReplacesPlayerHand() {
        ArrayList<Card> newHand = new ArrayList<>();
        newHand.add(new Card('H', 10));
        newHand.add(new Card('H', 11));
        newHand.add(new Card('H', 12));

        player.setHand(newHand);

        assertEquals(3, player.getHand().size());
        assertSame(newHand, player.getHand());
    }

    @Test
    void antePlayPairPlusSettersAndGettersWorkCorrectly() {

        player.setAnteBet(10);
        player.setPlayBet(10);
        player.setPairPlusBet(10);

        assertEquals(10, player.getAnteBet());
        assertEquals(10, player.getPlayBet());
        assertEquals(10, player.getPairPlusBet());
    }

    @Test
    void totalWinningsCanIncreaseOrDecrease() {
        player.setTotalWinnings(0);

        player.setTotalWinnings(player.getTotalWinnings() + 50);
        assertEquals(50, player.getTotalWinnings());

        player.setTotalWinnings(player.getTotalWinnings() - 20);
        assertEquals(30, player.getTotalWinnings());
    }
}
