import java.util.ArrayList;

public class Player {
    ArrayList<Card> hand;
    int anteBet;
    int playBet;
    int pairPlusBet;
    int totalWinnings;
    
    public Player() {
        hand = new ArrayList<>();
        anteBet = 0;
        playBet = 0;
        pairPlusBet = 0;
        totalWinnings = 0;
    }
    
    public ArrayList<Card> getHand() {
        return hand;
    }
    
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }
    
    public int getAnteBet() {
        return anteBet;
    }
    
    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }
    
    public int getPlayBet() {
        return playBet;
    }
    
    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }
    
    public int getPairPlusBet() {
        return pairPlusBet;
    }
    
    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }
    
    public int getTotalWinnings() {
        return totalWinnings;
    }
    
    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }
}
