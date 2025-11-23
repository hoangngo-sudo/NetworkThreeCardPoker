import java.io.Serializable;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    int ante;
    int pairPlus;
    int cash;
    int play;
    int winningsThisRound;
    boolean hang;
    boolean playOver;
    boolean playerWon;
    boolean newRound;
    int buttonPressed; // 1=deal, 2=play, 3=fold, 4=fresh start
    
    String card1;
    String card2;
    String card3;
    String dCard1;
    String dCard2;
    String dCard3;
    
    int winner; // 0=push, 1=player, 2=dealer
    String pHandVal;
    String dHandVal;
    
    PokerInfo() {
        ante = 0;
        pairPlus = 0;
        cash = 200;
        play = 0;
        winningsThisRound = 0;
        hang = true;
        playOver = false;
        playerWon = false;
        newRound = true;
        buttonPressed = 0;
        
        card1 = card2 = card3 = "";
        dCard1 = dCard2 = dCard3 = "";
        
        winner = 0;
        pHandVal = "";
        dHandVal = "";
    }
    
    PokerInfo(int a, int pp, int c) {
        this();
        ante = a;
        pairPlus = pp;
        cash = c;
    }
}
