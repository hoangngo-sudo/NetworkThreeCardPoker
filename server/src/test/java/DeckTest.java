import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class DeckTest {

    private Deck deck;

    @BeforeEach
    void deckSetup() {
        deck = new Deck();
    }

    @Test
    void newDeckHas52Cards() {
        assertEquals(52, deck.size(), "New deck should contain 52 cards.");
    }

    @Test
    void newDeckHasAllUniqueSuitAndValues() {
        Set<String> seen = new HashSet<>();

        for (Card c : deck) {
            String key = c.getSuit() + "-" + c.getValue();
            assertFalse(seen.contains(key), "Duplicate key should not be in deck.");
            seen.add(key);
        }

        assertEquals(52, seen.size(), "All 52 cards should be unique");
    }

    @Test
    void newDeckHasFourOfEachValue() {
        int[] counts = new int[15];

        for (Card c : deck) {
            counts[c.getValue()]++;
        }

        for (int value = 2; value <= 14; value++) {
            assertEquals(4, counts[value], "There should be 4 cards of value " + value);
        }
    }

    @Test
    void newDeckRebuiltCorrectly() {

        deck.remove(0);
        deck.remove(0);
        deck.remove(0);

        assertEquals(49, deck.size());

        deck.newDeck();
        assertEquals(52, deck.size(), "new deck should rebuild a full deck");
    }

    @Test
    void removingCardsDecreaseSize() {
        int originalSize = deck.size();

        Card rem1 = deck.remove(0);
        Card rem2 = deck.remove(0);

        assertNotNull(rem1);
        assertNotNull(rem2);
        assertEquals(originalSize - 2, deck.size());
    }
}
