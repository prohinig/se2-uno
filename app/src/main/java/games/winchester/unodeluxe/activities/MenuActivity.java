package games.winchester.unodeluxe.activities;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.R;

public class MenuActivity extends AppCompatActivity {
    int ok=0;
    MediaPlayer player;
    @BindView(R.id.ipTextView)
    TextView ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);
        
         try {
            this.ok=1;
            AssetFileDescriptor afd = getAssets().openFd("The Sims 2 - Complete Soundtrack.mp3");
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
            // Set the looping and play the music.
            player.setLooping(true);
            player.prepare();
            player.setLooping(true);
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void goToActivity2 (View view){
       Intent intent = new Intent (this, MultiplayerActivity.class);
       startActivity(intent);
    }
    public void goToTop (View view){
       Intent intent = new Intent (this, Top.class);
       startActivity(intent);
    }
    public void goToRules (View view){
      Intent intent = new Intent (this, Rules.class);
      startActivity(intent);
    }
    public void goToMusic (View view){
       if (ok==1) {
          player.stop();
          this.ok=0;
      }else {
           try {
               AssetFileDescriptor afd = getAssets().openFd("The Sims 2 - Complete Soundtrack.mp3");
               player = new MediaPlayer();
               player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
               // Set the looping and play the music.
               player.setLooping(true);
               player.prepare();
               player.setLooping(true);
               player.start();
               this.ok = 1;
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
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
