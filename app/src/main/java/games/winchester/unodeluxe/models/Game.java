package games.winchester.unodeluxe.models;

import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.messages.Name;
import games.winchester.unodeluxe.messages.Setup;
import games.winchester.unodeluxe.messages.Turn;
import games.winchester.unodeluxe.utils.GameLogic;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.enums.CardColor;

public class Game {
    public static int MAXPLAYERS = 5;

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
    private List<Player> players;
    private Player self;
    private GameActivity activity;
    // player that has the turn
    private int activePlayer;
    // keeps track of how many cards need to be drawn
    private int numberOfCardsToDraw;
    // session
    private Session session;
    private Turn turn;
    // player name
    private String name;
    private boolean colorWishPending;

    public Game(GameActivity activity) {
        this.reverse = false;
        this.activity = activity;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.activePlayer = 0;
        this.players = new ArrayList<Player>();
        this.self = null;
        this.colorWishPending = false;
    }

    public Game(GameActivity activity, Player admin) {
        this.deck = new Deck();
        this.stack = new Stack();
        this.reverse = false;
        this.players = new ArrayList<Player>();
        this.self = admin;
        this.activity = activity;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.turn = new Turn();
        // read player name from configuration
        this.players.add(self);
        this.colorWishPending = false;
    }

    public boolean cardClicked(Card c) {
        if (myTurn()) {
            return handleTurn(c, self);
        }
        return false;
    }

    private boolean myTurn() {
        if(self != null) {
            if (activePlayer == players.indexOf(self)) {
                return true;
            }
        }
        return false;
    }

    public void deckClicked() {
        if (myTurn()) {
            if (!isGameStarted()) {
                startGame();
            } else {
                if (getNumberOfCardsToDraw() != 0) {
                    handCards(1, null);
                    decrementNumberOfCardsToDraw();
                } else {
                    if (!GameLogic.hasPlayableCard(self.getHand(), getActiveColor(), getTopOfStackCard())) {
                        List<Card> tmp = handCards(1, null);

                        if (GameLogic.isPlayableCard(tmp.get(0), self.getHand(), getTopOfStackCard(), getActiveColor())) {
                            //TODO: player is allowed to play drawn card if its playable
                        }

                    } else {
                        activity.notificationHasPlayableCard();
                    }
                }
            }
        }

    }

    //handles a whole turn
    private boolean handleTurn(Card c, Player p) {
        if (numberOfCardsToDraw != 0) {
            activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
            return false;
        }

        if (GameLogic.isPlayableCard(c, p.getHand(), getTopOfStackCard(), activeColor)) {
            boolean result = playCard(c, p);
            if (result) {
                turn.activePlayer = setNextPlayer();
                turn.cardPlayed = c;
                turn.activeColor = c.getColor();
                turn.reverse = reverse;

                if (null != session) {
                    if(!colorWishPending) {
                        session.send(turn);
                    }
                }
            }
            return result;
        } else {
            activity.notificationCardNotPlayable();
            return false;
        }

    }

    public void setSession(Session s) {
        this.session = s;
    }

    public void messageReceived(Message m) {
        Card cardPlayed = null;
        if (m instanceof Turn) {
            // we received a turn a player made
            if (session instanceof HostSession) {
                // we are host so notify the others
                notifyPlayers((Turn) m);
            }

            Turn turn = (Turn) m;
            activePlayer = turn.activePlayer;
            activeColor = turn.activeColor;
            reverse = turn.reverse;

            if(0 < turn.cardsDrawn) {
                deck.deal(turn.cardsDrawn);
            }
            cardPlayed = turn.cardPlayed;
            if (null != turn.cardPlayed) {
                this.layCard(turn.cardPlayed);
            }


        } else if (m instanceof Setup) {
            Setup setup = (Setup) m;

            deck = setup.deck;
            players = setup.players;
            activeColor = setup.activeColor;
            stack = setup.stack;
            activePlayer = setup.activePlayer;
            gameStarted = true;

            cardPlayed = stack.getTopCard();

            activity.updateTopCard(cardPlayed.getGraphic());
            for(Player p : players){
                if(p.getName().equals(this.name)){
                    self = p;
                    break;
                }
            }
            this.activity.addToHand(self.getHand().getCards());

        } else if (m instanceof Name) {
            Name name = (Name) m;
            this.name = name.name;
        }


        // if its my turn enable clicks
        if (myTurn()) {
            turn = new Turn();
            if(null != cardPlayed){
                handleAction(cardPlayed);
            }
        }

    }

    public void clientConnected(ClientSession s) {
        String playerName = "Player" + (players.size() + 1);
        Player playerConnected = new Player(playerName);
        players.add(playerConnected);
        s.send(new Name(playerName));
    }

    public void clientDisconnected() {
        //TODO: client disconnected
    }

    private void notifyPlayers(Turn turn) {
//      we receive a turn from one player and send it to all
        session.send(turn);
    }

    // check if card can be played and return result
    private boolean playCard(Card c, Player p) {

        p.getHand().removeCard(c);
        this.layCard(c);
        activeColor = c.getColor();

        handleActionPlayed(c);

        if (p.getHand().getCards().size() == 0) {
            activity.notificationGameWon();
            gameStarted = false;
        }

        return true;
    }

    private void handleAction(Card c) {
        switch (GameLogic.actionRequired(c)) {
            case DRAWTWO:
                numberOfCardsToDraw += 2;
                break;
            case DRAWFOUR:
                numberOfCardsToDraw += 4;
            default:
                break;
        }
    }

    private void handleActionPlayed(Card c) {
        switch (GameLogic.actionRequired(c)) {
            case WISH:
            case DRAWFOUR:
                activity.wishAColor(this);
                colorWishPending = true;
                break;
            case REVERSE:
                //if a reverse card is played in a 2-Player-Game it acts like a Skip-Card
                //therefore skipping the reverse action and going to Skip action.
                if(players.size() > 2) {
                    reverse = !reverse;
                    break;
                }
            case SKIP:
                activePlayer = setNextPlayer();
                break;
            default:
                break;
        }
    }

    private void layCard(Card c) {
        this.stack.playCard(c);
        this.activity.updateTopCard(c.getGraphic());
    }

    private List<Card> handCards(@SuppressWarnings("SameParameterValue") int amount, Player p) {
        List<Card> cards = this.deck.deal(amount);
        p = p == null ? self : p;
        p.addCards(cards);
        if (p == self) {
            updateHand(cards);
        }
        return cards;
    }

    private void updateHand(List<Card> cards) {
        this.activity.addToHand(cards);
    }

    private void startGame() {
        if (1 < this.players.size() && !gameStarted) {
            // player next to dealer (=gamestarter) starts
            Card cardTopped = this.deck.deal(1).remove(0);

            //guarantees that no +4 Card is on top
            while (cardTopped.getSymbol() == CardSymbol.PLUSFOUR) {
                List<Card> tmp = new ArrayList<>();
                tmp.add(cardTopped);
                deck.addCards(tmp);
                deck.shuffle();
                cardTopped = this.deck.deal(1).remove(0);
            }

            this.layCard(cardTopped);
            activeColor = cardTopped.getColor();
            this.activity.updateTopCard(cardTopped.getGraphic());

            for (int i = 0; i < 7; i++) {
                for (Player p : this.players) {
                    this.handCards(1, p);
                }
            }

            this.activePlayer = 1;
            this.gameStarted = true;

            session.send(new Setup(this));
        }
    }

    public void stackToDeck() {
        this.deck.addCards(this.stack.getCards());
        this.deck.shuffle();
    }

    public void setActiveColor(CardColor color) {
        this.activeColor = color;
        this.turn.activeColor = color;
        if(colorWishPending){
            colorWishPending = false;
            session.send(turn);
        }

    }

    private boolean isGameStarted() {
        return gameStarted;
    }

    private int getNumberOfCardsToDraw() {
        return numberOfCardsToDraw;
    }

    private void decrementNumberOfCardsToDraw() {
        numberOfCardsToDraw--;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public CardColor getActiveColor() {
        return activeColor;
    }

    private Card getTopOfStackCard() {
        return stack.getTopCard();
    }

    public Deck getDeck() {
        return deck;
    }

    public Stack getStack() {
        return stack;
    }

    private boolean isReverse() {
        return reverse;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Turn getTurn() {
        return turn;
    }

    private int setNextPlayer() {
        if(isReverse()) {
            return ((--activePlayer) + players.size()) % players.size();
        } else {
            return (++activePlayer) % players.size();
        }
    }
}
