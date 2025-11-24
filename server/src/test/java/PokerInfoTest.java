import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class PokerInfoTest {

    private PokerInfo info;

    @BeforeEach
    public void setUp(){
        info = new PokerInfo();
    }

    @Test
    void defaultConstructorInitializesToExpectedDefaults() {

        assertEquals(0, info.ante);
        assertEquals(0, info.pairPlus);
        assertEquals(200, info.cash);
        assertEquals(0, info.play);
        assertEquals(0, info.winningsThisRound);

        assertTrue(info.hang, "hang should default to true");
        assertFalse(info.playOver, "playOver should default to false");
        assertFalse(info.playerWon, "playerWon should default to false");
        assertTrue(info.newRound, "newRound should default to true");
        assertEquals(0, info.buttonPressed);

        assertEquals("", info.card1);
        assertEquals("", info.card2);
        assertEquals("", info.card3);
        assertEquals("", info.dCard1);
        assertEquals("", info.dCard2);
        assertEquals("", info.dCard3);

        assertEquals(0, info.winner);
        assertEquals("", info.pHandVal);
        assertEquals("", info.dHandVal);
    }

    @Test
    void threeArgConstructorSetsAntePairPlusCash() {

        PokerInfo info1 = new PokerInfo(10, 5, 100);

        assertEquals(10, info1.ante);
        assertEquals(5, info1.pairPlus);
        assertEquals(100, info1.cash);

        // other defaults should still hold
        assertTrue(info1.hang);
        assertTrue(info1.newRound);
        assertEquals(0, info1.play);
    }

    @Test
    void canStoreCardStringsAndWinnerInfo() {

        info.card1 = "AH";
        info.card2 = "KH";
        info.card3 = "QH";
        info.dCard1 = "2C";
        info.dCard2 = "3D";
        info.dCard3 = "4H";

        info.pHandVal = "Straight Flush";
        info.dHandVal = "Straight";
        info.winner = 1; // player wins

        assertEquals("AH", info.card1);
        assertEquals("KH", info.card2);
        assertEquals("QH", info.card3);
        assertEquals("2C", info.dCard1);
        assertEquals("3D", info.dCard2);
        assertEquals("4H", info.dCard3);

        assertEquals("Straight Flush", info.pHandVal);
        assertEquals("Straight", info.dHandVal);
        assertEquals(1, info.winner);
    }

    @Test
    void canRepresentEndOfRoundState() {

        PokerInfo info1 = new PokerInfo(10, 0, 90);

        // chose to play
        info1.play = 1;
        info1.playOver = true;
        info1.playerWon = true;
        info1.newRound = false;
        info1.winningsThisRound = 20;

        assertEquals(1, info1.play);
        assertTrue(info1.playOver);
        assertTrue(info1.playerWon);
        assertFalse(info1.newRound);
        assertEquals(20, info1.winningsThisRound);
    }



}
