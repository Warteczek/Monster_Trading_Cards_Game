package at.fhtw.mtcg_app.service.user;

import at.fhtw.mtcg_app.model.User;

import java.sql.*;

public class UserDAL {
    public UserDAL() {


    }


    public boolean checkUserExists(User user){

        try(Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "TeMarcelo",
                "21222324");
            PreparedStatement statement= connection.prepareStatement("""
                SELECT name, bio, image FROM users
                WHERE username=?;
                """)
        ){
            statement.setString(1, user.getUsername());

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                return true;
            }
        } catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }
    public void addUser(User user){
        try(Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "TeMarcelo",
                "21222324");
            PreparedStatement statement= connection.prepareStatement("""
                INSERT INTO users(username, password, coins, elo, wins, losses, mtcg_token)
                VALUES(?,?,?,?,?,?,?);
                """)
        ){
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, 20);
            statement.setInt(4, 100);
            statement.setInt(5, 0);
            statement.setInt(6, 0);
            statement.setString(7, user.getUsername()+"-mtcgToken");

            statement.execute();

        } catch(SQLException exception){
            exception.printStackTrace();
        }

    }
    /*
    public User getUser(){
        try(Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "TeMarcelo",
                "21222324");
            PreparedStatement statement= connection.prepareStatement("""
                SELECT name, bio, image FROM users
                WHERE username=?;
                """)
        ){
            statement.setString(1, user.getUsername());


            statement.execute();

        } catch(SQLException exception){
            exception.printStackTrace();
        }

    }*/
}
