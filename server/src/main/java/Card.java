import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    
    char suit;
    int value;
    
    Card(char suit, int value) {
        this.suit = suit;
        this.value = value;
    }
    
    public char getSuit() {
        return suit;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        String valueStr;
        if (value == 14) valueStr = "A";
        else if (value == 13) valueStr = "K";
        else if (value == 12) valueStr = "Q";
        else if (value == 11) valueStr = "J";
        else valueStr = String.valueOf(value);
        
        return valueStr + suit;
    }
}
