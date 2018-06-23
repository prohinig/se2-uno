package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class Shake implements Message {
    private String playerName;
    private long timeStamp;
    private String loser;

    public Shake() {
        this.playerName = null;
        this.timeStamp = Long.MAX_VALUE;
        this.loser = null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }
}
