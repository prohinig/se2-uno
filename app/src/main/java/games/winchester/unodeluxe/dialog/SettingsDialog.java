package games.winchester.unodeluxe.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.app.Preferences;

public class SettingsDialog extends AlertDialog.Builder {

    @BindView(R.id.switchStack)
    Switch switchAdvancedRules;

    @BindView(R.id.switchCustomCards)
    Switch swichAllowCustomCards;

    @BindView(R.id.switchCheating)
    Switch switchAllowCheating;

    private Preferences preferences;

    public SettingsDialog(Context context) {
        super(context);

        preferences = Preferences.from(context);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_options, null);
        ButterKnife.bind(this, view);
        initializeViews();

        setTitle(R.string.configure_houserules);
        setView(view);
        setPositiveButton(R.string.save, (d, id) -> saveSettings());
        setNegativeButton(R.string.cancel, (d, id) -> {});
    }

    private void initializeViews(){
        switchAdvancedRules.setChecked(preferences.advancedRules());
        swichAllowCustomCards.setChecked(preferences.customCardsAllowed());
        switchAllowCheating.setChecked(preferences.isCheatingAllowed());
    }

    private void saveSettings() {
        preferences.setAdvancedRulesAllowed(switchAdvancedRules.isChecked());
        preferences.setCustomCardsAllowed(swichAllowCustomCards.isChecked());
        preferences.setAllowCheating(switchAllowCheating.isChecked());
    }

    @OnClick(R.id.btnHelp)
    void displayHelpText(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_rules_help, null);

        new AlertDialog.Builder(this.getContext())
                .setTitle(R.string.help_rules)
                .setView(view)
                .setPositiveButton(R.string.ok, (d, id) -> {})
                .create()
                .show();
    }
}
