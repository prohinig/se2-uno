package games.winchester.unodeluxe.models;

import java.util.ArrayList;
import java.util.List;

import at.laubi.network.messages.Message;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.messages.Setup;
import games.winchester.unodeluxe.messages.Turn;
import games.winchester.unodeluxe.utils.GameLogic;

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
    private List<Player> players;
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
        this.players = new ArrayList<>();
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

    public boolean cardClicked(Card c) {
        if (activePlayer == players.indexOf(self)) {
            return handleTurn(c, self);
        }
        return false;
    }

    public void deckClicked() {
        if (activePlayer == players.indexOf(self)) {

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
    public boolean handleTurn(Card c, Player p) {
        if (numberOfCardsToDraw != 0) {
            activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
            return false;
        }

        if (GameLogic.isPlayableCard(c, p.getHand(), getTopOfStackCard(), activeColor)) {
            boolean result = playCard(c, p);
            if (result) {
                turn.activePlayer = (++activePlayer) % players.size();
                turn.cardPlayed = c;
                turn.activeColor = c.getColor();

                if (null != session) {
                    session.send(turn);
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

            self = players.get(1);
            this.activity.addToHand(self.getHand().getCards());

        }

        // if its my turn enable clicks
        if (activePlayer == players.indexOf(self)) {
            turn = new Turn();
            if(null != cardPlayed){
                handleAction(cardPlayed);
            }
        }
    }

    public void clientConnected() {
        players.add(new Player("Player" + (players.size() + 1)));
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

        handleActionPlayed(c);

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
            case NONE:
                break;
        }
    }

    public void handleActionPlayed(Card c) {
        switch (GameLogic.actionRequired(c)) {
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

    public List<Card> handCards(int amount, Player p) {
        List<Card> cards = this.deck.deal(amount);
        p = p == null ? self : p;
        p.getHand().addCards(cards);
        if (p == self) {
            updateHand(cards);
        }
        return cards;
    }

    public void updateHand(List<Card> cards) {
        this.activity.addToHand(cards);
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
                List<Card> tmp = new ArrayList<>();
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
                    this.handCards(1, p);
                }
            }

            this.state = Game.STATE_RUNNING;
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


    public Deck getDeck() {
        return deck;
    }

    public Stack getStack() {
        return stack;
    }

    public boolean isReverse() {
        return reverse;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getState() {
        return state;
    }

    public Turn getTurn() {
        return turn;
    }
}
