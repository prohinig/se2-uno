package games.winchester.unodeluxe.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.app.Preferences;

public class SettingsDialog extends AlertDialog.Builder {

    Switch switchAdvancedRules;
    Switch swichAllowCustomCards;

    LayoutInflater inflater;

    private Preferences preferences;

    public SettingsDialog(Context context) {
        super(context);

        preferences = Preferences.from(context);
        inflater = LayoutInflater.from(context);

        View root = createContentView();

        loadSettings();

        setTitle("Hausregeln konfigurieren");
        setView(root);
        setPositiveButton("Speichern", (d, id) -> saveSettings());
        setNegativeButton("Abbrechen", (d, id) -> {});
    }

    private View createContentView(){
        View viewRoot = inflater.inflate(R.layout.layout_options, null);
        switchAdvancedRules = viewRoot.findViewById(R.id.switchStack);
        swichAllowCustomCards = viewRoot.findViewById(R.id.switchCustomCards);
        Button btnHelp = viewRoot.findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener((view) -> displayHelpText());

        return viewRoot;
    }

    private void saveSettings() {
        preferences.setAdvancedRulesAllowed(switchAdvancedRules.isChecked());
        preferences.setCustomCardsAllowed(swichAllowCustomCards.isChecked());
    }

    private void loadSettings() {
        switchAdvancedRules.setChecked(preferences.advancedRules());
        swichAllowCustomCards.setChecked(preferences.customCardsAllowed());
    }

    private void displayHelpText(){
        View view = inflater.inflate(R.layout.layout_rules_help, null);

        new AlertDialog.Builder(this.getContext())
                .setTitle(R.string.help_rules)
                .setView(view)
                .setPositiveButton("OK", (d, id) -> {})
                .create()
                .show();
    }
}
