package games.winchester.unodeluxe.models;

import java.util.ArrayList;
import java.util.List;


import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.Direction;
import games.winchester.unodeluxe.messages.Accusation;
import games.winchester.unodeluxe.messages.AccusationResult;
import games.winchester.unodeluxe.messages.Cheat;
import games.winchester.unodeluxe.messages.Name;
import games.winchester.unodeluxe.messages.Setup;
import games.winchester.unodeluxe.messages.Shuffle;
import games.winchester.unodeluxe.messages.Turn;
import games.winchester.unodeluxe.utils.GameLogic;
import games.winchester.unodeluxe.messages.Shake;


public class Game {

    // deck of cards
    private Deck deck;
    // stack where cards are laid on
    private Stack stack;
    // checks if game has been started
    private boolean gameStarted;

    private Direction direction = Direction.NORMAL;
    // color that is active does not always match topcard
    private CardColor activeColor;
    // players in the game, each player is one device
    private List<Player> players;

    private List<Shake> shakes;

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
    private boolean cheatingAllowed;
    private boolean includeCustomCards;
    private boolean ignoreNextTurn;

    private boolean shakeRequired = false;


    public Game(GameActivity activity) {
        this.activity = activity;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.activePlayer = 0;
        this.players = new ArrayList<>();
        this.self = null;
        this.colorWishPending = false;
    }

    public Game(GameActivity activity, Player admin) {
        // read chosen rules from preferences from Host
        this.advancedRules = activity.getPreferences().advancedRules();
        this.cheatingAllowed = activity.getPreferences().isCheatingAllowed();
        this.includeCustomCards = activity.getPreferences().customCardsAllowed();

        this.deck = includeCustomCards ? Decks.getCustomDeck() : Decks.getStandardDeck();
        this.stack = new Stack();
        this.players = new ArrayList<>();
        this.self = admin;
        this.activity = activity;
        this.gameStarted = false;
        this.numberOfCardsToDraw = 0;
        this.turn = new Turn();
        this.turn.setPlayerName(self.getName());
        // read player name from configuration
        this.players.add(self);
        this.colorWishPending = false;
        this.shakes = new ArrayList<>();
        this.name = self.getName();
    }

    public boolean cardClicked(Card c) {
        if (myTurn()) {
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
        if(!myTurn()){
            activity.notificationNotYourTurn();
            return;
        }

        if (!isGameStarted()) {
            startGame();
            return;
        }

        // if deck card count is low, shuffle stack into deck
        if (session instanceof HostSession) {
            shuffleNeeded();
        }

        if (numberOfCardsToDraw != 0) {
            handCards(1, name);
            turn.setCardsDrawn(turn.getCardsDrawn() + 1);
            decrementNumberOfCardsToDraw();

            // after drawing number of cards to draw the turn ends
            if (numberOfCardsToDraw == 0) {
                sendTurn();
            }
        } else {
            if (!GameLogic.hasPlayableCard(self.getHand(), getActiveColor(), getTopOfStackCard())) {
                List<Card> tmp = handCards(1, name);
                turn.setCardsDrawn(turn.getCardsDrawn() + 1);

                if (!GameLogic.isPlayableCard(tmp.get(0), self.getHand(), getTopOfStackCard(), getActiveColor())) {
                    sendTurn();
                }
            } else {
                activity.notificationHasPlayableCard();
            }
        }



    }

    // handles a whole turn
    private boolean handleTurn(Card c, Player p) {

        if (numberOfCardsToDraw != 0) {
            if(!advancedRules || turn.getCardsDrawn() != 0) {
                activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
                return false;
            }

            if(c.getSymbol() != getStack().getTopCard().getSymbol()) {
                activity.notificationNumberOfCardsToDraw(numberOfCardsToDraw);
                return false;
            }

            playCard(c, p);
            if (!colorWishPending) {
                sendTurn();
            }
            return true;
        }

        if (GameLogic.isPlayableCard(c, p.getHand(), getTopOfStackCard(), activeColor)) {
            playCard(c, p);

            if (!colorWishPending && p.getHand().getCards().size() != 1) {
                sendTurn();
            }

            return true;
        } else {
            activity.notificationCardNotPlayable();
            return false;
        }
    }

    public boolean cheat(Card c) {
        if (cheatingAllowed) {
            if (myTurn()) {
                if (self.hasCheated()) {
                    activity.notificationAlreadyCheated();
                    return false;
                } else {
                    if (numberOfCardsToDraw != 0) {
                        activity.notificationDrawCardsFirst(numberOfCardsToDraw);
                        return false;
                    } else if (self.getHand().cardsLeft() > 2) {
                        self.setCheated(true);
                        self.setAccuseable(true);
                        self.getHand().removeCard(c);
                        activity.notificationCheated();
                        sendCheat(c);
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
        } else {
            activity.notificationNoCheating();
            return false;
        }

    }


    public void opponentClicked(Object playerName) {
        if (cheatingAllowed) {
            if (myTurn()) {
                activity.showAccusePlayerDialog(playerName.toString());
            } else {
                activity.notificationNotAllowedToAccuse();
            }
        }
    }

    public void accusePlayer(String playerName) {
        if (null != session) {
            if (isHostGame()) {
                handleAccusation(new Accusation(self.getName(), playerName));
            } else {
                Accusation accusation = new Accusation(self.getName(), playerName);
                session.send(accusation);
            }
        }
    }

    private void handleAccusation(Accusation accusation) {
        Player accusedPlayer = getPlayerByName(accusation.getAccused());
        if (accusedPlayer != null) {
            AccusationResult ar = new AccusationResult(accusation.getAccuser(), accusedPlayer.getName(), accusedPlayer.isAccuseable());
            ar.setPenaltyCards(2);
            if (accusedPlayer.isAccuseable()) {
                accusedPlayer.setAccuseable(false);
            }
            handleAccusationResult(ar);
            session.send(ar);
        }
    }

    private void handleAccusationResult(AccusationResult accusationResult) {
        Player accusedPlayer = getPlayerByName(accusationResult.getAccused());
        Player accuser = getPlayerByName(accusationResult.getAccuser());
        if (accusedPlayer != null && accuser != null) {
            if (accusationResult.isAccusationCorrect()) {
                if (self.equals(accusedPlayer)) {
                    activity.notificationCorrectlyAccusedAccused(accuser.getName());
                    handCards(2, name);
                } else if (self.equals(accuser)) {
                    activity.notificationCorrectlyAccusedAccuser(accusedPlayer.getName());
                    handCards(2, accusedPlayer.getName());
                } else {
                    activity.notificationCorrectlyAccusedAll(accuser.getName(), accusedPlayer.getName());
                    handCards(2, accusedPlayer.getName());
                }
            } else {
                if (self.equals(accusedPlayer)) {
                    activity.notificationWronglyAccusedAccused(accuser.getName());
                    handCards(2, accuser.getName());
                } else if (self.equals(accuser)) {
                    activity.notificationWronglyAccusedAccuser(accusedPlayer.getName());
                    handCards(2, name);
                } else {
                    activity.notificationWronglyAccusedAll(accuser.getName(), accusedPlayer.getName());
                    handCards(2, accuser.getName());
                }
            }
        }
    }

    public void setSession(Session s) {
        this.session = s;
    }

    private void handleTurnMessage(Turn receivedTurn){
        // we received a turn a player made
        if (isHostGame()) {
            // we are host so notify the others
            notifyPlayers(receivedTurn);
        }

        activePlayer = receivedTurn.getActivePlayer();

        // card might not change
        if (null != receivedTurn.getActiveColor()) {
            setActiveColorInternal(receivedTurn.getActiveColor());
        }

        direction = receivedTurn.getDirection();

        numberOfCardsToDraw = receivedTurn.getCardsToDraw();

        Player p = getPlayerByName(receivedTurn.getPlayerName());
        if(p == null) return;


        Hand lastPlayersHand = p.getHand();
        // remove all cards the player drew from my deck
        if (0 < receivedTurn.getCardsDrawn() && !ignoreNextTurn) {
            handCards(receivedTurn.getCardsDrawn(), receivedTurn.getPlayerName());
        }

                if (3 > players.size()) {
                    Shake loserShake = new Shake();
                    loserShake.setLoser(name);
                    session.send(loserShake);
                    if (isHostGame()) {
                        handCards(4, name);
                    }
                } else if (isHostGame()) {
                    // 0 will always be lowest value
                    addOriginatorShake(receivedTurn.getPlayerName());
                }
        Card receivedTurnCardPlayed = receivedTurn.getCardPlayed();
        if (!ignoreNextTurn && null != receivedTurnCardPlayed) {
            this.layCard(receivedTurnCardPlayed);
            lastPlayersHand.removeCard(receivedTurnCardPlayed);
        }

        if (!ignoreNextTurn && receivedTurn.isShakeRequired()) {
            // special handling only 2 players
            if (2 < players.size()) {
                startShakeLimit();
            }

            if (3 > players.size()) {
                Shake loserShake = new Shake();
                loserShake.setLoser(name);
                session.send(loserShake);
                if(isHostGame()) {
                    handCards(4, name);
                }
            } else if (isHostGame()) {
                addOriginatorShake(receivedTurn.getPlayerName());
            }
        }

        if (!ignoreNextTurn && null != lastPlayersHand) {
            activity.updateCardCount(receivedTurn.getPlayerName(), lastPlayersHand.getCards().size());
        }

        ignoreNextTurn = false;
    }

    private void handleSetupMessage(Setup setup){
        deck = setup.getDeck();
        players = setup.getPlayers();
        setActiveColorInternal(setup.getActiveColor());
        stack = setup.getStack();
        activePlayer = setup.getActivePlayer();
        advancedRules = setup.isAdvancedRules();
        cheatingAllowed = setup.isCheatingAllowed();
        includeCustomCards = setup.isIncludeCustomCards();
        gameStarted = true;

        Card cardPlayed = stack.getTopCard();

        activity.updateTopCard(cardPlayed);
        for (Player p : players) {
            if (p.getName().equals(this.name)) {
                self = p;
                break;
            }
        }
        int indexOfMe = players.indexOf(self);
        ArrayList<String> opponents = new ArrayList<>();
        ArrayList<Integer> cardAmounts = new ArrayList<>();
        for (int i = 1; i < players.size(); i++) {
            opponents.add(players.get((indexOfMe + i) % players.size()).getName());
            cardAmounts.add(players.get((indexOfMe + i) % players.size()).getHand().cardsLeft());
        }

        activity.renderOpponents(opponents, cardAmounts);
        activity.addToHand(self.getHand().getCards());
    }

    private void handleCheatMessage(Cheat cheat){
        if (isHostGame()) {
            session.send(cheat);
        }
        // wasnt me
        if (!name.equals(cheat.getCheater())) {
            Player cheater = getPlayerByName(cheat.getCheater());

            if (cheater != null) {
                cheater.setCheated(true);
                cheater.setAccuseable(true);
                cheater.getHand().getCards().remove(cheat.getDissapearedCard());
                activity.updateCardCount(cheater.getName(), cheater.getHand().getCards().size());
            }
        }
    }

    private void handleShakeMessage(Shake shake){
        if (null != shake.getLoser()) {
            if(isHostGame()) {
                session.send(shake);
            }
            handCards(4, shake.getLoser());
        } else if (isHostGame()) {
            shakeReceived(shake);
        }
    }

    public void messageReceived(Message m) {
        if (m instanceof Turn) {
            handleTurnMessage((Turn) m);
        } else if (m instanceof Setup) {
            handleSetupMessage((Setup) m);
        } else if (m instanceof Name) {
            Name nameMessage = (Name) m;
            this.name = nameMessage.getName();
        } else if (m instanceof Cheat) {
            handleCheatMessage((Cheat) m);
        } else if (m instanceof Accusation) {
            if (isHostGame()) {
                handleAccusation((Accusation) m);
            }
        } else if (m instanceof AccusationResult) {
            handleAccusationResult((AccusationResult) m);
        } else if (m instanceof Shuffle) {
            this.deck = ((Shuffle) m).getDeck();
            this.stack.getCards();
            updateStackView();
        } else if (m instanceof Shake) {
            Shake shake = (Shake) m;

            if (null != shake.getLoser()) {
                if (isHostGame()) {
                    session.send(shake);
                }
                handCards(4, shake.getLoser());
            } else if (isHostGame()) {
                shakeReceived(shake);
            }
            handleShakeMessage((Shake) m);
        }

        if (session instanceof HostSession) {
            // if deck card count is low, shuffle stack into deck
            shuffleNeeded();
        }

        // if its my turn enable clicks
        if (myTurn()) {
            turn = new Turn();
            turn.setPlayerName(self.getName());
            turn.setCardsDrawn(0);
            activity.notificationYourTurn();
        }
    }

    private void addOriginatorShake(String playerName) {
        Shake originatorShake = new Shake();
        originatorShake.setPlayerName(playerName);
        originatorShake.setTimeStamp(0);
        shakeReceived(originatorShake);
    }

    private void shakeReceived(Shake shake) {
        shakes.add(shake);

        if (players.size() == shakes.size()) {
            determineShakeLoser();
        }
    }

    private void determineShakeLoser() {
        String highest = null;
        long lastShake = 1;
        for (Shake s : shakes) {
            if (s.getTimeStamp() > lastShake) {
                highest = s.getPlayerName();
            }
        }
        if (null != highest) {
            Shake loser = new Shake();
            loser.setLoser(highest);
            handCards(4, highest);
            session.send(loser);
        }
        // reset
        shakes = new ArrayList<>();
    }

    public void clientConnected(ClientSession s) {
        String playerName = "Player" + (players.size() + 1);
        Player playerConnected = new Player(playerName);
        players.add(playerConnected);
        s.send(new Name(playerName));
    }

    public void clientDisconnected() {
        // We simply do not handle it
    }

    private void notifyPlayers(Turn turn) {
//      we receive a turn from one player and send it to all
        session.send(turn);
    }


    // check if card can be played and return result
    private void playCard(Card c, Player p) {
        p.getHand().removeCard(c);
        layCard(c);
        setActiveColorInternal(c.getColor());
        turn.setCardPlayed(c);
        handleActionPlayed(c);

        if (p.getHand().getCards().isEmpty()) {
            turn.setActivePlayer(99);
            activity.notificationGameWon();
        }

        if (p.getHand().getCards().size() == 1) {
            activity.speechRecognition(p);
        }
    }

    public void unoAccepted() {
        if (!colorWishPending) {
            sendTurn();
        }
    }

    public void unoNotAccepted(Player p) {
        handCards(2, p.getName());
        turn.setCardsDrawn(turn.getCardsDrawn() + 2);
        if (!colorWishPending) {
            sendTurn();
        }
    }

    private void wishAColor() {
        activity.wishAColor(this);
        colorWishPending = true;
    }

    private void handleActionPlayed(Card c) {
        switch (c.getRequiredAction()) {
            case DRAWTWO:
                numberOfCardsToDraw += 2;
                break;
            case DRAWFOUR:
                numberOfCardsToDraw += 4;
                wishAColor();
                break;
            case WISH:
                wishAColor();
                break;
            case REVERSE:
                //if a reverse card is played in a 2-Player-Game it acts like a Skip-Card
                //therefore skipping the reverse action and going to Skip action.
                if (players.size() > 2) {
                    direction = direction.reverseDirection();
                } else {
                    setNextPlayer();
                }
                break;
            case SKIP:
                setNextPlayer();
                break;
            case SHAKE:
                turn.setShakeRequired(true);
                if (isHostGame() && 2 < players.size()) {
                    addOriginatorShake(name);
                }
                wishAColor();
                break;
            default:
                break;
        }
    }

    private void layCard(Card c) {
        this.stack.playCard(c);
        this.activity.updateTopCard(c);
    }

    private List<Card> handCards(@SuppressWarnings("SameParameterValue") int amount, String p) {
        List<Card> cards = this.deck.deal(amount);
        p = p == null ? name : p;
        Player player = getPlayerByName(p);

        if(player != null) {
            player.getHand().addCards(cards);

            if (player.equals(self)) {
                updateHand(cards);
            } else {
                activity.updateCardCount(player.getName(), player.getHand().cardsLeft());
            }
        }

        return cards;
    }

    private void updateHand(List<Card> cards) {
        this.activity.addToHand(cards);
    }

    private void setActiveColorInternal(CardColor color) {
        activeColor = color;
        activity.updateColor(color);
    }

    private void startGame() {
        if (1 < this.players.size() && !gameStarted) {
            // player next to dealer (=gamestarter) starts
            Card cardTopped = this.deck.deal(1).remove(0);

            //guarantees that no action card is topped as first card
            while (cardTopped.isActionCard()) {
                List<Card> tmp = new ArrayList<>();
                tmp.add(cardTopped);
                deck.addCards(tmp);
                deck.shuffle();
                cardTopped = this.deck.deal(1).remove(0);
            }

            layCard(cardTopped);
            setActiveColorInternal(cardTopped.getColor());

            for (int i = 0; i < 7; i++) {
                for (Player p : this.players) {
                    this.handCards(1, p.getName());
                }
            }

            ArrayList<String> opponents = new ArrayList<>();
            ArrayList<Integer> cardAmounts = new ArrayList<>();

            for (int i = 1; i < players.size(); i++) {
                opponents.add(players.get(i).getName());
                cardAmounts.add(players.get(i).getHand().cardsLeft());

            }

            activity.renderOpponents(opponents, cardAmounts);

            activePlayer = 1;
            gameStarted = true;

            session.send(new Setup(this));
        }
    }

    private void shuffleNeeded() {
        if (deck.cardsLeft() < 5 + numberOfCardsToDraw) {
            stackToDeck();
            session.send(new Shuffle(this.deck));
        }
    }

    private void stackToDeck() {
        List<Card> temp = new ArrayList<>();
        temp.addAll(this.deck.deal(this.deck.cardsLeft()));
        temp.addAll(stack.getCards());
        this.deck = new Deck(temp);
        this.deck.shuffle();
        updateStackView();
    }

    private void updateStackView() {
        activity.resetStackView();
        activity.notificationShuffle();
    }

    public void setActiveColor(CardColor color) {
        setActiveColorInternal(color);
        turn.setActiveColor(color);
        if (colorWishPending) {
            colorWishPending = false;
            sendTurn();
        }

    }

    private boolean isHostGame() {
        return session instanceof HostSession;
    }

    private boolean isClientGame() {
        return !isHostGame();
    }

    private void sendTurn() {
        if (null != session) {
            turn.setActivePlayer(setNextPlayer());
            turn.setActiveColor(activeColor);
            turn.setDirection(direction);
            turn.setCardsToDraw(numberOfCardsToDraw);
            session.send(turn);
            if (isClientGame()) {
                ignoreNextTurn = true;
            }
        }

        // we reset turn here to avoid sending same info twice
        // when same player is having turn twice
        turn = new Turn();
        turn.setPlayerName(self.getName());
    }

    private void sendCheat(Card c) {
        if (null != session) {
            Cheat cheat = new Cheat(self.getName(), c);
            session.send(cheat);
        }
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


    public List<Player> getPlayers() {
        return players;
    }

    private int setNextPlayer() {
        activePlayer = direction.getNextPlayerPos(activePlayer, players.size());

        return activePlayer;
    }

    public Session getSession() {
        return session;
    }

    private Player getPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public boolean isAdvancedRules() {
        return advancedRules;
    }

    public boolean isCheatingAllowed() {
        return cheatingAllowed;
    }

    public boolean isIncludeCustomCards() {
        return includeCustomCards;
    }

    private void startShakeLimit() {
        shakeRequired = true;
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                if (shakeRequired) {
                    deviceShakeRecognised();
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void deviceShakeRecognised() {
        if (shakeRequired) {
            long millis = System.currentTimeMillis() % 1000;
            shakeRequired = false;
            Shake shake = new Shake();
            shake.setPlayerName(name);
            shake.setTimeStamp(millis);
            if (!isHostGame()) {
                session.send(shake);
            } else {
                shakeReceived(shake);
            }
        }
    }
}
