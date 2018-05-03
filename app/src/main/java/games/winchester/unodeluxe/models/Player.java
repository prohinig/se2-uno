package games.winchester.unodeluxe.models;

import java.util.ArrayList;

import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Hand;

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

    public String getType (){
        return this.type;
    }

    public void addCards(ArrayList<Card> cards){
        this.hand.addCards(cards);
    }
}
