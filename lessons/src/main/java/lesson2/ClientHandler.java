package lesson2;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket, AuthService authService) {
        try {
            this.authService = authService;
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

    public void closeConnection() {
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
        while (true){
            String strFromClient = inputStream.readUTF();
            Client.outputStream(name, strFromClient);
            System.out.println("from " + name + " :" + strFromClient);
            if(name.equals("administrator")) {
                if (strFromClient.startsWith(ChatConstants.ADD_NEW_USER)) {
                    String[] parts = strFromClient.split("\\s+");
                    authService.addUser(parts[1], parts[2], parts[3]);
                }
            }
            if(strFromClient.startsWith(ChatConstants.RENAME)){
                String newNickName;
                String[] parts = strFromClient.split("\\s+");
                newNickName = authService.rename(parts[1], name);
                sendMsg("<" + name +">" + " changed the nickname to " + newNickName);
                name = newNickName;
            }
            if(strFromClient.equals(ChatConstants.STOP_WORD)){
                return;
            }
            if(strFromClient.startsWith(ChatConstants.PRIVATE_MSG)){
                String[] parts = strFromClient.split("\\s+");
                myServer.privateMsg("<" + name + ">" + " -> " + parts[1] + " (PM): " + parts[2], name, parts[1]);
            }else {
                myServer.broadcastMsg("<" + name + ">" + " : " + strFromClient);
            }
        }
    }

    public void authentication() throws IOException, SQLException {
        long start = System.currentTimeMillis();
        long end = start + 120 * 1000;
        while (System.currentTimeMillis() < end) {
            String str = inputStream.readUTF();
            if (str.startsWith(ChatConstants.AUTH_COMMAND)){
                String[] parts = str.split("\\s+");
                String nick = authService.searchUser(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg(ChatConstants.AUTH_OK + " " + nick);
                        name = nick;
                        myServer.broadcastMsg("<" + name + ">" + " went to the chat");
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


