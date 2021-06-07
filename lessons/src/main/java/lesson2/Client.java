package lesson2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame {

        private JTextArea chatArea;
        private JTextField msgInputField;

        private static Socket socket = new Socket();
        private static DataOutputStream outputStream;
        private static DataInputStream inputStream;

        public Client() {
            try {
                openConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            prepareGUI();
        }

        private void prepareGUI() {
            setBounds(600, 300, 500, 600);
            setTitle("Client");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.getLineWrap();
            add(new JScrollPane(chatArea), BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            JButton button = new JButton("Send");
            bottomPanel.add(button, BorderLayout.EAST);
            msgInputField = new JTextField();
            add(bottomPanel, BorderLayout.SOUTH);
            bottomPanel.add(msgInputField, BorderLayout.CENTER);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage();
                }
            });
            msgInputField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage();
                }
            });

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    try {
                        outputStream.writeUTF(ChatConstants.STOP_WORD);
                        closeConnection();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });

            setVisible(true);
        }

        private void sendMessage() {
            if (!msgInputField.getText().trim().isEmpty()) {
                try {
                    outputStream.writeUTF(msgInputField.getText());
                    msgInputField.setText("");
                    msgInputField.grabFocus();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

        private void openConnection() throws IOException {
            socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String strFromServer = inputStream.readUTF();
                            if(strFromServer.equals(ChatConstants.AUTH_OK)){
                                break;
                            }
                            chatArea.append(strFromServer);
                            chatArea.append("\n");
                        }
                        while (true) {
                            String strFromServer = inputStream.readUTF();
                            if (strFromServer.equals(ChatConstants.STOP_WORD)) {
                                break;
                            }
                            chatArea.append(strFromServer);
                            chatArea.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            throw new EOFException("Time out auth");
                        } catch (EOFException eofException) {
                            eofException.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public static void closeConnection() {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
                    new Client();
        }
    }
