package games.winchester.unodeluxe.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String KEY_ADVANCED_RULES = "advancedRules";
    private static final String KEY_ALLOW_CUSTOM_CARDS = "customCards";
    private static final String KEY_ALLOW_CHEATING = "cheating";

    private SharedPreferences sharedPreferences;

    private Preferences(SharedPreferences preferences) {
        sharedPreferences = preferences;
    }

    public boolean advancedRules() {
        return sharedPreferences.getBoolean(KEY_ADVANCED_RULES, false);
    }

    public boolean customCardsAllowed() {
        return sharedPreferences.getBoolean(KEY_ALLOW_CUSTOM_CARDS, false);
    }

    public void setAdvancedRulesAllowed(boolean allowed){
        sharedPreferences.edit().putBoolean(KEY_ADVANCED_RULES, allowed).apply();
    }
    public void setCustomCardsAllowed(boolean allowed){
        sharedPreferences.edit().putBoolean(KEY_ALLOW_CUSTOM_CARDS, allowed).apply();
    }

    public void setAllowCheating(boolean allowed){
        sharedPreferences.edit().putBoolean(KEY_ALLOW_CHEATING, allowed).apply();
    }

    public boolean isCheatingAllowed(){
        return sharedPreferences.getBoolean(KEY_ALLOW_CHEATING, false);
    }

    public static Preferences from(Context context) {
        return new Preferences(context.getSharedPreferences("UnoDeluxe", Context.MODE_PRIVATE));
    }
}
