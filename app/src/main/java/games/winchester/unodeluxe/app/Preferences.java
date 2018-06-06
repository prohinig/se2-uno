package games.winchester.unodeluxe.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String KEY_ADVANCED_RULES = "advancedRules";
    private static final String KEY_ALLOW_CUSTOM_CARDS = "customCards";

    private SharedPreferences preferences;

    private Preferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean advancedRules() {
        return preferences.getBoolean(KEY_ADVANCED_RULES, false);
    }

    public boolean customCardsAllowed() {
        return preferences.getBoolean(KEY_ALLOW_CUSTOM_CARDS, false);
    }

    public void setAdvancedRulesAllowed(boolean allowed){
        preferences.edit().putBoolean(KEY_ADVANCED_RULES, allowed).apply();
    }
    public void setCustomCardsAllowed(boolean allowed){
        preferences.edit().putBoolean(KEY_ADVANCED_RULES, allowed).apply();
    }

    public static Preferences from(Context context) {
        return new Preferences(context.getSharedPreferences("UnoDeluxe", Context.MODE_PRIVATE));
    }
}
