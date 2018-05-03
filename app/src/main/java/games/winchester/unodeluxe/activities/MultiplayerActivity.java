package games.winchester.unodeluxe.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import at.laubi.network.Network;
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

        network = new Network();
        network.setFallbackExceptionListener((e, s) -> {
            toastUiThread("Exception thrown: " + e.getMessage());
            e.printStackTrace();
        });
        network.setMessageListener((m, s) -> toastUiThread(m.toString()));

        network.setNewSessionListener(s -> toastUiThread("New client connected"));

        network.setConnectionEndListener(s -> toastUiThread("Client disconnected"));
    }

    @OnClick(R.id.btnCreateServer)
    void onHostClick(){
        network.createHost(hostSession -> {
            final MultiplayerActivity that = MultiplayerActivity.this;

            that.session = hostSession;

            that.runOnUiThread(() -> {
                that.btnConnect.setEnabled(false);
                that.ip.setText( Objects.requireNonNull(NetworkUtils.getLocalIpAddresses().get(1)));
                that.btnSend.setEnabled(true);
                that.etIp.setEnabled(false);
            });
        }, null);
    }

    @OnClick(R.id.btnConnect)
    void onBtnConnect() {
        String ip = etIp.getText().toString();

        network.createClient(ip, clientSession -> {
            final MultiplayerActivity that = MultiplayerActivity.this;

            that.session = clientSession;

            that.runOnUiThread(() -> {
                that.btnConnect.setEnabled(false);
                that.btnSend.setEnabled(true);
                that.etIp.setEnabled(false);
                that.btnHost.setEnabled(false);
            });
        }, null);
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
