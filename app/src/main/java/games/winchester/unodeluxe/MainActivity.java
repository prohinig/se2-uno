package games.winchester.unodeluxe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import games.winchester.unodeluxe.activities.MultiplayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnServer).setOnClickListener(view -> startMultiplayerActivity());

    }
    private void startMultiplayerActivity(){
        Intent intent = new Intent(this, MultiplayerActivity.class);
        this.startActivity(intent);
    }
}
