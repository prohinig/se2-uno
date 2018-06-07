package games.winchester.unodeluxe.models;

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

public class Game {

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
    // for advanced ruleset (+2 and +4 can be stacked)
    private boolean advancedRules;
    // for sevenO ruleset (if 0 is played every hand is swaped in current directions hand, if 7 is played choose a other player and swap hands)
    private boolean sevenO;

    public Game(GameActivity activity) {
        this.reverse = false;
        this.activity = activity;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.activePlayer = 0;
        this.players = new ArrayList<>();
        this.self = null;
        this.colorWishPending = false;
        // TODO: change the initialisation of the additional rule variables (reading from file or light SQL)
        advancedRules = true;
        sevenO = true;
    }

    public Game(GameActivity activity, Player admin) {
        this.deck = new Deck();
        this.stack = new Stack();
        this.reverse = false;
        this.players = new ArrayList<>();
        this.self = admin;
        this.activity = activity;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.turn = new Turn();
        // read player name from configuration
        this.players.add(self);
        this.colorWishPending = false;
        // TODO: change the initialisation of the additional rule variables (reading from file or light SQL)
        advancedRules = true;
        sevenO = true;
    }

    public boolean cardClicked(Card c) {
        if(myTurn()) {
            return handleTurn(c, self);
        } else {
            activity.notificationNotYourTurn();
            return false;
        }
    }

    private boolean myTurn() {
        return self != null && activePlayer == players.indexOf(self);
    }

    public void deckClicked() {
        if (myTurn()) {
            if (!isGameStarted()) {
                startGame();
            } else {
                if (getNumberOfCardsToDraw() != 0) {
                    handCards(1, null);
                    turn.setCardsDrawn(turn.getCardsDrawn() + 1);
                    decrementNumberOfCardsToDraw();
                } else {
                    if (!GameLogic.hasPlayableCard(self.getHand(), getActiveColor(), getTopOfStackCard())) {
                        List<Card> tmp = handCards(1, null);
                        turn.setCardsDrawn(turn.getCardsDrawn() + 1);

                        if (!GameLogic.isPlayableCard(tmp.get(0), self.getHand(), getTopOfStackCard(), getActiveColor())) {
                            turn.setActivePlayer(setNextPlayer());
                            turn.setReverse(reverse);
                            turn.setActiveColor(activeColor);
                            turn.setCardsToDraw(numberOfCardsToDraw);
                            sendTurn();
                        }
                    } else {
                        activity.notificationHasPlayableCard();
                    }
                }
            }
        } else {
            activity.notificationNotYourTurn();
        }

    }

    //handles a whole turn
    private boolean handleTurn(Card c, Player p) {
        if (numberOfCardsToDraw != 0) {
            if (advancedRules && turn.getCardsDrawn() == 0) {
                if (c.getSymbol() == getStack().getTopCard().getSymbol()) {

                    playCard(c, p);
                    //TODO: should be place this code also in playCard-function or do another function just for that?
                    turn.setActivePlayer(setNextPlayer());
                    turn.setCardPlayed(c);
                    turn.setActiveColor(c.getColor());
                    turn.setReverse(reverse);
                    turn.setCardsToDraw(numberOfCardsToDraw);

                    sendTurn();

                    return true;
                } else {
                    activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
                    return false;
                }
            } else {
                activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
                return false;
            }
        }

        if (GameLogic.isPlayableCard(c, p.getHand(), getTopOfStackCard(), activeColor)) {
            playCard(c, p);

            turn.setActivePlayer(setNextPlayer());
            turn.setCardPlayed(c);
            turn.setActiveColor(c.getColor());
            turn.setReverse(reverse);
            turn.setCardsToDraw(numberOfCardsToDraw);

            if (!colorWishPending) {
                sendTurn();
            }

            return true;
        } else {
            activity.notificationCardNotPlayable();
            return false;
        }

    }

    public boolean cheat(Card c) {
        if(players.get(activePlayer).hasCheated()) {
            activity.notificationAlreadyCheated();
            return false;
        } else {
            players.get(activePlayer).setHasCheated(true);
            players.get(activePlayer).getHand().removeCard(c);
            stack.getCards().add(c);
            activity.notificationCheated();
            return true;
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

            Turn receivedTurn = (Turn) m;
            activePlayer = receivedTurn.getActivePlayer();

            // card might not change
            if (null != receivedTurn.getActiveColor()) {
                activeColor = receivedTurn.getActiveColor();
            }

            reverse = receivedTurn.isReverse();
            numberOfCardsToDraw = receivedTurn.getCardsToDraw();

            // remove all cards the player drew from my deck
            if (0 < receivedTurn.getCardsDrawn()) {
                deck.deal(receivedTurn.getCardsDrawn());
            }

            if (0 < receivedTurn.getCardsDrawn()) {
                deck.deal(receivedTurn.getCardsDrawn());
            }
            cardPlayed = receivedTurn.getCardPlayed();
            if (null != receivedTurn.getCardPlayed()) {
                this.layCard(receivedTurn.getCardPlayed());
            }


        } else if (m instanceof Setup) {
            Setup setup = (Setup) m;

            deck = setup.getDeck();
            players = setup.getPlayers();
            activeColor = setup.getActiveColor();
            stack = setup.getStack();
            activePlayer = setup.getActivePlayer();
            gameStarted = true;

            cardPlayed = stack.getTopCard();

            activity.updateTopCard(cardPlayed.getGraphic());
            for (Player p : players) {
                if (p.getName().equals(this.name)) {
                    self = p;
                    break;
                }
            }
            this.activity.addToHand(self.getHand().getCards());

        } else if (m instanceof Name) {
            Name nameMessage = (Name) m;
            this.name = nameMessage.getName();
        }


        // if its my turn enable clicks
        if (myTurn()) {
            turn = new Turn();
            turn.setCardsDrawn(0);
            activity.notificationYourTurn();
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
    private void playCard(Card c, Player p) {
        p.getHand().removeCard(c);
        this.layCard(c);
        activeColor = c.getColor();

        handleActionPlayed(c);

        if (p.getHand().getCards().isEmpty()) {
            activity.notificationGameWon();
            gameStarted = false;
        }
    }

    private void handleActionPlayed(Card c) {
        switch (GameLogic.actionRequired(c)) {
            case DRAWTWO:
                numberOfCardsToDraw += 2;
                break;
            case DRAWFOUR:
                numberOfCardsToDraw += 4;
            case WISH:
                activity.wishAColor(this);
                colorWishPending = true;
                break;
            case REVERSE:
                //if a reverse card is played in a 2-Player-Game it acts like a Skip-Card
                //therefore skipping the reverse action and going to Skip action.
                if (players.size() > 2) {
                    reverse = !reverse;
                    break;
                }
                setNextPlayer();
                break;
            case SKIP:
                setNextPlayer();
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

            // TODO: handle actions of the first card

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
        activeColor = color;
        turn.setActiveColor(color);
        if (colorWishPending) {
            colorWishPending = false;
            sendTurn();
        }

    }

    private void sendTurn() {
        if (null != session) {
            session.send(turn);
        }
        // we reset turn here to avoid sending same info twice
        // when same player is having turn twice
        turn = new Turn();
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

    private int setNextPlayer() {
        int current = activePlayer;
        if (isReverse()) {
            activePlayer = (current + players.size() - 1) % players.size();
        } else {
            activePlayer = (current + 1) % players.size();
        }
        return activePlayer;
    }

    public Session getSession() {
        return session;
    }
}
