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

    private Switch switchAdvancedRules;
    private Switch swichAllowCustomCards;

    private LayoutInflater inflater;

    private Preferences preferences;

    public SettingsDialog(Context context) {
        super(context);

        preferences = Preferences.from(context);
        inflater = LayoutInflater.from(context);

        View root = createContentView();

        setTitle(R.string.configure_houserules);
        setView(root);
        setPositiveButton(R.string.save, (d, id) -> saveSettings());
        setNegativeButton(R.string.cancel, (d, id) -> {});
    }

    private View createContentView(){
        View viewRoot = inflater.inflate(R.layout.layout_options, null);
        switchAdvancedRules = viewRoot.findViewById(R.id.switchStack);
        swichAllowCustomCards = viewRoot.findViewById(R.id.switchCustomCards);
        
        switchAdvancedRules.setChecked(preferences.advancedRules());
        swichAllowCustomCards.setChecked(preferences.customCardsAllowed());

        Button btnHelp = viewRoot.findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener((view) -> displayHelpText());

        return viewRoot;
    }

    private void saveSettings() {
        preferences.setAdvancedRulesAllowed(switchAdvancedRules.isChecked());
        preferences.setCustomCardsAllowed(swichAllowCustomCards.isChecked());
    }

    private void displayHelpText(){
        View view = inflater.inflate(R.layout.layout_rules_help, null);

        new AlertDialog.Builder(this.getContext())
                .setTitle(R.string.help_rules)
                .setView(view)
                .setPositiveButton(R.string.ok, (d, id) -> {})
                .create()
                .show();
    }
}
