package games.winchester.unodeluxe;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView deck, stack;
    Deck cardDeck;
    Stack cardStack;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardDeck = new Deck();
        cardStack = new Stack();

        deck = (ImageView) findViewById(R.id.deckView);
        stack = (ImageView) findViewById(R.id.stackView);

        stack.setVisibility(View.INVISIBLE);

        deck.setClickable(true);
        deck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack.setVisibility(View.VISIBLE);

                ArrayList<Card> cards = cardDeck.deal(1);
                Card card = cards.get(0);

                cardStack.playCard(card);

                Toast.makeText(MainActivity.this,
                        "Drawn card \n" +
                        "color: " + card.getColor() +
                        "\nsymbol: " + card.getSymbol(),
                        Toast.LENGTH_LONG).show();
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

    public void handleShakeEvent(int count) {
        cardDeck.shuffle();
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
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
