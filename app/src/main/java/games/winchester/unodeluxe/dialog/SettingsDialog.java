package games.winchester.unodeluxe.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.app.Preferences;

public class SettingsDialog extends AlertDialog.Builder {

    Switch switchAdvancedRules;

    Switch swichAllowCustomCards;

    private Preferences preferences;

    public SettingsDialog(Context context) {
        super(context);

        this.preferences = Preferences.from(context);

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
        preferences.setAdvancedRulesAllowed(switchAdvancedRules.isChecked());
        preferences.setCustomCardsAllowed(swichAllowCustomCards.isChecked());
    }

    private void loadSettings() {
        switchAdvancedRules.setChecked(preferences.advancedRules());
        swichAllowCustomCards.setChecked(preferences.customCardsAllowed());
    }
}
