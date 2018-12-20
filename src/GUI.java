
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame implements ActionListener, KeyListener {

    private int button_count;
    private int points = 0;

    private GameClient client;

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

    private JColorChooser CC = new JColorChooser();
    private Color bgc = Color.GREEN;

    private JMenuBar MenuBar = new JMenuBar();
    private JMenu Menu = new JMenu("Settings");
    private JMenuItem buttonSettings = new JMenuItem("ButtonSettings");
    private JMenuItem info = new JMenuItem("Info");
    private JMenuItem serversettings = new JMenuItem("Server");
    private JMenu buttonColor   = new JMenu("Color");
    private JMenu submenu1 = new JMenu("ButtonSettings");
    private JMenu gamemode = new JMenu("Gamemode");

    private JMenuItem colorGreen = new JMenuItem("Green");
    private JMenuItem colorBlue = new JMenuItem("Blue");
    private JMenuItem colorRed = new JMenuItem("Red");
    private JMenuItem colorYellow = new JMenuItem("Yellow");


    private JMenuItem nonstopmode = new JMenuItem("Speed Mode");
    private JMenuItem beginnermode = new JMenuItem("Beginner Mode");

    public GUI(String title) {
        super(title);

        client = new GameClient("localhost", 5555);
        client.addActionListener(receiver);
        client.startClient();

        this.addKeyListener(this);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout());
        this.setVisible(true);

        west_south_pane.setLayout(new FlowLayout());

        chat_text.setPreferredSize(new Dimension(100, 20));
        chat_area.setEditable(false);

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

        MenuBar.add(Menu);
        MenuBar.add(Box.createHorizontalGlue());
        MenuBar.add(info);
        Menu.add(submenu1);
        Menu.add(serversettings);
        Menu.add(gamemode);
        //buttonSettings.add(submenu1);

        submenu1.add(buttonColor);

        buttonColor.add(colorGreen);
        buttonColor.add(colorBlue);
        buttonColor.add(colorRed);
        buttonColor.add(colorYellow);

        gamemode.add(nonstopmode);
        gamemode.add(beginnermode);

        colorBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                bgc=Color.BLUE;
            }
        });

        colorRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                bgc=Color.RED;
            }
        });
        colorGreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                bgc=Color.GREEN;
            }
        });
        colorYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                bgc=Color.yellow;
            }
        });


        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //buttonSettings.add(Menu);
            }
        });

        button_panel.setLayout(new GridLayout(4, 4));
        south_panel.setLayout(new FlowLayout());

        south_panel.add(ready_button);
        time_area.setEditable(false);
        points_text.setEditable(false);

        ready_button.addActionListener(this);

        ButtonInit();
        ButtonDisable();
    }

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

    private void ButtonInit() {
        for (int i = 0; i < 16; i++) {
            game_buttons[i] = new JButton("" + (i + 1));
            game_buttons[i].addActionListener(this);
            button_panel.add(game_buttons[i]);
            this.pack();
            this.setSize(500, 350);
        }
    }

    public void TimerWait(int time) {
        ready_button.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
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
                time_area.setText("Time: " +time);
            } else {
                chat_area.setText(chat_area.getText() + "\n\r" + actionEvent.getActionCommand());
            }
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ready")) {
            client.sendMessage("!");
            ready_button.setEnabled(false);
            client.sendMessage("TextMessage: I'm READY");
        } else if (e.getActionCommand().equals("SEND")) {
            client.sendMessage("TextMessage:" + chat_text.getText());
            chat_text.setText("");
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

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        /*
        System.out.println(keyEvent.getKeyCode());
        if (keyEvent.getKeyCode() == 10) {
            chat_send.doClick();
        }
        */
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
