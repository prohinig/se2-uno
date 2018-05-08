package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.List;


public class Player implements Serializable{

    private String name;
    private Hand hand;

    public Player(String name){
        this.name = name;
        this.hand = new Hand();
    }

    public Hand getHand (){
        return this.hand;
    }

    public void addCards(List<Card> cards){
        this.hand.addCards(cards);
    }
}
