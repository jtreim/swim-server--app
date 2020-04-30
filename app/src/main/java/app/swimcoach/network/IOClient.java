package app.swimcoach.network;

import android.util.Pair;

import org.json.JSONObject;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;

public class IOClient implements Client {
    private Socket socket;
    private static final String SERVER = "http://192.168.1.7:8080";
    public static final String EVENT_SERVER_DATA = "server_data";
    public static final String EVENT_CLIENT_DATA = "client_data";

    private ArrayList<Pair<String, Listener>> eventListeners;

    private IOClient(){
        this.eventListeners = new ArrayList<>();
    }

    public static final IOClient instance = new IOClient();

    @Override
    public boolean isConnected() {
        return this.socket == null;
    }

    @Override
    public void connect() throws URISyntaxException {
        this.socket = IO.socket(SERVER);
        for(Pair<String, Listener> eventListener : this.eventListeners){
            socket.on(eventListener.first, eventListener.second);
        }
        this.socket.connect();
    }

    @Override
    public void connect(IO.Options options) throws URISyntaxException {
        this.socket = IO.socket(SERVER, options);
        for(Pair<String, Listener> eventListener : this.eventListeners){
            socket.on(eventListener.first, eventListener.second);
        }
        this.socket.connect();
    }

    @Override
    public void send(String event, JSONObject data) throws SocketException {
        if(this.socket == null){
            throw new SocketException("Unable to send data; socket not connected");
        }

        this.socket.emit(event, data);
    }

    @Override
    public void send(JSONObject data) throws SocketException {
        this.send(IOClient.EVENT_CLIENT_DATA, data);
    }

    @Override
    public void addListener(String event, Listener listener) {
        Pair<String, Listener> l = new Pair<>(event, listener);
        this.eventListeners.add(l);
    }

    @Override
    public void disconnect() {
        this.socket.disconnect();
        this.socket = null;
    }
}
