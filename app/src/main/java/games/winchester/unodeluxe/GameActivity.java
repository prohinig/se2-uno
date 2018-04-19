package games.winchester.unodeluxe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    ImageView deckView, stackView, handCard;
    LinearLayout handLayout;
    Player self;
    Game game;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this is what happens when a player creates a game
        // it will be different for joining a game
        self = new Player("admin", Player.TYPE_ADMIN);
        game = new Game(self, this);

        deckView = (ImageView) findViewById(R.id.deckView);
        stackView = (ImageView) findViewById(R.id.stackView);
        handCard = (ImageView) findViewById(R.id.handCard);
        handLayout = (LinearLayout) findViewById(R.id.handLayout);

        stackView.setVisibility(View.INVISIBLE);
        handLayout.removeView(handCard);

        deckView.setClickable(true);
        deckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.startGame();
                stackView.setVisibility(View.VISIBLE);
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                handleShakeEvent(count);
            }
        });

    }

    public static Drawable getImageDrawable(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }

    // used to keep the stack UI up to date with the backend model
    public void updateTopCard (String graphic) {
        this.stackView.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), graphic));
    }

    // used to keep the hand UI up to date with the backend model
    public void addToHand (ArrayList<Card> cards) {
        for (Card c : cards) {
            ImageView cardView = new ImageView(GameActivity.this);
            cardView.setPadding(0, 0, 0, 0);
            cardView.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), c.getGraphic()));
            cardView.setClickable(true);
            cardView.setTag(c);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != v.getTag()) {
                        Card c = (Card) v.getTag();
                        boolean result = game.playCard(c, self);
                        if(true == result) {
                            handLayout.removeView(v);
                        }
                    }
                }
            });


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(125, 195);
            int marginLeft = handLayout.getChildCount() == 0 ? 0 : -30;

            // TODO for bigger displays check density and render accordingly
            // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(188, 293);
            // int marginLeft = handLayout.getChildCount() == 0 ? 0 : -60;

            layoutParams.setMargins(marginLeft, 0, 0, 0);

            cardView.setLayoutParams(layoutParams);
            handLayout.addView(cardView);
        }
    }

    public void handleShakeEvent(int count) {
        this.game.stackToDeck();
        Context context = getApplicationContext();
        CharSequence text = "Karten wurden gemischt.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
