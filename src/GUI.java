
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {

    private int button_count;
    private int points = 0;

    private JButton[] game_buttons = new JButton[16];
    public JButton ready_button = new JButton("ready");

    private JTextArea time_area = new JTextArea("Time: ");
    private JTextArea points_text = new JTextArea("Points: " + points);

    private JPanel button_panel = new JPanel();
    private JPanel south_panel = new JPanel();
    private JPanel east_panel = new JPanel();
    private JPanel west_panel = new JPanel();
    private JPanel west_south_pane = new JPanel();

    private long time_start;
    private long time_end;
    private long time_diff;
    private String time;
    private String[] split;
    private JTextArea player_name = new JTextArea();
    private JTextArea chat_area = new JTextArea();
    private JTextField chat_text = new JTextField("Massage");
    private JButton chat_send = new JButton("SEND");

    private JMenuBar MenuBar = new JMenuBar();
    private JMenuItem settings = new JMenuItem("settings");

    private ActionListener receiver = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().startsWith("Time")) {
                String comand = actionEvent.getActionCommand().substring(4);
                int time = Integer.parseInt(comand);
                TimerWait(time);
            } else if (actionEvent.getActionCommand().startsWith("Buttons")) {
                String comand = actionEvent.getActionCommand().substring(7);
                split = comand.split(";");
            } else if (actionEvent.getActionCommand().startsWith("WON")) {
                points++;
                points_text.setText("Points: " + points);
                ready_button.setEnabled(true);
            } else if (actionEvent.getActionCommand().startsWith("Spieler")) {
                String comand = actionEvent.getActionCommand().substring(8);
                int player = Integer.parseInt(comand);
                east_panel.setLayout(new GridLayout(3, 1));
                for (int i = 1; i <= player; i++) {
                    player_name.setText("Players: " + (i));
                    player_name.setEditable(false);
                    east_panel.add(player_name);
                    east_panel.add(points_text);
                    east_panel.add(time_area);
                }
            } else if (actionEvent.getActionCommand().startsWith("LOOSE")) {
                ButtonDisable();
                ready_button.setEnabled(true);

            } else if (actionEvent.getActionCommand().startsWith("Winners")) {
                time = actionEvent.getActionCommand().substring(12);
                time_area.setText(time);
            }
        }
    };

    private void PlayButtonStart() {
        button_count = Integer.parseInt(split[0]);
        int i = 0;
        while (i < button_count) {
            int buttonUsed = Integer.parseInt(split[i + 1]);
            game_buttons[buttonUsed].setEnabled(true);
            game_buttons[buttonUsed].setBackground(Color.GREEN);
            i++;
        }
    }

    private GameClient client;

    public GUI(String title) {
        super(title);

        client = new GameClient("localhost", 5555);
        client.addActionListener(receiver);
        client.startClient();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout());
        this.setVisible(true);

        west_south_pane.setLayout(new FlowLayout());

        west_panel.setLayout(new BorderLayout());
        west_panel.add(chat_area, BorderLayout.CENTER);
        west_panel.add(west_south_pane, BorderLayout.SOUTH);

        chat_send.addActionListener(this);
        west_south_pane.add(chat_text);
        west_south_pane.add(chat_send);

        this.getContentPane().add(button_panel, BorderLayout.CENTER);
        this.getContentPane().add(south_panel, BorderLayout.SOUTH);
        this.getContentPane().add(MenuBar, BorderLayout.NORTH);
        this.getContentPane().add(east_panel, BorderLayout.EAST);
        this.getContentPane().add(west_panel, BorderLayout.WEST);

        MenuBar.add(settings);

        button_panel.setLayout(new GridLayout(4, 4));
        south_panel.setLayout(new FlowLayout());

        south_panel.add(ready_button);
        time_area.setEditable(false);
        points_text.setEditable(false);

        ready_button.addActionListener(this);

        ButtonInit();
        ButtonDisable();
    }

    private void ButtonInit() {
        for (int i = 0; i < 16; i++) {
            game_buttons[i] = new JButton("" + (i + 1));
            game_buttons[i].addActionListener(this);
            button_panel.add(game_buttons[i]);
            this.pack();
            this.setSize(440, 350);
        }
    }

    public void TimerWait(int time) {
        ready_button.setEnabled(false);
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
                PlayButtonStart();
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
        } else if (e.getActionCommand().equals("SEND")) {
            System.out.println(chat_text.getText());

        } else {
            int buttonNR = Integer.parseInt(e.getActionCommand());
            System.out.println(e.getActionCommand());
            game_buttons[buttonNR - 1].setEnabled(false);
            game_buttons[buttonNR - 1].setBackground(getBackground());
            this.button_count--;
            if ((this.button_count) == 0) {
                time_end = System.currentTimeMillis();
                time_diff = time_end - time_start;
                time = String.valueOf(time_diff);
                time_area.setText(time);
                client.sendMessage("Done" + time);
            }
        }
    }
}
