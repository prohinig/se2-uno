package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class Accusation implements Message {
    private String accuser;
    private String accused;

    public Accusation(String accuser, String accused) {
        this.accuser = accuser;
        this.accused = accused;
    }

    public String getAccuser() {
        return accuser;
    }

    public String getAccused() {
        return accused;
    }
}
