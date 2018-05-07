package games.winchester.unodeluxe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import at.laubi.network.Network;
import at.laubi.network.messages.Message;
import at.laubi.network.session.Session;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.ColorWishDialog;
import games.winchester.unodeluxe.models.Game;
import games.winchester.unodeluxe.models.Player;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.models.ShakeDetector;
import games.winchester.unodeluxe.app.UnoDeluxe;
import games.winchester.unodeluxe.utils.GameLogic;
import games.winchester.unodeluxe.utils.NetworkUtils;

public class GameActivity extends AppCompatActivity {

    private ImageView deckView, stackView, handCard;
    private LinearLayout handLayout;
    private Player self;
    private Game game;
    private Network network;
    private Session session;


    @BindView(R.id.ipText)
    TextView ip;

    private boolean clicksEnabled = true;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        String host = null; // or other values
        // or other values
        if(null != b) {
            host = b.getString("host");
        }

        //String text = "Host: " + host;
        //int duration = Toast.LENGTH_SHORT;
        //Toast toast = Toast.makeText(UnoDeluxe.getContext(), text, duration);
        //toast.show();

        deckView = (ImageView) findViewById(R.id.deckView);
        stackView = (ImageView) findViewById(R.id.stackView);
        handCard = (ImageView) findViewById(R.id.handCard);
        handLayout = (LinearLayout) findViewById(R.id.handLayout);

        handLayout.removeView(handCard);
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

        game =  new Game(this);
        network = new Network();
        network.setFallbackExceptionListener((e, s) -> {
            toastUiThread("Exception thrown: " + e.getMessage());
            e.printStackTrace();
        });

        network.setMessageListener((m, s) -> gameMessageReceived(m));
        network.setNewSessionListener((s) -> gameClientConnected());
        network.setConnectionEndListener((s) -> gameClientDisconnected());


        if(null == host){
            network.createHost(hostSession -> {
                final GameActivity that = GameActivity.this;

                that.session = hostSession;

                // this is what happens when a player creates a game
                // it will be different for joining a game
                that.game.setSession(hostSession);
                that.self = that.game.getSelf();

                that.deckView.setClickable(true);
                that.deckView.setOnClickListener(v -> {
                    if (!game.isGameStarted()) {
                        game.startGame();
                        stackView.setVisibility(View.VISIBLE);
                    } else {
                        if (game.getNumberOfCardsToDraw() != 0) {
                            game.handCards(1, null);
                            game.decrementNumberOfCardsToDraw();
                        } else {
                            if (!GameLogic.hasPlayableCard(self.getHand(), game.getActiveColor(), game.getTopOfStackCard())) {
                                ArrayList<Card> tmp = game.handCards(1, null);

                                if (GameLogic.isPlayableCard(tmp.get(0), self.getHand(), game.getTopOfStackCard(), game.getActiveColor())) {
                                    //TODO: player is allowed to play drawn card if its playable
                                }

                            } else {
                                notificationHasPlayableCard();
                            }
                        }
                    }
                });
                that.runOnUiThread(() -> {
//                    that.btnConnect.setEnabled(false);
                    that.ip.setText( "I am: " + Objects.requireNonNull(NetworkUtils.getLocalIpAddresses().get(0)));
//                    that.btnSend.setEnabled(true);
//                    that.etIp.setEnabled(false);
                });
            }, null);



//            deckView.setClickable(true);
//            deckView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    deckViewClick();
//                }
//            });

            // ShakeDetector initialization
        }
        else {
            network.createClient(host, clientSession -> {
                final GameActivity that = GameActivity.this;

                that.session = clientSession;

                // this is what happens when a player creates a game
                // it will be different for joining a game
                that.game.setSession(clientSession);
                that.self = that.game.getSelf();


                that.runOnUiThread(() -> {
//                    that.btnConnect.setEnabled(false);
//                    that.btnSend.setEnabled(true);
//                    that.etIp.setEnabled(false);
//                    that.btnHost.setEnabled(false);
                });
            }, (e, s) -> {
                toastUiThread("Verbindung zum Spiel fehlgeschlagen.");
                e.printStackTrace();
                Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                startActivity(intent);
            });

            // this is what happens when a player creates a game

//             try to connect to host and if succesful create game with connection
        }

    }

//    void deckViewClick() {
//        if(clicksEnabled) {
//            game.deckClicked();
//            game.startGame();
//            stackView.setVisibility(View.VISIBLE);
//        }
//    }

    public static Drawable getImageDrawable(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
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

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != v.getTag()) {
                        Card c = (Card) v.getTag();
                        boolean result = game.handleTurn(c, self);
                        if (true == result) {
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

    public void wishAColor(Game game) {
        ColorWishDialog cwd = new ColorWishDialog();
        cwd.setCancelable(false);
        cwd.show(getSupportFragmentManager(), "WishColor", game);
    }

    public void notificationNumberOfCardsToDraw(int i) {
        Context context = getApplicationContext();
        CharSequence text = "Du musst noch " + i + " Karten ziehen.";
        if (i == 1) {
            text = "Du musst noch eine Karte ziehen.";
        }
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void notificationCardNotPlayable() {
        Context context = getApplicationContext();
        CharSequence text = "Du darfst diese Karte jetzt nicht spielen.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void notificationGameWon() {
        Context context = getApplicationContext();
        CharSequence text = "Glückwunsch! Du hast diese Runde gewonnen!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void notificationHasPlayableCard() {
        Context context = getApplicationContext();
        CharSequence text = "Du hast noch spielbare Karten.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void handleShakeEvent(int count) {
        this.game.stackToDeck();
        Context context = getApplicationContext();
        CharSequence text = "Deck wurde gemischt.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setClicksEnabled(boolean clicksEnabled) {
        this.clicksEnabled = clicksEnabled;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(session != null) session.close();
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

    public ImageView getHandCardView() {
        return handCard;
    }

    private void toastUiThread(final String message){
        this.runOnUiThread(() -> Toast.makeText(GameActivity.this, message, Toast.LENGTH_LONG).show());
    }

    private void gameMessageReceived(Message m){
        this.runOnUiThread(() -> this.game.messageReceived(m));
    }

    private void gameClientConnected(){
        this.runOnUiThread(() -> this.game.clientConnected());
    }

    private void gameClientDisconnected(){
        this.runOnUiThread(() -> this.game.clientDisconnected());
    }
}
