package games.winchester.unodeluxe.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.laubi.network.MessageSendListener;
import at.laubi.network.Network.FallbackCallbacks;
import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.messages.SimpleStringMessage;
import at.laubi.network.ClientSession;
import at.laubi.network.HostSession;
import at.laubi.network.Network;
import at.laubi.network.Session;
import games.winchester.unodeluxe.utils.NetworkUtils;

public class MultiplayerActivity extends AppCompatActivity {
    TextView ip;
    Button btnConnect, btnHost, btnSend;
    EditText etIp, etMessage;

    private Network network;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        this.ip = findViewById(R.id.tvIp);
        this.btnConnect = findViewById(R.id.btnConnect);
        this.btnHost = findViewById(R.id.btnCreateServer);
        this.etIp = findViewById(R.id.inputIp);
        this.etMessage = findViewById(R.id.editText);
        this.btnSend = findViewById(R.id.btnSendMessage);

        this.btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHostClick();
            }
        });
        this.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnConnect();
            }
        });
        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnSend();
            }
        });

        this.btnSend.setEnabled(false);

        Network.Options options = new Network.Options();
        options.callbacks = new FallbackCallbacks() {
            @Override
            public void onException(Exception e, Session s) {
                toastUiThread(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onMessageReceived(Message message) {
                toastUiThread(message.toString());
            }
        };

        network = new Network(options);
    }

    private void onHostClick(){
        network.createHost(new Network.SessionCreationListener<HostSession>() {
            @Override
            public void onSuccess(HostSession session) {
                final MultiplayerActivity that = MultiplayerActivity.this;

                that.session = session;

                that.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        that.btnConnect.setEnabled(false);
                        that.ip.setText( NetworkUtils.getLocalIpAddress());
                        that.btnSend.setEnabled(true);
                        that.etIp.setEnabled(false);
                    }
                });
            }

            @Override
            public void onException(Exception e) {
                toastUiThread(e.getMessage());
            }
        });
    }

    private void onBtnConnect(){
        String text = etIp.getText().toString();

        network.createClient(text, new Network.SessionCreationListener<ClientSession>() {
            @Override
            public void onSuccess(ClientSession session) {
                final MultiplayerActivity that = MultiplayerActivity.this;

                that.session = session;

                that.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        that.btnConnect.setEnabled(false);
                        that.btnSend.setEnabled(true);
                        that.etIp.setEnabled(false);
                        that.btnHost.setEnabled(false);
                    }
                });
            }

            @Override
            public void onException(Exception e) {
                toastUiThread(e.getMessage());
            }
        });
    }

    private void onBtnSend(){
        session.send(new SimpleStringMessage(this.etMessage.getText().toString()), new MessageSendListener() {
            @Override
            public void onException(Exception e) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(session != null) session.close();
    }

    private void toastUiThread(final String message){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MultiplayerActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
