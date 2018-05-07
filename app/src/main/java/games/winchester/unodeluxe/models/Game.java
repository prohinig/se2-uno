package games.winchester.unodeluxe.models;

import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.messages.CardsDealt;
import games.winchester.unodeluxe.messages.Setup;
import games.winchester.unodeluxe.messages.Turn;
import games.winchester.unodeluxe.utils.GameLogic;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.enums.Action;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.Direction;

public class Game {
    public static int MAXPLAYERS = 5;
    public static int STATE_PENDING = 0;
    public static int STATE_RUNNING = 1;


    // deck of cards
    private Deck deck;
    // stack where cards are laid on
    private Stack stack;
    // checks if game has been started
    private boolean gameStarted;
    // checks direction -> false: (index++) true: (index--)
    private boolean reverse;
    // color that is active does not always match topcard
    private CardColor activeColor;
    // players in the game, each player is one device
    private ArrayList<Player> players;
    private Player self;
    private GameActivity activity;
    // player that has the turn
    private int activePlayer;
    private int state;
    // keeps track of how many cards need to be drawn
    private int numberOfCardsToDraw;
    private Session session;
    private Turn turn;


    // something like a connection to join the game and receive requests or send messages
    // private Connection conn

    public Game(GameActivity activity) {
        this.deck = new Deck();
        this.stack = new Stack();
        this.reverse = false;
        this.players = new ArrayList<Player>();
        this.self = new Player("player1");
        this.activity = activity;
        this.state = Game.STATE_PENDING;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.turn = new Turn();
        // read player name from configuration
        this.players.add(self);
    }

    public Player getSelf() {
        return this.self;
    }

    public Game(String name, GameActivity activity) {
        // in this case the game is uninitialized and waits for message of master
    }
    //handles a whole turn
    public boolean handleTurn(Card c, Player p) {
        if (numberOfCardsToDraw != 0) {
            activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
            return false;
        }

//        session.send(turn);

        if (GameLogic.isPlayableCard(c, p.getHand(), getTopOfStackCard(), activeColor)) {
            boolean result = playCard(c, p);
            if(result){
                turn.cardPlayed = c;
                if(null != session){
                    session.send(turn);
                }
            }
            return result;
        } else {
            activity.notificationCardNotPlayable();
            return false;
        }

    }

    public void setSession(Session s){
        this.session = s;
    }

    public void messageReceived(Message m) {
        if(m instanceof Turn){
            // we received a turn a player made
            Turn turn = (Turn) m;
            if(session instanceof HostSession){
                // we are host so notify the others
                notifyPlayers((Turn) m);
            }

            if(null != turn.cardPlayed){
                this.layCard(turn.cardPlayed);
            }

            // if its my turn enable clicks
            boolean playersTurn = turn.player == players.indexOf(self);
            this.activity.setClicksEnabled(playersTurn);

        }else if(m instanceof Setup){

        }else if(m instanceof CardsDealt) {

        }
    }

    public void clientConnected() {
        //TODO

    }

    public void clientDisconnected() {
        //TODO
    }

    public void notifyPlayers(Turn turn) {
//      we receive a turn from one player and send it to all
        session.send(turn);
    }

    // check if card can be played and return result
    public boolean playCard(Card c, Player p) {

        p.getHand().removeCard(c);
        this.layCard(c);
        activeColor = c.getColor();

        handleAction(c);

        if (p.getHand().getCards().size() == 0) {
            activity.notificationGameWon();
            gameStarted = false;
        }

        return true;
    }

    public void handleAction(Card c) {
        switch (GameLogic.actionRequired(c)) {
            case DRAWTWO:
                numberOfCardsToDraw += 2;
                break;
            case DRAWFOUR:
                numberOfCardsToDraw += 4;
            case WISH:
                activity.wishAColor(this);
                break;
            case REVERSE:
                reverse = !reverse;
                break;
            case SKIP:
                //TODO implement Skip function
                break;
            case NONE:
                break;
        }
    }

    private void layCard(Card c) {
        this.stack.playCard(c);
        this.activity.updateTopCard(c.getGraphic());
    }

    public ArrayList<Card> handCards(int amount) {
        ArrayList<Card> cards = this.deck.deal(amount);
        self.getHand().addCards(cards);
        this.activity.addToHand(cards);
        return cards;
    }

    public Player join(String playerName) {
        if (this.players.size() + 1 <= Game.MAXPLAYERS) {
            Player player = new Player(playerName);
            this.players.add(player);
            return player;
        }

        return null;
    }

    public void startGame() {
        // for testing purposes its 0 but should be 1
        if (0 < this.players.size() && Game.STATE_PENDING == this.state) {
            // player next to dealer (=gamestarter) starts
            Card cardTopped = this.deck.deal(1).remove(0);

            //guarantees that no +4 Card is on top
            while (cardTopped.getSymbol() == CardSymbol.PLUSFOUR) {
                ArrayList<Card> tmp = new ArrayList<>();
                tmp.add(cardTopped);
                deck.addCards(tmp);
                deck.shuffle();
            }

            this.layCard(cardTopped);
            activeColor = cardTopped.getColor();
            this.activity.updateTopCard(cardTopped.getGraphic());

            // deal 3 * 3 for each player
            for (int i = 0; i < 7; i++) {
                for (Player p : this.players) {
                    this.handCards(1);
                }
            }

            this.state = Game.STATE_RUNNING;
            this.activePlayer = 0; //TODO: change to player to the left of game initiator
            this.gameStarted = true;
            handleAction(cardTopped);
        }
    }

    public void stackToDeck() {
        this.deck.addCards(this.stack.getCards());
        this.deck.shuffle();
    }

    public void setActiveColor(CardColor color) {
        this.activeColor = color;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public int getNumberOfCardsToDraw() {
        return numberOfCardsToDraw;
    }

    public void decrementNumberOfCardsToDraw() {
        numberOfCardsToDraw--;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int indexOfPlayer) {
        this.activePlayer = indexOfPlayer;
    }

    public CardColor getActiveColor() {
        return activeColor;
    }

    public Card getTopOfStackCard() {
        return stack.getTopCard();
    }


}
