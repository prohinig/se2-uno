package games.winchester.unodeluxe;

public class Game {
    private Deck deck;
    private Stack stack;
    //private Connection ??
    // private ArrayList<Player>

    public Game (){
        this.deck = new Deck();
        this.stack = new Stack();
    }

    public boolean playCard(){
        return false;
    }

    public void shuffle(){

    }

    public void stackToDeck(){
        this.deck.addCards(this.stack.getCards());
        this.deck.shuffle();
    }

    public boolean canPlayCard(Card c){
        return true;
    }

    public void actionRequired(){

    }
}
