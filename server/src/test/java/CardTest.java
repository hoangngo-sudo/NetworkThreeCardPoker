import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void constructorAndGettersStoreSuitAndValCorrectly() {
        Card c = new Card('H', 10);

        assertEquals('H', c.getSuit(), "Suit should be Hearts");
        assertEquals(10, c.getValue(), "Value should be 10");
    }

    @Test
    void toStringReturnsNumericValueFor2To10() {
        Card c2 =  new Card('C', 2);
        Card c3 = new Card('D', 9);
        Card c4 = new Card('S', 10);

        assertEquals("2C", c2.toString());
        assertEquals("9D", c3.toString());
        assertEquals("10S", c4.toString());
    }

    @Test
    void toStringReturnsFaceLettersCorrectly() {
        Card jack = new Card('H', 11);
        Card queen = new Card('C', 12);
        Card king = new Card('D', 13);
        Card ace = new Card('S', 14);

        assertEquals("JH", jack.toString());
        assertEquals("QC", queen.toString());
        assertEquals("KD", king.toString());
        assertEquals("AS", ace.toString());
    }

    @Test
    void cardsWithSameSuitAndValueAreEquivalent() {
        Card c1 = new Card('H', 14);
        Card c2 = new Card('H', 14);

        assertEquals(c1.getSuit(), c2.getSuit());
        assertEquals(c1.getValue(), c2.getValue());
    }
}
