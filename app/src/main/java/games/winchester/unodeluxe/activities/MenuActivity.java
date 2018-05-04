package games.winchester.unodeluxe.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import at.laubi.network.session.ClientSession;
import at.laubi.network.session.Session;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.activities.MultiplayerActivity;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.ipTextView)
    TextView ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStartGame)
    void onBtnStartGameClick() {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        Bundle b = new Bundle();
        // we put a the client session to game if game is joined.
        // in case of creating a game nothing is put to intent
        b.putString("host", null); //Your id
        intent.putExtras(b); //Put your id to your next Intent

        startActivity(intent);
        finish();

    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        Bundle b = new Bundle();
        String host = ip.getText().toString();
        if(0 < host.length()){
            // we put a the client session to game if game is joined.
            // in case of creating a game nothing is put to intent
            b.putString("host", host); //Your id
            intent.putExtras(b); //Put your id to your next Intent

            startActivity(intent);
            finish();
        }
    }
}
