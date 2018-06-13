package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class AccusationResult implements Message {
    private String accuser;
    private String accused;
    private boolean accusationCorrect;
    private int penaltyCards;

    public AccusationResult(String accuser, String accused, boolean accusationCorrect) {
        this.accuser = accuser;
        this.accused = accused;
        this.accusationCorrect = accusationCorrect;
        penaltyCards = 0;
    }

    public String getAccuser() {
        return accuser;
    }

    public String getAccused() {
        return accused;
    }

    public boolean isAccusationCorrect() {
        return accusationCorrect;
    }

    public int getPenaltyCards() {
        return penaltyCards;
    }

    public void setPenaltyCards(int penaltyCards) {
        this.penaltyCards = penaltyCards;
    }
}
