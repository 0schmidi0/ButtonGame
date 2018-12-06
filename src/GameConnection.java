import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GameConnection {
    private boolean running = false;
    private Socket connection;

    private DataInputStream receive;
    private DataOutputStream send;

    private ArrayList<ActionListener> listeners = new ArrayList<>();

    public GameConnection(Socket player) {
        this.connection = player;
    }

    public void start() {
        this.running = true;


        try {
            this.receive = new DataInputStream(this.connection.getInputStream());
            this.send = new DataOutputStream(this.connection.getOutputStream());
        } catch (Exception e) {

        }


        Thread t = new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        String message = receive.readUTF();
                        notifyListeners(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public void send(String message) {
        if (this.send != null) {
            try {
                send.writeUTF(message);
                send.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.running = false;
    }

    public void notifyListeners(String message) {
        for (ActionListener l : listeners) {
            l.actionPerformed(new ActionEvent(this, 0, message));
        }
    }
    public void addActionListener (ActionListener l){
        this.listeners.add(l);
    }
}
