import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class GameServer {
    private int port;
    private boolean running = false;
    private static int TIMER_OFFSET = 3000;
    private Random rn = new Random();

    private int readyCnt = 0;

    private int random_button_anzahl;
    private JButton[] game_buttons = new JButton[16];
    private int random_16;
    private int[] activate_buttons;
    private ArrayList<GameConnection> clients = new ArrayList<>();

    public GameServer(int port) {
        this.port = port;
    }

    private ActionListener broadcastListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("!")) {
                ++readyCnt;
                //  System.out.println("con");

                if (readyCnt >= clients.size()) {
                    SetTimer();
                    PlayButtons();
                    // warte eine zufällige zeit
                    // erzeuge zufällige werte
                    /*String asdf = "";
                    random_button_anzahl = (new Random().nextInt(3) + 1);
                    for (int i = 1; i <= random_button_anzahl; i++) {
                        random_16 = new Random().nextInt(15) + 1;
                        activate_buttons[i] = random_16;

                        asdf = asdf + Integer.toString(random_16) + ";";
                    }
                    broadcastMessage("ENABLEBUTTONS;" + asdf);
                */
                }
            } else if (e.getActionCommand().equals("done")) {
                // e.getSource() --> sender vom done
                // sende an alle außer dem sender "you lose"

                // sende an den sender "you win"
            }
        }
    };

    private void PlayButtons() {

    }

    private void broadcastMessage(String msg) {
        for (GameConnection c : clients) {
            c.send(msg);
        }
    }


    private void SetTimer() {
        int random_time = rn.nextInt(TIMER_OFFSET)+TIMER_OFFSET;
        System.out.println("Time: " + random_time);
        broadcastMessage("?");
    }


    public void start() {
        this.running = true;

        Thread serverThread = new Thread() {

            @Override
            public void run() {
                try (ServerSocket server = new ServerSocket(GameServer.this.port)) {
                    while (running) {
                        Socket client = server.accept();
                        System.out.println("CON");
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

    public static void main(String[] args) {
        new GameServer(5555).start();
    }
}
