import java.util.ArrayList;

public class Dealer {
    Deck theDeck;
    ArrayList<Card> dealersHand;
    
    public Dealer() {
        theDeck = new Deck();
        dealersHand = new ArrayList<>();
    }
    
    public ArrayList<Card> dealHand() {
        ArrayList<Card> hand = new ArrayList<>();
        
        if (theDeck.size() < 3) {
            theDeck.newDeck();
        }
        
        for (int i = 0; i < 3; i++) {
            hand.add(theDeck.remove(0));
        }
        
        return hand;
    }
    
    public ArrayList<Card> getDealersHand() {
        return dealersHand;
    }
    
    public void setDealersHand(ArrayList<Card> hand) {
        this.dealersHand = hand;
    }
}
