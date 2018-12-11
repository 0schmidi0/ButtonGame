import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer {
    private int port;
    private boolean running = false;

    private int readyCnt = 0;
    private ArrayList <GameConnection> clients = new ArrayList<>();

    public GameServer(int port) {
        this.port = port;
    }

    private ActionListener broadcastListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("READY!")) {
                ++readyCnt;
                System.out.println("con");

                if (readyCnt >= clients.size()) {
                    //StartTimer();
                    System.out.println("Start");

                }
            } else if (e.getActionCommand().equals("done")) {
                // e.getSource() --> sender vom done
                // sende an alle außer dem sender "you lose"

                // sende an den sender "you win"
            }
        }
    };

    public void start() {
        this.running = true;

        Thread serverThread = new Thread() {

            @Override
            public void run() {
                try (ServerSocket server = new ServerSocket(GameServer.this.port)) {
                    while (running) {
                        Socket client = server.accept();
                        GameConnection player = new GameConnection(client);

                        player.addActionListener(broadcastListener);
                        clients.add(player);
                        player.start();
                    }
                } catch (Exception e) {

                }
            }
        };
        serverThread.start();
    }

    public void stop() {
        this.running = false;
    }
}
