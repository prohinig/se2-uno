package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class Shake implements Message {
    private boolean required;
    private long timestamp;
    private int player;

    public Shake(boolean required) {
        this.required = required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public boolean isRequired() {
        return required;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getPlayer() {
        return player;
    }
}
