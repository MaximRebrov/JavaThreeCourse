package lesson2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";

            new Thread(() -> {
                try {
                    authentication();
                    readMessage();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch(IOException e){
            throw new RuntimeException("Problems creating a client handler");
        }
    }

    private void closeConnection () {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " exited the chat");
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() throws IOException, SQLException {
        DataBaseApp dataBaseApp = new DataBaseApp();
        while (true){
            String strFromClient = inputStream.readUTF();
            System.out.println("from " + name + " :" + strFromClient);
            if(name.equals("administrator")) {
                if (strFromClient.startsWith(ChatConstants.ADD_NEW_USER)) {
                    String[] parts = strFromClient.split("\\s+");
                    dataBaseApp.addUser(parts[1], parts[2], parts[3]);
                }
            }
            if(strFromClient.startsWith(ChatConstants.RENAME)){
                String newNickName;
                String[] parts = strFromClient.split("\\s+");
                newNickName = dataBaseApp.rename(parts[1], name);
                sendMsg(name + " changed the nickname to " + newNickName);
                name = newNickName;
            }
            if(strFromClient.equals(ChatConstants.STOP_WORD)){
                return;
            }
            if(strFromClient.startsWith(ChatConstants.PRIVATE_MSG)){
                String[] parts = strFromClient.split("\\s+");
                myServer.privateMsg(name + " -> " + parts[1] + " (PM): " + parts[2], name, parts[1]);
            }else {
                myServer.broadcastMsg(name + " : " + strFromClient);
            }
        }
    }

    public void authentication() throws IOException, SQLException {
        DataBaseApp dataBaseApp = new DataBaseApp();
        long start = System.currentTimeMillis();
        long end = start + 12 * 1000;
        while (System.currentTimeMillis() < end) {
            String str = inputStream.readUTF();
            if (str.startsWith(ChatConstants.AUTH_COMMAND)){
                String[] parts = str.split("\\s+");
                String nick = dataBaseApp.searchUser(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg(ChatConstants.AUTH_OK + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " went to the chat");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("account is busy");
                    }
                } else {
                    sendMsg("invalid username/password");
                }
            }
        }
        closeConnection();
    }

    public void sendMsg(String msg) {
        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


