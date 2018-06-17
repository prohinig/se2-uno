package games.winchester.unodeluxe.models;


import java.util.ArrayList;
import java.util.List;

import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.messages.Name;
import games.winchester.unodeluxe.messages.Setup;
import games.winchester.unodeluxe.messages.Turn;
import games.winchester.unodeluxe.utils.GameLogic;


import java.util.TimerTask;
import java.util.Timer;


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

    private final int REQ_CODE_SPEECH_INPUT = 100;

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            activity.SpeechRecognition();
        }
    };

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
                if (numberOfCardsToDraw != 0) {
                    handCards(1, null);
                    turn.setCardsDrawn(turn.getCardsDrawn() + 1);
                    decrementNumberOfCardsToDraw();

                    // after drawing number of cards to draw the turn ends
                    if(numberOfCardsToDraw == 0) {
                        sendTurn();
                    }
                } else {
                    if (!GameLogic.hasPlayableCard(self.getHand(), getActiveColor(), getTopOfStackCard())) {
                        List<Card> tmp = handCards(1, null);
                        turn.setCardsDrawn(turn.getCardsDrawn() + 1);

                        // if deck is empty, get cards from stack and put them to deck and shuffle
                        if(deck.getSize() == 0) {
                            stackToDeck();
                            deck.shuffle();
                            activity.notificationDeckShuffled();
                        }

                        if (!GameLogic.isPlayableCard(tmp.get(0), self.getHand(), getTopOfStackCard(), getActiveColor())) {
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
        if(myTurn()) {
            if (self.hasCheated()) {
                activity.notificationAlreadyCheated();
                return false;
            } else {
                if(numberOfCardsToDraw != 0) {
                    activity.notificationDrawCardsFirst(numberOfCardsToDraw);
                    return false;
                } else if (self.getHand().getSize() > 2) {
                    self.setCheated(true);
                    self.getHand().removeCard(c);
                    stack.getCards().add(c);
                    activity.notificationCheated();
                    return true;
                } else {
                    activity.notificationNotAllowedToCheat();
                    return false;
                }
            }
        } else {
            activity.notificationNotYourTurn();
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

        if(p.getHand().getCards().size()==1) {

            Timer t = new Timer();
            t.schedule(task, 1000);

            if(activity.speechResult==0){
                switch (GameLogic.actionRequired(c)) {
                    case DRAWTWO:
                        numberOfCardsToDraw += 2;
                        break;
                }
            }
        }
    }

    private void handleActionPlayed(Card c) {
        switch (GameLogic.actionRequired(c)) {
            case DRAWTWO:
                numberOfCardsToDraw += 2;
                break;
            case DRAWFOUR:
                numberOfCardsToDraw += 4;
                activity.wishAColor(this);
                colorWishPending = true;
                break;
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

            //guarantees that no action card is topped as first card
            while (isActionCard(cardTopped)) {
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

    public boolean isActionCard(Card c) {
        switch (c.getSymbol()) {
            case PLUSTWO:
            case PLUSFOUR:
            case SKIP:
            case REVERSE:
            case WISH:
                return true;
            default:
                return false;
        }
    }

    public void stackToDeck() {
        Card toppedCard = stack.getTopCard();
        stack.getCards().remove(toppedCard);
        deck.addCards(stack.getCards());
        stack.getCards().removeAll(stack.getCards());
        stack.playCard(toppedCard);
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
            turn.setActivePlayer(setNextPlayer());
            turn.setCardPlayed(stack.getTopCard());
            turn.setActiveColor(activeColor);
            turn.setReverse(reverse);
            turn.setCardsToDraw(numberOfCardsToDraw);
            session.send(turn);
        }
        // we reset turn here to avoid sending same info twice
        // when same player is having turn twice
        turn = new Turn();
    }

    private boolean isGameStarted() {
        return gameStarted;
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
