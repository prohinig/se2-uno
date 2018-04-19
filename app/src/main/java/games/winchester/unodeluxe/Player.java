package games.winchester.unodeluxe;

import java.util.ArrayList;

public class Player {
    public static String TYPE_ADMIN = "admin";
    public static String TYPE_PLAYER = "player";

    private String name;
    private Hand hand;
    private String type;
    private String ip;

    public Player(String name, String type){
        this.name = name;
        this.type = type;
        this.hand = new Hand();
    }

    public Hand getHand (){
        return this.hand;
    }

    public void addCards(ArrayList<Card> cards){
        this.hand.addCards(cards);
    }
}
