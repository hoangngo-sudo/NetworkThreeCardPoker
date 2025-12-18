import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class ThreeCardLogicTest {

    // helper function to build a hand
    private ArrayList<Card> hand(Card c1, Card c2, Card c3) {
        ArrayList<Card> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);
        return list;
    }

    // evalHand Tests
    @Test
    void evalHandDetectsStraightFlush() {
        ArrayList<Card> h = hand(
                new Card('H', 10),
                new Card('H', 11),
                new Card('H', 12)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.STRAIGHT_FLUSH, result);
    }

    @Test
    void evalHandDetectsThreeOfAKind() {
        ArrayList<Card> h = hand(
                new Card('H', 10),
                new Card('C', 10),
                new Card('D', 10)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.THREE_KIND, result);
    }

    @Test
    void evalHandDetectsStraight() {
        ArrayList<Card> h = hand(
                new Card('H', 11),
                new Card('C', 10),
                new Card('S', 9)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.STRAIGHT, result);
    }

    @Test
    void evalHandDetectsStraightSpecialCase() {
        ArrayList<Card> h = hand(
                new Card('H', 14),
                new Card('C', 13),
                new Card('S', 12)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.STRAIGHT, result);
    }

    @Test
    void evalHandDetectsFlush() {
        ArrayList<Card> h = hand(
                new Card('H', 9),
                new Card('H', 2),
                new Card('H', 4)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.FLUSH, result);
    }

    @Test
    void evalHandDetectsPair() {
        ArrayList<Card> h = hand(
                new Card('H', 3),
                new Card('C', 9),
                new Card('S', 9)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.PAIR, result);
    }

    @Test
    void evalHandDetectsHighCardWhenNoOtherHand() {
        ArrayList<Card> h = hand(
                new Card('C', 2),
                new Card('D', 5),
                new Card('H', 9)
        );

        ThreeCardLogic.Hands result = ThreeCardLogic.evalHand(h);
        assertEquals(ThreeCardLogic.Hands.HIGH_CARD, result);
    }


    // evalWinnings tests
    @Test
    void evalPPWinningsPaysCorrectMultipliers() {
        int bet = 5;

        int sf = ThreeCardLogic.evalPPWinnings(
                hand(new Card('H', 10), new Card('H', 11), new Card('H', 12)), bet);
        int trips = ThreeCardLogic.evalPPWinnings(
                hand(new Card('C', 7), new Card('D', 7), new Card('H', 7)), bet);
        int straight = ThreeCardLogic.evalPPWinnings(
                hand(new Card('C', 4), new Card('D', 5), new Card('H', 6)), bet);
        int flush = ThreeCardLogic.evalPPWinnings(
                hand(new Card('S', 2), new Card('S', 8), new Card('S', 14)), bet);
        int pair = ThreeCardLogic.evalPPWinnings(
                hand(new Card('C', 9), new Card('D', 9), new Card('H', 4)), bet);
        int high = ThreeCardLogic.evalPPWinnings(
                hand(new Card('C', 2), new Card('D', 5), new Card('H', 9)), bet);

        assertEquals(5 * 40, sf);
        assertEquals(5 * 30, trips);
        assertEquals(5 * 6, straight);
        assertEquals(5 * 3, flush);
        assertEquals(5 * 1, pair);
        assertEquals(0, high);
    }

    @Test
    void dealerQualifiesIfHasQueenHighOrBetter() {
        ArrayList<Card> h = hand(
                new Card('H', 3),
                new Card('C', 14),
                new Card('S', 5)
        );
        assertTrue(ThreeCardLogic.dealerQualifies(h));
    }

    @Test
    void dealerDoesNotQualifyWhenHighCardBelowQueen() {
        ArrayList<Card> h = hand(
                new Card('H', 3),
                new Card('C', 11),
                new Card('S', 5)
        );
        assertFalse(ThreeCardLogic.dealerQualifies(h));
    }

    // compare hands tests

    @Test
    void compareHandsPlayerWinsHigherRankedHand() {
        ArrayList<Card> dealer = hand(
                new Card('H', 2),
                new Card('D', 5),
                new Card('C', 9)
        );  // high card only

        ArrayList<Card> player = hand(
                new Card('H', 9),
                new Card('D', 9),
                new Card('S', 3)
        );  // pair

        int result = ThreeCardLogic.compareHands(dealer, player);

        assertEquals(1, result, "1 should indicate player wins");
    }

    @Test
    void compareHandsDealerWinsHigherRankedHand() {
        ArrayList<Card> dealer = hand(
                new Card('H', 4),
                new Card('D', 5),
                new Card('C', 6)
        );  // straight

        ArrayList<Card> player = hand(
                new Card('H', 2),
                new Card('D', 3),
                new Card('S', 4)
        );  // lower straight

        int result = ThreeCardLogic.compareHands(dealer, player);

        assertEquals(2, result, "2 should indicate dealer wins");
    }

    @Test
    void compareHandsTieWhenSameHandsAndSameHighCards() {
        ArrayList<Card> dealer = hand(
                new Card('H', 10),
                new Card('D', 11),
                new Card('C', 12)
        );

        ArrayList<Card> player = hand(
                new Card('S', 10),
                new Card('C', 11),
                new Card('D', 12)
        );

        int result = ThreeCardLogic.compareHands(dealer, player);

        assertEquals(0, result, "0 should indicate a tie");
    }

    @Test
    void highestCardValReturnsMaxCardValue() {
        ArrayList<Card> h = hand(
                new Card('H', 6),
                new Card('D', 2),
                new Card('C', 14)
        );

        int max = ThreeCardLogic.highestCardVal(h);
        assertEquals(14, max, "Ace (14) should be the highest card value");
    }

}
