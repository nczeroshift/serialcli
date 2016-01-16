
/**
 * Serial CLI (Serial Port Command Line Interface)
 * Build 1.0.0
 * Created by Luís F. Loureiro, github.com/nczeroshift
 * Under MIT license
 */

package org.nczeroshift.serialcli;

import jssc.*;
import org.nczeroshift.commons.JFrameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    private static String defaultRate = "9600";

    private static final String[] baudRateValues = new String[]{
            "300",
            "600",
            "1200",
            "2400",
            "9600",
            "14400",
            "19200",
            "28800",
            "38400",
            "57600",
            "76800",
            "115200",
            "230400",
            "250000",
            "256000"};

    private static final String welcomeMsg = "Serial CLI - Serial port Command Line Interface\n" +
            "Version 1.0\n" + "github.com/nczeroshift/serialcli\n" + "\n" +
            "Not connected!\n";
    ;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("YY/MM/dd HH:mm:ss");
    private static final String baudRateDefaultValue = "9600";

    private static final Color grayBg2 = new Color(0x393939);
    private static final Color grayBg = new Color(0x727272);

    private JComboBox serialPortSelection = null;
    private JComboBox baudRateComboBox = null;
    private JButton serialPortConnectButton = null;
    private JButton serialPortDisconnectButton = null;
    private JTextArea cliCommandList = null;
    private JTextField cliInputTextField = null;
    private JButton cliSendBtn = null;
    private ArrayList<String> messages = new ArrayList<String>();
    private int currentBackIndex = 0;
    private SerialPort serialPort = null;

    public static JPanel createFixedPadding(int size) {
        JPanel ret = new JPanel();
        ret.setMaximumSize(new Dimension(size, size));
        ret.setMinimumSize(new Dimension(size, size));
        ret.setPreferredSize(new Dimension(size, size));
        ret.setBackground(null);
        return ret;
    }

    public static JPanel createHorizontalPadding() {
        JPanel ret = new JPanel();
        ret.setMaximumSize(new Dimension(9999, 1));
        ret.setMinimumSize(new Dimension(9999, 1));
        ret.setPreferredSize(new Dimension(9999, 1));
        ret.setBackground(null);
        return ret;
    }

    public static JFrame createWindow(String title, int width, int height, JPanel mainPanel) {
        JFrame frame = new JFrame();
        frame.setTitle(title);

        frame.setSize(new Dimension(width, height));
        frame.setPreferredSize(new Dimension(width, height));
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(mainPanel);
        frame.pack();
        return frame;
    }

    public Main() throws IOException, FontFormatException {
        Font codeFont = new Font("Bitstream Vera Sans Mono", Font.PLAIN, 14);

        JPanel panel = new JPanel();
        panel.setBackground(grayBg);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel topSection = new JPanel();
        topSection.setMaximumSize(new Dimension(9999, 32));
        topSection.setMinimumSize(new Dimension(20, 32));
        topSection.setPreferredSize(new Dimension(20, 32));
        topSection.setBackground(null);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.X_AXIS));
        //topSection.setBackground(Color.RED);
        panel.add(topSection);

        topSection.add(createFixedPadding(10));

        JLabel serialPort = new JLabel("Port");
        serialPort.setForeground(Color.WHITE);
        topSection.add(serialPort);
        topSection.add(createFixedPadding(10));

        String[] serialPortNames = SerialPortList.getPortNames();
        //String [] serialPortNames  = new String[]{"/dev/tty.linvor-DevB"};
        serialPortSelection = new JComboBox<String>();
        serialPortSelection.setModel(new DefaultComboBoxModel(serialPortNames));
        serialPortSelection.setSize(120, 25);
        serialPortSelection.setPreferredSize(new Dimension(120, 25));
        serialPortSelection.setMaximumSize(new Dimension(120, 25));
        serialPortSelection.setMinimumSize(new Dimension(120, 25));
        topSection.add(serialPortSelection);

        topSection.add(createFixedPadding(10));

        JLabel baudrate = new JLabel("Baud");
        baudrate.setForeground(Color.WHITE);
        topSection.add(baudrate);
        topSection.add(createFixedPadding(10));

        baudRateComboBox = new JComboBox<String>(baudRateValues);
        baudRateComboBox.setSize(120, 25);
        baudRateComboBox.setPreferredSize(new Dimension(120, 25));
        baudRateComboBox.setMaximumSize(new Dimension(120, 25));
        baudRateComboBox.setMinimumSize(new Dimension(120, 25));
        baudRateComboBox.setSelectedItem(baudRateDefaultValue);
        baudRateComboBox.setSelectedItem(defaultRate);
        topSection.add(baudRateComboBox);

        topSection.add(createFixedPadding(10));

        serialPortConnectButton = new JButton("Connect");
        serialPortConnectButton.setSize(120, 24);
        serialPortConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });
        topSection.add(serialPortConnectButton);

        serialPortDisconnectButton = new JButton("Disconnect");
        serialPortDisconnectButton.setSize(120, 24);
        serialPortDisconnectButton.setVisible(false);
        serialPortDisconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        });
        topSection.add(serialPortDisconnectButton);

        topSection.add(createHorizontalPadding());

        JPanel middleSection = new JPanel();
        middleSection.setBackground(null);
        panel.add(middleSection);
        middleSection.setLayout(new BoxLayout(middleSection, BoxLayout.X_AXIS));

        middleSection.add(createFixedPadding(10));

        cliCommandList = new JTextArea();

        cliCommandList.setBorder(BorderFactory.createEmptyBorder());
        cliCommandList.setBackground(grayBg2);
        cliCommandList.setForeground(Color.WHITE);
        cliCommandList.setFont(codeFont);
        cliCommandList.setEditable(false);

        cliCommandList.setText(welcomeMsg);

        JScrollPane cliScroll = new JScrollPane(cliCommandList);
        cliScroll.setBorder(BorderFactory.createLineBorder(grayBg2, 4));
        middleSection.add(cliScroll);

        middleSection.add(createFixedPadding(10));

        JPanel bottomSection = new JPanel();
        bottomSection.setMinimumSize(new Dimension(40, 40));
        bottomSection.setMaximumSize(new Dimension(9999, 40));
        bottomSection.setPreferredSize(new Dimension(40, 40));
        bottomSection.setBackground(null);
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.X_AXIS));

        bottomSection.add(createFixedPadding(10));

        JLabel cliInputIndicator = new JLabel(">", JLabel.CENTER);
        cliInputIndicator.setMaximumSize(new Dimension(25, 25));
        cliInputIndicator.setMinimumSize(new Dimension(25, 25));
        cliInputIndicator.setPreferredSize(new Dimension(25, 25));
        cliInputIndicator.setBorder(BorderFactory.createLineBorder(grayBg2, 2));

        cliInputIndicator.setBackground(grayBg2);
        cliInputIndicator.setForeground(Color.WHITE);
        cliInputIndicator.setFont(codeFont);
        cliInputIndicator.setOpaque(true);
        bottomSection.add(cliInputIndicator);

        cliInputTextField = new JTextField();
        cliInputTextField.setMaximumSize(new Dimension(9999, 25));
        cliInputTextField.setMinimumSize(new Dimension(25, 25));
        cliInputTextField.setPreferredSize(new Dimension(25, 25));
        cliInputTextField.setBorder(BorderFactory.createLineBorder(grayBg2, 4));
        cliInputTextField.setBackground(grayBg2);
        cliInputTextField.setForeground(Color.WHITE);
        cliInputTextField.setFont(codeFont);
        bottomSection.add(cliInputTextField);

        bottomSection.add(createFixedPadding(10));

        cliSendBtn = new JButton("Send (Enter)");
        cliSendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        cliInputTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                    sendMessage();


            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (messages.size() > 0) {
                        cliInputTextField.setText(messages.get(currentBackIndex));

                        if (currentBackIndex > 0)
                            currentBackIndex--;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (messages.size() > 0 && currentBackIndex < messages.size() - 1) {
                        cliInputTextField.setText(messages.get(currentBackIndex));

                        if (currentBackIndex < messages.size() - 1)
                            currentBackIndex++;
                    }
                }
            }
        });

        bottomSection.add(cliSendBtn);

        bottomSection.add(createFixedPadding(10));

        panel.add(bottomSection);

        createWindow("Serial CLI", 800, 600, panel).setVisible(true);
    }

    private void addText(final String txt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                cliCommandList.setText(cliCommandList.getText() + txt);
            }
        });
    }

    private void sendMessage() {
        try {

            String txt = cliInputTextField.getText();

            if (txt.trim().length() == 0)
                return;

            if (messages.size() == 0 || !messages.get(messages.size() - 1).equals(txt))
                messages.add(txt);

            currentBackIndex = messages.size() - 1;

            cliInputTextField.setText("");

            if (serialPort != null)
                serialPort.writeString(txt);//+"\r\n"

        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            Integer baudRateValue = Integer.parseInt(baudRateComboBox.getSelectedItem().toString());

            serialPort = new SerialPort(serialPortSelection.getSelectedItem().toString());
            serialPort.openPort();
            serialPort.setParams(baudRateValue,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            int maskFlags = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            serialPort.setEventsMask(maskFlags);
            serialPort.addEventListener(new SerialPortReader());

            addText("\n" + dateFormat.format(new Date()) + " - Connected\n");

            serialPortSelection.setEnabled(false);
            baudRateComboBox.setEnabled(false);
            serialPortConnectButton.setVisible(false);
            serialPortDisconnectButton.setVisible(true);

        } catch (Exception e) {
            addText("\nError opening serial port.\n");
        }
    }

    private void disconnect() {

        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        serialPort = null;

        addText("\n" + dateFormat.format(new Date()) + " - Disconnected\n");
        serialPortSelection.setEnabled(true);
        baudRateComboBox.setEnabled(true);
        serialPortConnectButton.setVisible(true);
        serialPortDisconnectButton.setVisible(false);

        String[] serialPortNames = SerialPortList.getPortNames();
        String oldPort = (String) serialPortSelection.getSelectedItem();

        DefaultComboBoxModel model = (DefaultComboBoxModel) serialPortSelection.getModel();
        model.removeAllElements();


        for (String s : serialPortNames)
            model.addElement(s);

        if (model.getIndexOf(oldPort) >= 0)
            serialPortSelection.setSelectedItem(oldPort);
    }

    class SerialPortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {
                if (event.getEventValue() > 0) {

                    try {
                        String buffer = serialPort.readString(event.getEventValue());
                        addText(buffer);
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, FontFormatException {

        JFrameUtils.loadLookAndFeel();
        new Main();
    }
}
