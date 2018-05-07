package games.winchester.unodeluxe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.laubi.network.Network;
import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.Session;
import butterknife.BindView;
import butterknife.ButterKnife;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.app.UnoDeluxe;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.ColorWishDialog;
import games.winchester.unodeluxe.models.Game;
import games.winchester.unodeluxe.models.Player;
import games.winchester.unodeluxe.models.ShakeDetector;
import games.winchester.unodeluxe.utils.NetworkUtils;

public class GameActivity extends AppCompatActivity {

    @BindView(R.id.deckView)
    ImageView deckView;

    @BindView(R.id.stackView)
    ImageView stackView;

    @BindView(R.id.handLayout)
    LinearLayout handLayout;

    @BindView(R.id.ipText)
    TextView ip;

    private Player self;
    private Game game;
    private Network network;
    private Session session;

    private boolean clicksEnabled = true;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        this.setupNetwork();
        this.setupSensors();

        String host = getIntent().getStringExtra("host");

        if (host == null) {
            this.setupMultiplayerHost();
        } else {
            this.setupMultiplayerClient(host);
        }
    }

    private void setupNetwork() {
        network = new Network();

        network.setFallbackExceptionListener(this::onFallbackException);
        network.setMessageListener(this::onMessageReceived);
        network.setNewSessionListener(this::onNewSession);
        network.setConnectionEndListener(this::onSessionEnd);
    }

    private void onFallbackException(Exception e, Session s) {
        toastUiThread("Exception thrown: " + e.getMessage());
        e.printStackTrace();
    }

    private void onMessageReceived(Message message, Session session) {
        runOnUiThread(() -> game.messageReceived(message));
    }

    private void onNewSession(ClientSession session) {
        runOnUiThread(() -> game.clientConnected());
    }

    private void onSessionEnd(Session session) {
        runOnUiThread(() -> game.clientDisconnected());
    }

    private void setupSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(this::handleShakeEvent);
    }

    private void setupMultiplayerHost() {
        final String format = "I am: %s";
        final List<String> networks = NetworkUtils.getLocalIpAddresses();

        if(networks.isEmpty()) return;

        final String hostIp = networks.get(0);

        network.createHost(hostSession -> {
            session = hostSession;
            game.setSession(hostSession);
            self = game.getSelf();

            deckView.setClickable(true);
            deckView.setOnClickListener(l -> game.deckClicked());

            runOnUiThread(() -> {
                ip.setText(String.format(format, hostIp));
            });
        }, null);

        game = new Game(this);

        deckView.setClickable(true);
    }

    private void setupMultiplayerClient(String host) {
        network.createClient(host, clientSession -> {
            session = clientSession;

            game.setSession(clientSession);
            self = game.getSelf();

            deckView.setClickable(true);
            deckView.setOnClickListener(l -> game.deckClicked());

        }, (e, s) -> {
            toastUiThread("Verbindung zum Spiel fehlgeschlagen.");
            e.printStackTrace();
            startActivity(new Intent(this, MenuActivity.class));
        });
    }

    public static Drawable getImageDrawable(Context c, String ImageName) {
        final int resourceIdentifier = c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName());

        return c.getResources().getDrawable(resourceIdentifier);
    }

    // used to keep the stack UI up to date with the backend model
    public void updateTopCard(String graphic) {
        this.stackView.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), graphic));
    }

    // used to keep the hand UI up to date with the backend model
    public void addToHand(ArrayList<Card> cards) {
        for (Card c : cards) {
            ImageView cardView = new ImageView(GameActivity.this);
            cardView.setPadding(0, 0, 0, 0);
            cardView.setImageDrawable(getImageDrawable(UnoDeluxe.getContext(), c.getGraphic()));
            cardView.setClickable(true);
            cardView.setTag(c);

            cardView.setOnClickListener(v -> {
                if(v.getTag() == null) return;

                Card c1 = (Card) v.getTag();
                boolean result = game.handleTurn(c1, self);
                if (result) {
                    handLayout.removeView(v);
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

    public void wishAColor(Game game) {
        ColorWishDialog cwd = new ColorWishDialog();
        cwd.setCancelable(false);
        cwd.show(getSupportFragmentManager(), "WishColor", game);
    }

    public void notificationNumberOfCardsToDraw(int i) {
        String text = i == 1 ? "Du musst noch eine Karte ziehen." : "Du musst noch " + i + " Karten ziehen.";

        this.toastUiThread(text, Toast.LENGTH_SHORT);
    }

    public void notificationCardNotPlayable() {
        this.toastUiThread("Du darfst diese Karte jetzt nicht spielen.", Toast.LENGTH_SHORT);
    }

    public void notificationGameWon() {
        this.toastUiThread("Glückwunsch! Du hast diese Runde gewonnen!", Toast.LENGTH_SHORT);
    }

    public void notificationHasPlayableCard() {
        this.toastUiThread("Du hast noch spielbare Karten.", Toast.LENGTH_SHORT);
    }

    public void handleShakeEvent(int count) {
        this.game.stackToDeck();

        this.toastUiThread("Deck wurde gemischt", Toast.LENGTH_SHORT);
    }

    public void setClicksEnabled(boolean clicksEnabled) {
        this.clicksEnabled = clicksEnabled;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (session != null) session.close();
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
}
