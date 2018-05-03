package games.winchester.unodeluxe.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.laubi.network.session.ClientSession;
import at.laubi.network.session.Session;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.activities.GameActivity;
import games.winchester.unodeluxe.activities.MultiplayerActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnGame)
    void onGameButtonClick() {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        Bundle b = new Bundle();
        // we put a the client session to game if game is joined.
        // in case of creating a game nothing is put to intent
        b.putString("host", null); //Your id
        intent.putExtras(b); //Put your id to your next Intent

        startActivity(intent);
        finish();

    }

    @OnClick(R.id.btnMultiplayer)
    void onMultiplayerButtonClick(){
        startActivity(MultiplayerActivity.class);
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}
