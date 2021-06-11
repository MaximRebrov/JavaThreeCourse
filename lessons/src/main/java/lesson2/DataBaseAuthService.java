package lesson2;

import java.sql.*;

public class DataBaseAuthService implements AuthService{

    static final String DATABASE_URL = "jdbc:sqlite:javadb.db";
    static Connection connection;
    static Statement statement;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_URL);
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        DataBaseAuthService databaseAuthService = new DataBaseAuthService();
        databaseAuthService.createTable();
    }

    public void createTable() throws SQLException {
        String createTable = "create table person (" +
                "login varchar(30) not null," +
                "password varchar(15) not null," +
                "nickName varchar(30)not null)";
        statement.execute(createTable);
    }

    @Override
    public String searchUser(String login, String password) {
        String sql = String.format("SELECT nickName From person WHERE login = '%s' AND password = '%s';", login, password);
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String str = resultSet.getString(1);
                return str;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public void addUser(String login, String password, String nickName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into person (login, password, nickName) values (?, ?, ?)")){
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickName);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String rename(String nickName, String odlNickName) throws SQLException {
        String sql = String.format("UPDATE person SET nickName = '%s' WHERE nickName = '%s';", nickName, odlNickName);
        statement.execute(sql);
        return nickName;
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

