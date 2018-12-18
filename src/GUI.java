
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GUI extends JFrame implements ActionListener {
    private static int TIMER_OFFSET = 3000;

    private JButton[] game_buttons = new JButton[16];
    private JButton ready_button = new JButton("ready");

    private JTextArea time_area = new JTextArea("Time: ");
    private JTextArea win_area = new JTextArea("?");

    private JPanel button_panel = new JPanel();
    private JPanel south_panel = new JPanel();

    private Random rn = new Random();
    private int random_button_count;

    private long time_start;
    private long time_end;
    private long time_diff;
    private String time;

    private ActionListener receiver = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().startsWith("Time")) {
                String comand = actionEvent.getActionCommand().substring(4);
                int time = Integer.parseInt(comand);
                TimerWait(time);
            }else if (actionEvent.getActionCommand().startsWith("Buttons")) {
                String comand = actionEvent.getActionCommand().substring(8);
                System.out.println(comand);
            }
        }
    };

    private GameClient client;

    public GUI(String title) {
        super(title);

        // erzeuge neuen gameclient für kommunikaiton mit server
        client = new GameClient("localhost", 5555);
        client.addActionListener(receiver);
        client.startClient();


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout());
        this.setVisible(true);

        this.getContentPane().add(button_panel, BorderLayout.CENTER);
        this.getContentPane().add(south_panel, BorderLayout.SOUTH);

        button_panel.setLayout(new GridLayout(4, 4));
        south_panel.setLayout(new FlowLayout());
        south_panel.add(win_area);
        south_panel.add(ready_button);
        south_panel.add(time_area);
        time_area.setEditable(false);
        win_area.setEnabled(false);

        ready_button.addActionListener(this);

        ButtonInit();
        ButtonDisable();
        // TimerRun();
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

    public void TimerWait(int time) {
        new Thread() {
            @Override
            public void run() {

                try {
                    Thread.sleep(time);
                    //RandomButtonEnable();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time_start = System.currentTimeMillis();
                System.out.println("Times Over");
            }
        }.start();

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
        if (e.getActionCommand().equals("ready")) {
            client.sendMessage("!");
        } else {
            int buttonNR = Integer.parseInt(e.getActionCommand());
            game_buttons[buttonNR - 1].setEnabled(false);
            game_buttons[buttonNR - 1].setBackground(getBackground());
            this.random_button_count--;
            if ((this.random_button_count) == 0) {
                time_end = System.currentTimeMillis();
                time_diff = time_end - time_start;
                //System.out.println("Zeit bis alle buttons gedrückt wurden:"+time_diff);  // nur für teszwecke ausgegeben
                time = String.valueOf(time_diff);
                time_area.setText(time);
               // TimerRun();
            }
        }
    }
}
