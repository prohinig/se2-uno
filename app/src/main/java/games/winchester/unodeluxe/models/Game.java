package games.winchester.unodeluxe.models;

import java.util.ArrayList;

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
    // direction that is played -> normal (index++) reverse (index--)
    private Direction direction;
    // color that is active does not always match topcard
    private CardColor activeColor;
    // players in the game, each player is one device
    private ArrayList<Player> players;
    private GameActivity activity;
    // index of the player that has the turn
    private int activePlayer;
    private int state;

    // something like a connection to join the game and receive requests or send messages
    // private Connection conn

    public Game(Player admin, GameActivity activity) {
        this.deck = new Deck();
        this.stack = new Stack();
        this.direction = Direction.NORMAL;
        this.players = new ArrayList<Player>();
        this.activity = activity;
        this.state = Game.STATE_PENDING;

        // read player name from configuration
        this.players.add(admin);
    }

    public Game(String name, GameActivity activity) {
        // in this case the game is uninitialized and waits for message of master
    }

    public void messageReceived() {
        boolean playersTurn = false;
        this.activity.setClicksEnabled(playersTurn);

        if(playersTurn){
            // handleTurn
        }
    }

    public void notifiyPlayers() {
        for(Player p : players) {
            if(Player.TYPE_PLAYER.equals(p.getType())) {

            }
        }
    }

    // check if card can be played and return result
    public boolean playCard(Card c, Player p) {
        // is it active player
        if (this.activePlayer == this.players.indexOf(p)) {
            if (GameLogic.canPlayCard(c, p.getHand(), this.stack.getTopCard(), this.activeColor)) {
                p.getHand().removeCard(c);
                this.layCard(c);
                activeColor = c.getColor();
                return true;
            }
        }

        return false;
    }

    public void deckClicked() {

    }

    //handles a whole turn
    public boolean handleTurn(Card c, Player p) {
        if (actionRequired() != Action.NONE) {
            switch (actionRequired()) {
                case SKIP:
                    return true;
                case DRAWTWO:
                    handCards(p, 2);
                    return true;
                case DRAWFOUR:
                    handCards(p, 4);
                    return true;
                default:
                    break;
            }
        }

        if (!playCard(c, p)) {
            handCards(p, 1);
        }

        return true;
    }

    private void layCard(Card c) {

        this.stack.playCard(c);
        this.activity.updateTopCard(c.getGraphic());
    }

    public void handCards(Player p, int amount) {
        ArrayList<Card> cards = this.deck.deal(amount);
        p.getHand().addCards(cards);
        this.activity.addToHand(cards);
    }

    public Action actionRequired() {
        return GameLogic.actionRequired(stack.getTopCard());
    }

    public Player join(String playerName) {
        if (this.players.size() + 1 <= Game.MAXPLAYERS) {
            Player player = new Player(playerName, Player.TYPE_PLAYER);
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
            this.layCard(cardTopped);
            activeColor = cardTopped.getColor();
            this.activity.updateTopCard(cardTopped.getGraphic());

            // deal 3 * 3 for each player
            for (int i = 0; i <= 2; i++) {
                for (Player p : this.players) {
                    this.handCards(p, 3);
                }
            }

            this.state = Game.STATE_RUNNING;
            this.activePlayer = 0;
        }
    }

    public void stackToDeck() {
        this.deck.addCards(this.stack.getCards());
        this.deck.shuffle();
    }


}
