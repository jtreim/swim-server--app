package app.swimcoach.network;

import org.json.JSONObject;

import java.net.SocketException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter.Listener;

public interface Client {
    boolean isConnected();
    void connect() throws URISyntaxException;
    void connect(IO.Options options) throws URISyntaxException;
    void send(String event, JSONObject data) throws SocketException;
    void send(JSONObject data) throws SocketException;
    void addListener(String event, Listener listener);
    void disconnect();
}
