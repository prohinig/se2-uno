package games.winchester.unodeluxe.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.models.Card;

public class CardGraphicResolver {
    private final Context context;
    private final Map<Card, Drawable> cache = new HashMap<>();
    
    public CardGraphicResolver(Context context) {
        this.context = context;
    }

    public Drawable resolveDrawable(Card card){
        Drawable drawable = cache.get(card);

        if(drawable == null) {
            drawable = findDrawable(card);
            cache.put(card, drawable);
        }

        return drawable;
    }

    public int resolveResourceId(Card card){
        final String resourceName = getResourceNameForCard(card);

        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    private Drawable findDrawable(Card card) {
        return context.getResources().getDrawable(resolveResourceId(card));
    }

    public static String getResourceNameForCard(Card card){
        String name = getNameForColor(card.getColor()) + getNameForSymbol(card.getSymbol());

        return name.isEmpty() ? "back" : name;
    }
    
    private static String getNameForColor(CardColor color){
        switch(color) {
            case RED: return "r";
            case YELLOW: return "y";
            case GREEN: return "g";
            case BLUE: return "b";
            default: return "";
        }
    }

    private static String getNameForSymbol(CardSymbol symbol){
        switch(symbol) {
            case ZERO: return "0";
            case ONE: return "1";
            case TWO: return "2";
            case THREE: return "3";
            case FOUR: return "4";
            case FIVE: return "5";
            case SIX: return "6";
            case SEVEN: return "7";
            case EIGHT: return "8";
            case NINE: return "9";
            case PLUSTWO: return "d2";
            case PLUSFOUR: return "draw4";
            case SKIP: return "s";
            case REVERSE: return "r";
            case WISH: return "wild";
            case SHAKE: return "shake";
            default: return "";
        }
    }
}
