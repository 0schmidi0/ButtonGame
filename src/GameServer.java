import javax.swing.*;
import java.awt.*;
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

    private ArrayList<GameConnection> clients = new ArrayList<>();

    public GameServer(int port) {
        this.port = port;
    }

    private ActionListener broadcastListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("!")) {
                ++readyCnt;


                if (readyCnt >= clients.size()) {
                    SetTimer();
                    PlayButtons();
                    readyCnt = 0;
                }
            } else if (e.getActionCommand().startsWith("Done")) {
                String time = e.getActionCommand().substring(4);
                GameConnection c = (GameConnection) e.getSource();
                System.out.println(c);
                c.send("WON");
                broadcastMessage("Winnerstime:" + time);
                for (GameConnection con : clients) {
                    if (con != c) {
                        con.send("LOOSE");
                    }
                }
            }else if (e.getActionCommand().startsWith("TextMess")){
                String message = e.getActionCommand().substring(12);
                broadcastMessage(message);

            }
        }
    };

    private void PlayButtons() {
        int random_button_count = rn.nextInt(4) + 1;
        int[] activeButtons = new int[random_button_count];

        int i = 0;
        String mass = "Buttons" + random_button_count + ";";

        while (i < random_button_count) {
            int random_button = rn.nextInt(16);

            if (!containsValue(activeButtons, random_button)) {
                activeButtons[i] = random_button;
                mass = mass + random_button + ";";
                ++i;
            }
        }
        broadcastMessage(mass);
    }

    private boolean containsValue(int[] arr, int value) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == value) {
                return true;
            }
        }

        return false;
    }

    private void broadcastMessage(String msg) {
        for (GameConnection c : clients) {
            c.send(msg);
        }
    }


    private void SetTimer() {
        int random_time = rn.nextInt(TIMER_OFFSET) + TIMER_OFFSET;
        System.out.println("Time: " + random_time);
        broadcastMessage("Time" + random_time);
    }


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
                        broadcastMessage("Spieler:" + clients.size());
                    }
                } catch (Exception e) {
                    System.out.println("Can't start Server");
                }
            }
        };
        serverThread.start();
    }

    public void stop() {
        this.running = false;
    }

    public static void main(String[] args) {
        new GameServer(4555).start();
    }
}
