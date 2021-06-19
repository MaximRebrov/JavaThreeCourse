package lesson2;

import java.sql.SQLException;

public interface AuthService {

        void start();

        void stop();

        String rename(String nickName, String odlNickName) throws SQLException;

        String searchUser(String login, String password);

        void addUser(String login, String password, String nickName);
}

