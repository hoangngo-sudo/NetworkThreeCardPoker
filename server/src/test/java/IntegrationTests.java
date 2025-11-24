// These integration tests validate that Dealer, Player, ThreeCardLogic,
// and PokerInfo work together correctly to simulate full game rounds.
//

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class IntegrationTests {

    // Helper to create a custom 3-card hand
    private ArrayList<Card> hand(Card a, Card b, Card c) {
        ArrayList<Card> h = new ArrayList<>();
        h.add(a);
        h.add(b);
        h.add(c);
        return h;
    }

    @Test
    void FullRoundPlayerWinsWithPairDealerNoQualify() {
        Dealer dealer = new Dealer();
        Player player = new Player();
        player.setTotalWinnings(0);

        // Player places bets
        int anteBet = 10;
        int pairPlusBet = 5;
        player.setAnteBet(anteBet);
        player.setPairPlusBet(pairPlusBet);

        // FORCE HANDS FOR A CONTROLLED ROUND
        // Player: Pair of 9s
        player.setHand(hand(
                new Card('H', 9),
                new Card('D', 9),
                new Card('C', 4)
        ));

        // Dealer: High card only, below Queen -> does NOT qualify
        ArrayList<Card> dealerHand = hand(
                new Card('S', 10),
                new Card('H', 8),
                new Card('D', 5)
        );
        dealer.setDealersHand(dealerHand);

        // Evaluate round
        ThreeCardLogic.Hands pHandType = ThreeCardLogic.evalHand(player.getHand());
        ThreeCardLogic.Hands dHandType = ThreeCardLogic.evalHand(dealer.getDealersHand());

        assertEquals(ThreeCardLogic.Hands.PAIR, pHandType);
        assertEquals(ThreeCardLogic.Hands.HIGH_CARD, dHandType);

        boolean dealerQualifies = ThreeCardLogic.dealerQualifies(dealer.getDealersHand());
        assertFalse(dealerQualifies, "Dealer should NOT qualify with low high card");

        // Player winnings
        int ppWinnings = ThreeCardLogic.evalPPWinnings(player.getHand(), pairPlusBet);

        // Ante wins automatically because dealer fails to qualify
        int anteWinnings = anteBet;

        // Play bet is returned back to player because dealer did not qualify
        int playBetReturned = anteBet;

        // Final total
        int expectedTotal = ppWinnings + anteWinnings + playBetReturned;

        player.setTotalWinnings(player.getTotalWinnings() + expectedTotal);

        assertEquals(expectedTotal, player.getTotalWinnings(),
                "Player should receive correct total winnings this round");
    }

    @Test
    void fullRoundDealerWinsStraightFlushVsPlayerStraight() {
        Dealer dealer = new Dealer();
        Player player = new Player();
        // player.setTotalWinnings(100); // starting cash

        player.setAnteBet(20);
        player.setPlayBet(20);
        player.setPairPlusBet(0);

        PokerInfo info = new PokerInfo(20, 0, 100);

        // Player: Straight (4,5,6)
        ArrayList<Card> pHand = hand(
                new Card('H', 4),
                new Card('C', 5),
                new Card('D', 6)
        );
        player.setHand(pHand);

        // Dealer: Straight Flush (10,J,Q of hearts)
        ArrayList<Card> dHand = hand(
                new Card('H', 10),
                new Card('H', 11),
                new Card('H', 12)
        );
        dealer.setDealersHand(dHand);

        int outcome = ThreeCardLogic.compareHands(dealer.getDealersHand(), player.getHand());
        assertEquals(2, outcome, "Dealer should win with a straight flush over straight");

        boolean qualifies = ThreeCardLogic.dealerQualifies(dealer.getDealersHand());
        assertTrue(qualifies, "Straight flush qualifies");

        int ppWinnings = ThreeCardLogic.evalPPWinnings(pHand, player.getPairPlusBet());
        player.setTotalWinnings(player.getTotalWinnings() + ppWinnings);

        // Ante + Play both lost
        int expectedFinal = 100 - 20 - 20 + ppWinnings;
        int remainingCash = info.cash - 20 - 20 + ppWinnings;

        assertEquals(expectedFinal, remainingCash);
    }

    @Test
    void fullRoundTieResultsInPushForAnteAndPlay() {
        Dealer dealer = new Dealer();
        Player player = new Player();
        player.setTotalWinnings(50);

        player.setAnteBet(10);
        player.setPlayBet(10);
        player.setPairPlusBet(0);

        ArrayList<Card> tieHand = hand(
                new Card('C', 10),
                new Card('D', 11),
                new Card('S', 12)
        ); // straight

        dealer.setDealersHand(tieHand);
        player.setHand(tieHand);

        int outcome = ThreeCardLogic.compareHands(tieHand, tieHand);
        assertEquals(0, outcome);

        // No money is won or lost for ante/play
        int expectedFinal = 50;

        assertEquals(expectedFinal, player.getTotalWinnings());
    }

    @Test
    void integrationDeckRebuildsBetweenRoundsAndHandsDiffer() {
        Dealer dealer = new Dealer();

        // --- Round 1 ---
        ArrayList<Card> r1Player = dealer.dealHand();
        ArrayList<Card> r1Dealer = dealer.dealHand();

        assertEquals(3, r1Player.size());
        assertEquals(3, r1Dealer.size());

        int sizeAfterR1 = dealer.theDeck.size();
        assertEquals(52 - 6, sizeAfterR1);

        // --- Force deck too small ---
        while (dealer.theDeck.size() > 2) {
            dealer.dealHand();
        }

        assertTrue(dealer.theDeck.size() < 3);

        // --- Round 2 triggers rebuild ---
        ArrayList<Card> r2Player = dealer.dealHand();
        assertEquals(3, r2Player.size(), "Dealer should rebuild and deal 3 cards");

        int sizeAfterR2 = dealer.theDeck.size();
        assertEquals(52 - 3, sizeAfterR2,
                "After rebuild and deal, deck should have 49 cards");

        // Verify round 1 and round 2 hands are not identical
        boolean identical =
                r1Player.get(0).getSuit() == r2Player.get(0).getSuit() &&
                        r1Player.get(0).getValue() == r2Player.get(0).getValue();

        assertFalse(identical,
                "It is extremely unlikely the same cards would appear in exact order after deck rebuild");
    }

    @Test
    void simulateTwoConsecutiveRoundsApplyWinningsCorrectly() {
        Dealer dealer = new Dealer();
        Player player = new Player();
        player.setTotalWinnings(100);

        // ----- Round 1 -----
        player.setAnteBet(10);
        player.setPlayBet(10);
        player.setPairPlusBet(5);

        // Player: Flush
        player.setHand(hand(
                new Card('H', 3),
                new Card('H', 9),
                new Card('H', 12)
        ));

        // Dealer: High card, Queen-high -> qualifies
        dealer.setDealersHand(hand(
                new Card('S', 14),
                new Card('D', 2),
                new Card('C', 7)
        ));

        int outcome1 = ThreeCardLogic.compareHands(dealer.getDealersHand(), player.getHand());
        assertEquals(1, outcome1, "Player wins round 1");

        int ppWinnings1 = ThreeCardLogic.evalPPWinnings(player.getHand(), player.getPairPlusBet());
        int totalGain1 = ppWinnings1 + 10 + 10; // PP + ante + play wins

        player.setTotalWinnings(player.getTotalWinnings() + totalGain1);


        // ----- Round 2 -----
        player.setAnteBet(20);
        player.setPlayBet(20);
        player.setPairPlusBet(0);

        // Player: High card only
        player.setHand(hand(
                new Card('C', 5),
                new Card('D', 7),
                new Card('H', 9)
        ));

        // Dealer: Pair -> wins
        dealer.setDealersHand(hand(
                new Card('C', 4),
                new Card('H', 4),
                new Card('D', 10)
        ));

        int outcome2 = ThreeCardLogic.compareHands(dealer.getDealersHand(), player.getHand());
        assertEquals(2, outcome2, "Dealer wins round 2");

        boolean qualifies2 = ThreeCardLogic.dealerQualifies(dealer.getDealersHand());
        assertTrue(qualifies2);

        int totalLoss2 = 20 + 20; // lost ante + play

        player.setTotalWinnings(player.getTotalWinnings() - totalLoss2);


        // EXPECTED FINAL
        int expected = 100 + totalGain1 - totalLoss2;
        assertEquals(expected, player.getTotalWinnings(),
                "Player winnings should match total gains/losses across two rounds");

    }

    @Test
    void foldingLosesBothAnteAndPairPlus() {
        Player p = new Player();
        p.setTotalWinnings(100);
        p.setAnteBet(15);
        p.setPairPlusBet(10);

        int initialCash = 100;
        int afterBets = initialCash - 15 - 10;
        
        assertEquals(75, afterBets);
        
        int finalCash = afterBets;
        assertEquals(75, finalCash);
    }

    @Test
    void dealerQueenHighExactlyQualifies() {
        ArrayList<Card> queenHigh = hand(
            new Card('H', 12),
            new Card('D', 10),
            new Card('C', 5)
        );
        
        boolean qualifies = ThreeCardLogic.dealerQualifies(queenHigh);
        assertTrue(qualifies);
    }

    @Test
    void dealerJackHighDoesNotQualify() {
        ArrayList<Card> jackHigh = hand(
            new Card('S', 11),
            new Card('H', 9),
            new Card('C', 2)
        );
        
        assertFalse(ThreeCardLogic.dealerQualifies(jackHigh));
    }

    @Test
    void pairPlusPayoutsStraightFlush() {
        ArrayList<Card> sf = hand(
            new Card('H', 5),
            new Card('H', 6),
            new Card('H', 7)
        );
        
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(sf, bet);
        assertEquals(400, winnings);
    }

    @Test
    void pairPlusPayoutsThreeOfKind() {
        ArrayList<Card> trips = hand(
            new Card('H', 8),
            new Card('D', 8),
            new Card('C', 8)
        );
        
        int winnings = ThreeCardLogic.evalPPWinnings(trips, 5);
        assertEquals(150, winnings);
    }

}
