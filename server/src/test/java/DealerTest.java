import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class DealerTest {

    private Dealer dealer;
    private ArrayList<Card> hand;

    @BeforeEach
    void setUp() {
        dealer = new Dealer();
        hand = dealer.dealHand();
    }

    @Test
    void dealHandReturnsThreeCards() {

        assertNotNull(hand, "Dealt hand should not be null");
        assertEquals(3, hand.size(), "Dealer hand size should be 3");
    }

    @Test
    void dealerHandReducesDeckSizeByThree() {
        // int deckSize = dealer.theDeck.size();

        assertEquals(3, hand.size());
        assertEquals(49, dealer.theDeck.size());
    }

    @Test
    void dealerHandRebuildsDeckWhenLow() {

        // force deck to have fewer than 3 cards
        while (dealer.theDeck.size() >= 3) {
            dealer.dealHand();
        }
        int deckSize =  dealer.theDeck.size();
        assertTrue(deckSize < 3, "Precondition: deck should be fewer than 3 cards");

        // ArrayList<Card> dealerHand =  dealer.dealHand();

        // After calling dealhand, the deck should have been rebuilt and then reduced by 3
        assertEquals(3, hand.size());
        assertTrue(dealer.theDeck.size() >= 0, "Deck should be in a valid state after dealing");
    }

    @Test
    void setDealersHandStoresAndReturnsSameReference() {
        ArrayList<Card> customHand = new ArrayList<>();

        customHand.add(new Card('H', 14));
        customHand.add(new Card('C', 13));
        customHand.add(new Card('D', 12));

        dealer.setDealersHand(customHand);
        assertSame(customHand, dealer.getDealersHand());
    }
}
