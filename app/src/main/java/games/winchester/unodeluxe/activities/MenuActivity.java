package games.winchester.unodeluxe.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_options, null);

        builder
                .setTitle("Hausregeln konfigurieren")
                .setView(view)
                .setPositiveButton("Speichern", (d, id) -> {
                    Switch s = view.findViewById(R.id.switchStack);
                    Log.e("Switch", s.isChecked() + "");
                })
                .setNegativeButton("Abbrechen", (d, id) -> {});

        Dialog dialog = builder.create();
        dialog.show();
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
