package server.db;

import protocol.info.UserInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 03.12.15.
 */
public class DataBaseController {

    private Connection dbConnection;
    private Statement statement;

    public DataBaseController(){
        dbConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/test", "", "");
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean exist(String login)  {
        String query = "SELECT COUNT(login) FROM users WHERE login = '"+login + "';";
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return false;
    }

    public void add(String login, String password) {
        String query = "INSERT INTO users (login, password, friend_count, authorized) VALUES ('"+login +"', '" + password + "', " + 0 +", "+ 1 + ");";
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public boolean isValid(String login, String password) {
        String query = "SELECT COUNT(*) FROM USERS WHERE LOGIN = '" + login + "' AND PASSWORD = '" + password + "';";
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                int count = resultSet.getInt(1);
                if(count>0){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        }
        return false;
    }

    public int getID(String login) {
        String queryID = "SELECT user_id FROM users WHERE login='" + login + "';";
        int ID = 0;
        try {
            ResultSet rs = statement.executeQuery(queryID);
            while (rs.next()) {
                ID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return ID;
    }

    public void addFriend(int user_id, int friend_id){
        String query = "INSERT INTO friends (friend_one,friend_two) VALUES ('"+user_id+"', '"+friend_id+ "');";
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public void removeFriend(int user_id, int friend_id){

    }


    public List<UserInfo> getFriendList(int user_id) {
        List<UserInfo> friends = new ArrayList<>();
        String query = "SELECT user_id, login, authorized FROM users WHERE user_id IN (" +
                "SELECT friend_two FROM friends WHERE" +
                "(friend_one='"+user_id+"' AND status='1')" +
                "UNION " +
                "SELECT friend_one FROM friends WHERE" +
                "(friend_two='"+user_id+"' AND status='1'));";
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String login = rs.getString(2);
                int isAuthorized = rs.getInt(3);
                System.out.println("friends " + user_id + ": id "+ id + ", login "+ login+", Authorized "+ isAuthorized);
                friends.add(new UserInfo(id, login, isAuthorized));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public void setAuthorized(Integer ID, int state){
        String query = "UPDATE users\n" +
                "SET authorized='"+state+"'\n" +
                "WHERE\n" +
                "(user_id='"+ID+"');";
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public UserInfo getUserInfo(Integer id) {
        String query = "SELECT user_id, login, authorized FROM users WHERE user_id='" + id + "';";
        UserInfo userInfo = null;
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                int ID = rs.getInt(1);
                String login = rs.getString(2);
                int isAuthorized = rs.getInt(3);
                userInfo = new UserInfo(ID, login, isAuthorized);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return userInfo;
    }

    public void acceptFriend(int ID, int friendID) {
        String query ="UPDATE friends\n" +
                "SET status='"+ 1 +"' WHERE\n" +
                "(friend_one='"+ ID + "' OR friend_two='"+ ID + "')\n" +
                "AND\n" +
                "(friend_one='"+ friendID +"' OR friend_two='"+ friendID +"');";
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public boolean isFriends(int user_id, int friend_id){
        String query = "SELECT friend_one, friend_two, status FROM friends\n" +
                "WHERE\n" +
                "(friend_one='" + user_id + "' OR friend_two='" + user_id +"')\n" +
                "AND\n" +
                "(friend_one='" + friend_id + "' OR friend_two='" + friend_id + "')";
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                int status = rs.getInt(3);
                if(status==1){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return false;
    }
}
