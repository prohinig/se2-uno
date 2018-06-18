package games.winchester.unodeluxe.enums;

public enum Direction {
    NORMAL,
    REVERSE;

    public Direction reverseDirection(){
        return this == NORMAL ? REVERSE : NORMAL;
    }

    public int getNextPlayerPos(int curPlayer, int playerCount){
        return
                (this == NORMAL ? curPlayer + 1 : curPlayer + playerCount - 1)
                        % playerCount;
    }
}
