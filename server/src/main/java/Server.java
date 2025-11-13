import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {
    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<>();
    TheServer server;
    private Consumer<Serializable> callback;
    int port;
    public boolean isRunning = false;
    
    public Server(Consumer<Serializable> call) {
        callback = call;
        this.port = 5555;
    }
    
    public Server(Consumer<Serializable> call, String text) {
        callback = call;
        try {
            this.port = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            this.port = 5555;
        }
    }
    
    public void startServer() {
        if (!isRunning) {
            server = new TheServer();
            server.start();
            isRunning = true;
        }
    }
    
    public void stopServer() {
        if (isRunning) {
            try {
                isRunning = false;
                for (ClientThread client : clients) {
                    try {
                        client.connection.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
                clients.clear();
                if (server != null && server.mysocket != null) {
                    server.mysocket.close();
                }
                callback.accept("Server stopped");
            } catch (Exception e) {
                callback.accept("Error stopping server: " + e.getMessage());
            }
        }
    }
    
    public int getClientCount() {
        return clients.size();
    }
    
    class TheServer extends Thread {
        ServerSocket mysocket;
        
        public void run() {
            try {
                mysocket = new ServerSocket(port);
                callback.accept("Server is waiting for a client on port " + port);
                
                while (isRunning) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("Client " + count + " has connected to server");
                    clients.add(c);
                    c.start();
                    count++;
                    JavaFXTemplate.updateClientCount(clients.size());
                }
            } catch (Exception e) {
                if (isRunning) {
                    callback.accept("Server socket did not launch: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    class ClientThread extends Thread {
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        Player player;
        Dealer dealer;
        
        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
            this.player = new Player();
            this.dealer = new Dealer();
        }
        
        public void run() {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
                connection.setTcpNoDelay(true);
                
                // Send initial welcome message to client
                PokerInfo welcome = new PokerInfo();
                welcome.buttonPressed = 0; // 0 means ready to play
                out.writeObject(welcome);
                out.reset();
                callback.accept("Client " + count + " - Sent initial welcome message");
            } catch (Exception e) {
                callback.accept("Streams not open for client " + count);
                return;
            }
            
            while (true) {
                try {
                    PokerInfo data = (PokerInfo) in.readObject();
                    
                    // Process the game based on button pressed
                    if (data.buttonPressed == 1) { // Deal button
                        handleDeal(data);
                    } else if (data.buttonPressed == 2) { // Play button
                        handlePlay(data);
                    } else if (data.buttonPressed == 3) { // Fold button
                        handleFold(data);
                    }
                    
                    out.writeObject(data);
                    out.reset();
                    
                } catch (Exception e) {
                    callback.accept("Client " + count + " disconnected");
                    clients.remove(this);
                    break;
                }
            }
        }
        
        private void handleDeal(PokerInfo data) {
            // Validate bets
            if (data.ante < 5 || data.ante > 25) {
                callback.accept("Client " + count + " has invalid ante bet: $" + data.ante);
                return;
            }
            
            if (data.pairPlus > 0 && (data.pairPlus < 5 || data.pairPlus > 25)) {
                callback.accept("Client " + count + " has invalid pair plus bet: $" + data.pairPlus);
                return;
            }
            
            // Set player bets
            player.setAnteBet(data.ante);
            player.setPairPlusBet(data.pairPlus);
            
            // Deal cards
            dealer.theDeck.newDeck();
            ArrayList<Card> playerHand = dealer.dealHand();
            ArrayList<Card> dealerHand = dealer.dealHand();
            
            player.setHand(playerHand);
            dealer.setDealersHand(dealerHand);
            
            // Send cards to client
            data.card1 = playerHand.get(0).toString();
            data.card2 = playerHand.get(1).toString();
            data.card3 = playerHand.get(2).toString();
            data.dCard1 = dealerHand.get(0).toString();
            data.dCard2 = dealerHand.get(1).toString();
            data.dCard3 = dealerHand.get(2).toString();
            
            // Evaluate hands
            data.pHandVal = ThreeCardLogic.evalHand(playerHand).getName();
            data.dHandVal = ThreeCardLogic.evalHand(dealerHand).getName();
            
            callback.accept("Client " + count + " has dealt: Ante=$" + data.ante + 
                          ", PP=$" + data.pairPlus + ", Player: " + data.pHandVal);
        }
        
        private void handlePlay(PokerInfo data) {
            player.setPlayBet(player.getAnteBet());
            data.play = player.getPlayBet();
            
            int winnings = 0;
            ArrayList<Card> playerHand = player.getHand();
            ArrayList<Card> dealerHand = dealer.getDealersHand();
            
            // Evaluate Pair Plus first
            if (player.getPairPlusBet() > 0) {
                int ppWinnings = ThreeCardLogic.evalPPWinnings(playerHand, player.getPairPlusBet());
                if (ppWinnings > 0) {
                    winnings += ppWinnings;
                    callback.accept("Client " + count + " has won Pair Plus: $" + ppWinnings);
                } else {
                    winnings -= player.getPairPlusBet();
                    callback.accept("Client " + count + " has lost Pair Plus: -$" + player.getPairPlusBet());
                }
            }
            
            // Check if dealer qualifies
            boolean dealerQualifies = ThreeCardLogic.dealerQualifies(dealerHand);
            
            if (!dealerQualifies) {
                // Dealer doesn't qualify - both ante and play wagers are returned (pushed)
                // No winnings, no losses from ante/play bets
                callback.accept("Client " + count + " has dealer's cards does not qualify (ante pushed)");
                data.winner = 0; // Indicate dealer didn't qualify
            } else {
                // Compare hands
                int result = ThreeCardLogic.compareHands(dealerHand, playerHand);
                data.winner = result;
                
                if (result == 1) { // Player wins
                    int antePlayWin = (player.getAnteBet() + player.getPlayBet()) * 2;
                    winnings += antePlayWin;
                    callback.accept("Client " + count + " has player wins: $" + antePlayWin);
                } else if (result == 2) { // Dealer wins
                    int loss = player.getAnteBet() + player.getPlayBet();
                    winnings -= loss;
                    callback.accept("Client " + count + " has dealer wins: -$" + loss);
                } else { // Tie
                    callback.accept("Client " + count + " cards are pushed. Tie game");
                }
            }
            
            player.setTotalWinnings(player.getTotalWinnings() + winnings);
            data.winningsThisRound = winnings;
            data.cash = player.getTotalWinnings();
            
            callback.accept("Client " + count + " has total winnings: $" + data.cash);
        }
        
        private void handleFold(PokerInfo data) {
            int loss = player.getAnteBet();
            if (player.getPairPlusBet() > 0) {
                loss += player.getPairPlusBet();
            }
            
            player.setTotalWinnings(player.getTotalWinnings() - loss);
            data.winningsThisRound = -loss;
            data.cash = player.getTotalWinnings();
            
            callback.accept("Client " + count + " has folded: -$" + loss + ", Total: $" + data.cash);
        }
    }
}
