package games.winchester.unodeluxe.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.network.Server;

public class MultiplayerActivity extends AppCompatActivity {

    TextView ip;
    Button btnConnect, btnHost;
    EditText ipInput;

    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        this.ip = findViewById(R.id.tvIp);
        this.btnConnect = findViewById(R.id.btnConnect);
        this.btnHost = findViewById(R.id.btnCreateServer);
        this.ipInput = findViewById(R.id.inputIp);

        this.btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHostClick();
            }
        });
    }

    private void onHostClick(){
        try {
            this.server = new Server(10001);
            this.ip.setText(getLocalIpAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    System.out.println("ip1--:" + inetAddress);
                    System.out.println("ip2--:" + inetAddress.getHostAddress());

                    // for getting IPV4 format
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {

                        String ip = inetAddress.getHostAddress().toString();
                        System.out.println("ip---::" + ip);


                        return ip;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }


}
