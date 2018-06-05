package games.winchester.unodeluxe.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import games.winchester.unodeluxe.R;

public class SettingsDialog extends AlertDialog.Builder {
    public static final String KEY_ADVANCED_RULES = "advancedRules";
    public static final String KEY_ALLOW_CUSTOM_CARDS = "customCards";

    Switch switchAdvancedRules;

    Switch swichAllowCustomCards;

    private SharedPreferences preferences;

    public SettingsDialog(Context context, SharedPreferences preferences) {
        super(context);
        this.preferences = preferences;

        View view = LayoutInflater.from(context).inflate(R.layout.layout_options, null);

        switchAdvancedRules = view.findViewById(R.id.switchStack);
        swichAllowCustomCards = view.findViewById(R.id.switchCustomCards);

        loadSettings();

        setTitle("Hausregeln konfigurieren");
        setView(view);
        setPositiveButton("Speichern", (d, id) -> saveSettings());
        setNegativeButton("Abbrechen", (d, id) -> {});
        setNeutralButton("Hilfe", (d, id) -> {});
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(KEY_ADVANCED_RULES, switchAdvancedRules.isChecked());
        editor.putBoolean(KEY_ALLOW_CUSTOM_CARDS, swichAllowCustomCards.isChecked());

        editor.apply();
    }

    private void loadSettings() {
        switchAdvancedRules.setChecked(preferences.getBoolean(KEY_ADVANCED_RULES, false));
        swichAllowCustomCards.setChecked(preferences.getBoolean(KEY_ALLOW_CUSTOM_CARDS, false));
    }
}
