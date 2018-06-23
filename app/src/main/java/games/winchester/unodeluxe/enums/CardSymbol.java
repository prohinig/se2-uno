package games.winchester.unodeluxe.enums;

/**
 * Created by christianprohinig on 10.04.18.
 */

public enum CardSymbol {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    REVERSE,
    SKIP,
    PLUSTWO,
    WISH,
    PLUSFOUR,
    SHAKE;

    public Action getAction(){

        switch(this){
            case PLUSTWO:   return Action.DRAWTWO;
            case REVERSE:   return Action.REVERSE;
            case SKIP:      return Action.SKIP;
            case WISH:      return Action.WISH;
            case PLUSFOUR:  return Action.DRAWFOUR;
            case SHAKE:     return Action.SHAKE;
            default:        return Action.NONE;
        }
    }

    public boolean isActionSymbol(){
        switch(this){
            case PLUSFOUR:
            case REVERSE:
            case SKIP:
            case WISH:
            case PLUSTWO:
            case SHAKE:
                return true;
            default:
                return false;
        }
    }
}
