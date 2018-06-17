package games.winchester.unodeluxe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import at.laubi.network.Network;
import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import butterknife.BindView;
import butterknife.ButterKnife;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.ColorWishDialog;
import games.winchester.unodeluxe.models.Game;
import games.winchester.unodeluxe.models.Player;
import games.winchester.unodeluxe.models.ShakeDetector;
import games.winchester.unodeluxe.utils.NetworkUtils;

import android.widget.Button;
import java.util.Locale;
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import java.util.ArrayList;
import static android.widget.Toast.LENGTH_SHORT;


public class GameActivity extends AppCompatActivity {

    @BindView(R.id.deckView)
    ImageView deckView;

    @BindView(R.id.stack_layout)
    FrameLayout stackLayout;

    @BindView(R.id.handLayout)
    LinearLayout handLayout;

    @BindView(R.id.main_game_layout)
    ConstraintLayout gameLayout;

    @BindView(R.id.ipText)
    TextView ip;

    private Game game;
    private Network network;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // Needed to detect swipe event
    private float oldTouchValue = 0f;
    private float newTouchValue = 0f;
    private static final float MIN_DISTANCE = 50f;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    public int speechResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);



        this.setupNetwork();
        this.setupSensors();
        this.setupGame();
    }

    private void setupGame() {
        String host = getIntent().getStringExtra("host");

        if (host == null) {
            game = new Game(this, new Player("Player1"));
            this.setupMultiplayerHost();
        } else {
            game = new Game(this);
            this.setupMultiplayerClient(host);
        }
    }

    private void setupNetwork() {
        network = new Network();

        network.setFallbackExceptionListener((e, s) -> onFallbackException(e));
        network.setMessageListener((m, s) -> onMessageReceived(m));
        network.setNewSessionListener(this::onNewSession);
        network.setConnectionEndListener(s -> onSessionEnd());
    }

    private void onFallbackException(Exception e) {
        toastUiThread(String.format(getString(R.string.failed_connecting), e.getMessage()));
        Log.e("GameActivity", e.getMessage(), e);
    }

    private void onMessageReceived(Message message) {
        runOnUiThread(() -> game.messageReceived(message));
    }

    private void onNewSession(ClientSession session) {
        runOnUiThread(() -> game.clientConnected(session));
    }

    private void onSessionEnd() {
        runOnUiThread(() -> game.clientDisconnected());
    }

    private void setupSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(c -> handleShakeEvent());
        }
    }

    private void setupMultiplayerHost() {
        final String format = getString(R.string.host_message);
        final List<String> networks = NetworkUtils.getLocalIpAddresses();

        if (networks.isEmpty()) return;

        final String hostIp = networks.get(0);

        network.createHost(hostSession -> {
            game.setSession(hostSession);

            runOnUiThread(() -> {
                deckView.setClickable(true);
                deckView.setOnClickListener(l -> game.deckClicked());
            });

            runOnUiThread(() -> ip.setText(String.format(format, hostIp)));
        }, null);

    }

    private void setupMultiplayerClient(String host) {
        network.createClient(host, clientSession -> {
            game.setSession(clientSession);

            runOnUiThread(() -> {
                deckView.setClickable(true);
                deckView.setOnClickListener(l -> game.deckClicked());
            });

        }, (e, s) -> {
            toastUiThread(getString(R.string.connection_failed));
            Log.e("GameActivity", e.getMessage(), e);
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        });
    }

    public static Drawable getImageDrawable(Context c, String imageName) {
        final int resourceIdentifier = c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());

        return c.getResources().getDrawable(resourceIdentifier);
    }

    // used to keep the stack UI up to date with the backend model
    // used to keep the stack UI up to date with the backend model
    public void updateTopCard(String graphic) {
        LayoutInflater inflater = getLayoutInflater();
        View stackLay = inflater.inflate(R.layout.stack, gameLayout, false);
        ImageView card = stackLay.findViewById(R.id.stackView);
        card.setImageDrawable(getImageDrawable(this, graphic));
        Random rand = new Random();
        float randomNum = rand.nextInt(361);
        card.setRotation(randomNum);
        stackLayout.addView(stackLay);
    }

    // used to keep the hand UI up to date with the backend model
    public void addToHand(List<Card> cards) {

        for (Card c : cards) {
            ImageView cardView = new ImageView(GameActivity.this);
            cardView.setPadding(0, 0, 0, 0);
            cardView.setImageDrawable(getImageDrawable(this, c.getGraphic()));
            cardView.setClickable(true);
            cardView.setTag(c);

            cardView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldTouchValue = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        newTouchValue = event.getY();
                        float deltaY = Math.abs(newTouchValue - oldTouchValue);
                        if (deltaY > MIN_DISTANCE) {
                            // user swiped a card down
                            if(newTouchValue > oldTouchValue) {
                                if(v.getTag() == null) return false;
                                if(game.cheat((Card) v.getTag())) {
                                    handLayout.removeView(v);
                                }
                                return true;
                            }
                        } else if (deltaY <= MIN_DISTANCE) {
                            // user clicked a card
                            if (v.getTag() == null) return false;
                            if (game.cardClicked((Card) v.getTag())) {
                                handLayout.removeView(v);
                                return true;
                            }
                        }
                        break;
                    default:
                        break;
                }

                return false;
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

        final int childCount = handLayout.getChildCount();
        View[] children = new View[childCount];
        for (int i = 0; i < childCount; i++) {
            children[i] = handLayout.getChildAt(i);
        }

        Arrays.sort(children, (a, b) -> {
            Card cardA = (Card) a.getTag();
            Card cardB = (Card) b.getTag();

            return cardA.compareTo(cardB);
        });

        handLayout.removeAllViews();

        for (int i = 0; i < childCount; i++) {
            if (i != 0)
                ((LinearLayout.LayoutParams) children[i].getLayoutParams())
                        .setMargins(-30, 0, 0, 0);
            handLayout.addView(children[i]);
        }

    }

    public void wishAColor(Game game) {
        ColorWishDialog cwd = new ColorWishDialog();
        cwd.setCancelable(false);
        cwd.show(getSupportFragmentManager(), "WishColor", game);
    }

    public void notificationNumberOfCardsToDraw(int i) {
        String text = i == 1 ? getString(R.string.draw_one) : "Du musst noch " + i + " Karten ziehen.";

        this.toastUiThread(text, LENGTH_SHORT);
    }

    public void notificationCardNotPlayable() {
        this.toastUiThread(getString(R.string.card_not_possible), LENGTH_SHORT);
    }

    public void notificationGameWon() {
        this.toastUiThread(getString(R.string.round_won), LENGTH_SHORT);
    }

    public void notificationHasPlayableCard() {
        this.toastUiThread(getString(R.string.cards_playable), LENGTH_SHORT);
    }

    public void notificationNotYourTurn() {
        this.toastUiThread(getString(R.string.not_your_turn), LENGTH_SHORT);
    }

    public void notificationYourTurn() {
        this.vibrate();
        this.toastUiThread(getString(R.string.your_turn), LENGTH_SHORT);
    }

    public void notificationCheated() {
        this.toastUiThread(getString(R.string.cheated), LENGTH_SHORT);
    }

    public void notificationAlreadyCheated() {
        this.toastUiThread(getString(R.string.already_cheated), LENGTH_SHORT);
    }

    public void notificationNotAllowedToCheat() {
        this.toastUiThread(getString(R.string.not_allowed_to_cheat), LENGTH_SHORT);
    }

    public void notificationDeckShuffled() {
        this.toastUiThread(getString(R.string.deck_shuffeled), LENGTH_SHORT);
    }

    public void notificationDrawCardsFirst(int i) {
        this.toastUiThread(String.format(getString(R.string.draw_cards_first), i), LENGTH_SHORT);
    }

    public void vibrate() {
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 400 milliseconds
        v.vibrate(400);
    }


    public void handleShakeEvent() {
        //TODO: for shake action card
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (game != null && game.getSession() != null) {
            game.getSession().close();
        }

        finish();
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


    private void toastUiThread(final String message) {
        toastUiThread(message, Toast.LENGTH_LONG);
    }

    private void toastUiThread(final String message, final int length) {
        this.runOnUiThread(() -> Toast.makeText(GameActivity.this, message, length).show());
    }


    public void SpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.say_something) );
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                     if(result.get(0).equals("UNO")||
                             result.get(0).equals("UN")||
                             result.get(0).equals("U")||
                             result.get(0).equals("uno")||
                             result.get(0).equals("un")||
                    result.get(0).equals("u"))   {
                         speechResult=1;

                     } else
                     {
                         speechResult=0;
                     }

                }
                break;
            }
        }
    }
}
