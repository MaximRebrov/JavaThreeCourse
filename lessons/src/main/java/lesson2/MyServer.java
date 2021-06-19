package lesson2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {

    private List<ClientHandler> clients;
    private AuthService authService;
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    public AuthService getAuthService(){
        return authService;
    }

    public MyServer(){
        try (ServerSocket server = new ServerSocket(ChatConstants.PORT)){
            authService = new DataBaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true){
                System.out.println("Server started. Waiting connection");
                Socket socket = server.accept();
                System.out.println("Client connection");
                executorService.execute(() -> new ClientHandler(this, socket, authService));
            }
        } catch (IOException e) {
            System.out.println("Server error");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(authService != null){
                authService.stop();
                executorService.shutdown();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick){
        for(ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg){
        for(ClientHandler o : clients){
            o.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }

    public void privateMsg(String msg, String... nick){
        int countList = 0;
        int countNick = nick.length;
        for(ClientHandler o : clients){
            for (String n : nick) {
                if(o.getName().equals(n)){
                    o.sendMsg(msg);
                    if(++countList == countNick) {
                        return;
                    }
                }
            }
        }
    }
}
