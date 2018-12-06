
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.Random;

public class GUI extends JFrame implements ActionListener {
    private static int TIMER_OFFSET = 3000;

    private JButton[] game_buttons = new JButton[16];
    private JButton ready_button = new JButton("READY!");

    private JTextArea score_pane = new JTextArea("Score: ");

    private JPanel button_panel = new JPanel();
    private JPanel south_panel = new JPanel();

    private Random rn = new Random();
    private int random_button_count;

    public GUI(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout());
        this.setVisible(true);

        this.getContentPane().add(button_panel, BorderLayout.CENTER);
        this.getContentPane().add(south_panel, BorderLayout.SOUTH);

        button_panel.setLayout(new GridLayout(4, 4));
        south_panel.setLayout(new FlowLayout());
        south_panel.add(ready_button);
        south_panel.add(score_pane);
        score_pane.setEditable(false);

        ready_button.addActionListener(this);

        GameServer server = new GameServer(5555);
        server.start();

        // c.addActionListener(new ActionListener() {
        //   @Override
        // public void actionPerformed(ActionEvent e) {
        //   if (e.getActionCommand().equals("you win")) {
        // im Textfeld "Gewinner" anzeigen
        // }
        //}
        //});

        ButtonInit();
        ButtonDisable();
        StartTimer();
    }

    private void ButtonInit() {
        for (int i = 0; i < 16; i++) {
            game_buttons[i] = new JButton("" + (i + 1));
            game_buttons[i].addActionListener(this);
            button_panel.add(game_buttons[i]);
            this.pack();
            this.setSize(340, 350);
        }
    }

    private void StartTimer() {
        new Thread() {
            @Override
            public void run() {
                int random_time = rn.nextInt(TIMER_OFFSET);
                try {
                    Thread.sleep(random_time + TIMER_OFFSET);
                    RandomButtonEnable();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void RandomButtonEnable() {
        this.random_button_count = rn.nextInt(4) + 1;
        for (int i = 0; i < random_button_count; i++) {
            int random_button = rn.nextInt(16);
            if (game_buttons[random_button].getBackground() == Color.GREEN) {
                i--;
            } else {
                game_buttons[random_button].setEnabled(true);
                game_buttons[random_button].setBackground(Color.GREEN);
            }
        }
    }

    private void ButtonDisable() {
        for (int i = 0; i < 16; i++) {
            game_buttons[i].setEnabled(false);
            game_buttons[i].setBackground(getBackground());
        }
    }


    public static void main(String[] args) {
        GUI obj = new GUI("ButtonGame");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("READY!")) {
            GameClient c = new GameClient("localhost", 5555);
        } else {
            int buttonNR = Integer.parseInt(e.getActionCommand());
            game_buttons[buttonNR - 1].setEnabled(false);
            game_buttons[buttonNR - 1].setBackground(getBackground());
            this.random_button_count--;
            if ((this.random_button_count) == 0) {
                StartTimer();
            }
        }
    }
}