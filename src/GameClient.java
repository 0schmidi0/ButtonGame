import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GameClient {

    private ArrayList<ActionListener> listeners = new ArrayList<>();
    private GameConnection player;

    public GameClient(String host, int port) {
        try {
            Socket client = new Socket(host, port);

            player = new GameConnection(client);
            player.start();

            for (ActionListener l : listeners) {
                player.addActionListener(l);
            }
        } catch (IOException e) {
        }
    }

    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void sendMessage(String msg) {
        player.send(msg);
    }

}
