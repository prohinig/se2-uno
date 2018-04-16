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

public class MainActivity extends AppCompatActivity {

    ImageView deck, stack, handCard;
    Deck cardDeck;
    Stack cardStack;
    LinearLayout handLayout;
    static ArrayList<Card> hand;


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
        handCard = (ImageView) findViewById(R.id.handCard);
        handLayout = (LinearLayout) findViewById(R.id.handLayout);

        stack.setVisibility(View.INVISIBLE);
        handLayout.removeView(handCard);
        hand = new ArrayList<Card>();

        deck.setClickable(true);
        deck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Deck size: " + cardDeck.getSize(),
                        Toast.LENGTH_SHORT).show();

                if(1 > cardStack.getSize()) {
                    stack.setVisibility(View.VISIBLE);
                    ArrayList<Card> cards = cardDeck.deal(1);
                    Card card = cards.get(0);

                    cardStack.playCard(card);
                    stack.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), card.getGraphic()));
                }

                ArrayList<Card> drawn = cardDeck.deal(3);
                hand.addAll(drawn);

                for (Card c : drawn) {
                    ImageView cardView = new ImageView(MainActivity.this);
                    cardView.setPadding(0, 0, 0, 0);
                    cardView.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), c.getGraphic()));
                    cardView.setClickable(true);
                    cardView.setTag(c);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(null != v.getTag()){
                                handLayout.removeView(v);
                                Card c = (Card) v.getTag();
                                hand.remove(c);
                                cardStack.playCard(c);
                                stack.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), c.getGraphic()));
                            }
                        }
                    });

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(125, 195);
                    layoutParams.setMargins(0, 0, -25, 0);
                    cardView.setLayoutParams(layoutParams);

                    handLayout.addView(cardView);
                }
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
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
