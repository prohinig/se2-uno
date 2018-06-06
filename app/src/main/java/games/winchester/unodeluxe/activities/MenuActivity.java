package games.winchester.unodeluxe.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.app.Preferences;
import games.winchester.unodeluxe.app.UnoDeluxe;
import games.winchester.unodeluxe.dialog.SettingsDialog;

public class MenuActivity extends AppCompatActivity {
    @BindView(R.id.etIP)
    TextView ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStartGame)
    void onBtnStartGameClick() {
        startGameActivity(null);
    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {
        final String host = ip.getText().toString();

        if(!host.isEmpty()){
            startGameActivity(host);
        }
    }

    @OnClick(R.id.btnHouseRules)
    void onBtnHouseRulesClick(){
        new SettingsDialog(this).create().show();
    }

    @OnClick(R.id.btnTutorial)
    void onBtnTutorialClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_rules, null);

        builder
                .setTitle("Spielverlauf")
                .setView(view)
                .setPositiveButton("OK", (d, id) -> {});

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void startGameActivity(String host) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("host", host);
        startActivity(intent);
    }
}
