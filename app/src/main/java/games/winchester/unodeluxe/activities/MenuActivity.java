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
        startGameActivity(null);
    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {
        final String host = ip.getText().toString();

        if(!host.isEmpty()){
            startGameActivity(host);
        }
    }

    private void startGameActivity(String host) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("host", host);
        startActivity(intent);

        finish();
    }
}
