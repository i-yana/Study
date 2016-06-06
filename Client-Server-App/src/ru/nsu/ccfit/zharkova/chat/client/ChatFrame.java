package ru.nsu.ccfit.zharkova.chat.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

/**
 * Created by Yana on 07.07.15.
 */
public class ChatFrame extends JFrame {

    public final static String CHAT_NAME = "*** 514.room chat ***";
    private String lastUsername;
    private StyledDocument doc;
    private SimpleAttributeSet attr = new SimpleAttributeSet();

    private JTextPane ta;
    private JTextField tf;
    private JTextField tfHost;
    private JTextField tfPort;

    private JButton login;
    private JButton logout;

    private DefaultListModel<String> listModel;

    private JLabel label;
    private JLabel red, green, yellow, blue, white, magenta, orange, cyan, pink, black, myColor;

    private Color tColor = new Color(255,255,255);

    private final Controller controller;

    public ChatFrame(Controller controller) throws IOException, BadLocationException {
        super(CHAT_NAME);
        this.controller = controller;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("font.jpg")))));
        int defaultPort = ClientConfig.PORT;
        String defaultHost = ClientConfig.HOST;
        tfHost = new JTextField(defaultHost);
        tfPort = new JTextField(String.valueOf(defaultPort));
        JLabel lbHost = new JLabel("Host:");
        JLabel lbPort = new JLabel("Port:");
        lbHost.setBounds(600, 15, 40, 20);
        tfHost.setBounds(643, 15, 100, 20);
        lbPort.setBounds(745, 15, 30, 20);
        tfPort.setBounds(777, 15, 50, 20);
        add(lbHost);
        add(tfHost);
        add(lbPort);
        add(tfPort);

        ta = new JTextPane();
        doc = ta.getStyledDocument();
        doc.insertString(doc.getLength(), "WELCOME TO CHAT!\n", null);
        StyleConstants.setForeground(attr, Color.black);
        DefaultCaret caret = (DefaultCaret)ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ta.setBorder(BorderFactory.createEmptyBorder());
        ta.setFont(new Font("Arial", Font.BOLD, 14));
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBackground(null);
        JScrollPane scrollPane = new JScrollPane(ta, VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBounds(60, 60, 616, 300);
        add(scrollPane);

        tf = new JTextField();
        tf.setBackground(Color.WHITE);
        tf.setBounds(60, 400, 400, 35);
        add(tf);
        login = new JButton("Go to room");
        login.addActionListener(e -> {

            try {
                InetAddress inetAddress = InetAddress.getByName(tfHost.getText().trim());
                String hostAddress = inetAddress.getHostAddress();
                int port = Integer.parseInt(tfPort.getText().trim());
                lastUsername = tf.getText().trim();
                if(lastUsername.isEmpty()){
                    label.setText("Enter nickname and select a color");
                    return;
                }
                controller.handleLoginAction(hostAddress, port, lastUsername);
                ta.setText("");
            } catch (UnknownHostException e1) {
                label.setText("unknown host");
            } catch (NumberFormatException e1){
                label.setText("wrong port number");
            } catch (BadLocationException e1) {
                label.setText("server does not answer");
                controller.handleDisconnect();
            } catch (ParserConfigurationException e1) {
                label.setText("unknown error");
            }
        });
        login.setBounds(472, 400, 100, 35);
        add(login);

        logout = new JButton("Leave");
        logout.addActionListener(e -> {
            try {
                controller.handleLogoutAction("Enter nickname and select a color");
            } catch (IOException | BadLocationException ignored) {}
        });
        logout.setEnabled(false);
        logout.setBounds(580, 400, 100, 35);
        add(logout);

        listModel = new DefaultListModel<String>();
        JList<String> list = new JList<String>(listModel);
        JScrollPane listsp = new JScrollPane(list, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        listsp.setBorder(BorderFactory.createEmptyBorder());
        listsp.setBounds(700, 60, 110, 300);
        add(listsp);

        tf.requestFocus();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (logout.isEnabled()) {
                    logout.doClick();
                }
                super.windowClosing(e);
            }
        });

        JLabel inRoom = new JLabel("Now in room:");
        inRoom.setBounds(700, 40, 150, 20);
        add(inRoom);

        label = new JLabel("Enter nickname and select a color");
        label.setBounds(64, 435, 400, 30);
        add(label);

        red = createColor(Color.red, 64, 475, 20, 20);
        green = createColor(Color.green, 164, 475, 20, 20);
        yellow = createColor(Color.yellow, 84, 475, 20, 20);
        blue = createColor(Color.blue, 184, 475, 20, 20);
        magenta = createColor(Color.magenta, 104, 475, 20, 20);
        orange = createColor(Color.orange, 124, 475, 20, 20);
        cyan = createColor(Color.cyan, 204, 475, 20, 20);
        pink = createColor(Color.pink, 224, 475, 20, 20);
        white = createColor(Color.white, 144, 475, 20, 20);
        black = createColor(Color.black, 244, 475, 20, 20);
        add(red);
        add(green);
        add(yellow);
        add(blue);
        add(magenta);
        add(orange);
        add(cyan);
        add(pink);
        add(white);
        add(black);

        myColor = new JLabel();
        myColor.setBackground(Color.white);
        myColor.setOpaque(true);
        myColor.setBounds(41, 405, 20, 25);
        myColor.setBorder(BorderFactory.createLineBorder(Color.white, 1));
        add(myColor);

        setSize(new Dimension(800, 500));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public Color getMessageColor(){
        return tColor;
    }

    public void setLoginButtonMode(){
        tf.setText("");
        tf.setSelectedTextColor(tColor);
        label.setText("Enter message");
        login.setEnabled(false);
        logout.setEnabled(true);
        tfHost.setEditable(false);
        tfPort.setEditable(false);
        tf.addActionListener(e -> {
            controller.handleSendingAction(tf.getText());
            tf.setText("");
        });
    }

    public void setLogoutButtonMode() {
        login.setEnabled(true);
        logout.setEnabled(false);
        label.setText("Enter nickname and select a color");
        tf.setText(lastUsername);
        tfHost.setEditable(true);
        tfPort.setEditable(true);
        listModel.clear();
        tf.removeAll();
    }

    private JLabel createColor(final Color color, int x1, int y1, int x2, int y2) {
        JLabel colorLabel = new JLabel();
        colorLabel.setBackground(color);
        colorLabel.setOpaque(true);
        colorLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tColor = color;
                myColor.setBackground(color);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        colorLabel.setBounds(x1, y1, x2, y2);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        return colorLabel;
    }


    void append(String str, Color c) {
        try {
            StyleConstants.setForeground(attr, c);
            doc.insertString(doc.getLength(), str + " \n", attr);
            doc.createPosition(doc.getLength());
        } catch (Exception e) {
            label.setText(e.toString());
        }
    }

    public void setMainLabel(String info){
        label.setText(info);
    }

    public DefaultListModel<String> getListModel(){
        return listModel;
    }

}

