package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.User;

import java.sql.*;

public class UserRepo {

    public void addUser(User user, UnitOfWork newUnit){
        try{
            PreparedStatement statement= newUnit.getStatement("INSERT INTO users(username, password, coins, elo, wins, losses, mtcg_token) VALUES(?,?,?,?,?,?,?)");
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

    public boolean checkUserExists(User user, UnitOfWork newUnit){

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT name, bio, image FROM users WHERE username=?");
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

    public String checkCredentials(User user, UnitOfWork newUnit){
        String password="", token="None";
        try{
            PreparedStatement statement= newUnit.getStatement("SELECT password, mtcg_token FROM users WHERE username=?");
            statement.setString(1, user.getUsername());


            ResultSet resultSet= statement.executeQuery();
            boolean empty=false;
            if (!resultSet.next()){
                empty=true;
                return token;
            }
            if(!empty){
                while (resultSet.next()) {
                    password= resultSet.getString("password");
                    token= resultSet.getString("mtcg_token");
                }
                if(password==user.getPassword()){
                    return token;
                }
            }

        } catch(SQLException exception){
            exception.printStackTrace();
        }

    return "None";
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
