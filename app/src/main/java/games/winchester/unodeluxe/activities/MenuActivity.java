package games.winchester.unodeluxe.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        startActivity(GameActivity.class);
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
