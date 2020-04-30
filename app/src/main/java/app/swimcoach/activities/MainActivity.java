package app.swimcoach.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Iterator;

import app.swimcoach.R;
import app.swimcoach.network.IOClient;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    IOClient client = IOClient.instance;
    private Activity activity = MainActivity.this;
    private TextView mServerMsgDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServerMsgDisplay = findViewById(R.id.server_message);

        client.addListener(Socket.EVENT_CONNECT, new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                getApplicationContext(),
                                "Connection successful",
                                Toast.LENGTH_SHORT).show();
                        JSONObject data = new JSONObject();
                        try {
                            client.send(data);
                        } catch (SocketException e) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Internal socket.io error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        client.addListener(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(
                                getApplicationContext(),
                                "Server connection error",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        client.addListener(IOClient.EVENT_SERVER_DATA, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject)args[0];
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<String> keys = data.keys();
                        StringBuilder sb = new StringBuilder();
                        sb.append("Data from server:\n{\n");
                        while(keys.hasNext()){
                            sb.append("\t");
                            String key = keys.next();
                            sb.append(key);
                            sb.append(": ");
                            try {
                                sb.append(data.get(key));
                            } catch (JSONException e) {
                                sb.append("INVALID");
                            }
                            sb.append("\n");
                        }
                        sb.append("}");

                        mServerMsgDisplay.setText(sb.toString());
                    }
                });
            }
        });
        client.addListener(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                getApplicationContext(),
                                "Disconnected from server",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        try {
            client.connect();
        } catch (URISyntaxException e) {
            Toast.makeText(
                    getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        System.out.println("Finished OnCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(client.isConnected()){
            client.disconnect();
        }
    }
}
