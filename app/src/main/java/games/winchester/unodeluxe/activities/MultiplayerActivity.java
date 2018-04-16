package games.winchester.unodeluxe.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.laubi.network.CreationListener;
import at.laubi.network.MessageSendListener;
import at.laubi.network.Network;
import at.laubi.network.NetworkCallbacks;
import at.laubi.network.NetworkOptions;
import at.laubi.network.exceptions.NetworkException;
import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.messages.SimpleStringMessage;
import games.winchester.unodeluxe.utils.NetworkUtils;

public class MultiplayerActivity extends AppCompatActivity {
    private Network network;
    private Session session;

    @BindView(R.id.tvIp)
    TextView ip;

    @BindView(R.id.btnConnect)
    Button btnConnect;

    @BindView(R.id.btnCreateServer)
    Button btnHost;

    @BindView(R.id.btnSendMessage)
    Button btnSend;

    @BindView(R.id.inputIp)
    EditText etIp;

    @BindView(R.id.editText)
    EditText etMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        ButterKnife.bind(this);

        this.btnSend.setEnabled(false);

        NetworkCallbacks callbacks = new NetworkCallbacks() {
            @Override
            public void onExceptionFallback(Exception e, Session s) {
                toastUiThread(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onReceivedMessage(Message message, Session session) {
                toastUiThread(message.toString());
            }
        };

        network = new Network(null, callbacks);
    }

    @OnClick(R.id.btnCreateServer)
    void onHostClick(){
        network.createHost(new CreationListener<HostSession>() {
            @Override
            public void onSuccess(HostSession session) {
                final MultiplayerActivity that = MultiplayerActivity.this;

                that.session = session;

                that.runOnUiThread(() -> {
                    that.btnConnect.setEnabled(false);
                    that.ip.setText( NetworkUtils.getLocalIpAddress());
                    that.btnSend.setEnabled(true);
                    that.etIp.setEnabled(false);
                });
            }

            @Override
            public void onException(Exception e) {
                toastUiThread(e.getMessage());
            }
        });
    }

    @OnClick(R.id.btnConnect)
    void onBtnConnect() {
        String text = etIp.getText().toString();

        network.createClient(text, new CreationListener<ClientSession>() {
            @Override
            public void onSuccess(ClientSession session) {
                final MultiplayerActivity that = MultiplayerActivity.this;

                that.session = session;

                that.runOnUiThread(() -> {
                    that.btnConnect.setEnabled(false);
                    that.btnSend.setEnabled(true);
                    that.etIp.setEnabled(false);
                    that.btnHost.setEnabled(false);
                });
            }

            @Override
            public void onException(Exception e) {
                toastUiThread(e.getMessage());
            }
        });
    }

    @OnClick(R.id.btnSendMessage)
    void onBtnSend(){
        session.send(new SimpleStringMessage(this.etMessage.getText().toString()));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(session != null) session.close();
    }

    private void toastUiThread(final String message){
        this.runOnUiThread(() -> Toast.makeText(MultiplayerActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
