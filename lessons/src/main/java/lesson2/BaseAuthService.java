package lesson2;

import java.sql.PreparedStatement;
import java.util.List;

public class BaseAuthService implements AuthService {

    private PreparedStatement sql;

    private class Entry {
        private String login;
        private String pass;
        private String nick;


        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    @Override
    public void start() {
        System.out.println("Service auth started");
    }

    @Override
    public void stop() {
        System.out.println("Service auth stoped");
    }

}
