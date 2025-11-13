import java.util.ArrayList;
import java.util.Collections;

public class Deck extends ArrayList<Card> {
    private static final long serialVersionUID = 1L;
    
    public Deck() {
        newDeck();
    }
    
    public void newDeck() {
        this.clear();
        populateDeck();
        Collections.shuffle(this);
    }
    
    private void populateDeck() {
        char[] suits = {'C', 'D', 'H', 'S'};
        
        for (char suit : suits) {
            for (int value = 2; value <= 14; value++) {
                this.add(new Card(suit, value));
            }
        }
    }
}
