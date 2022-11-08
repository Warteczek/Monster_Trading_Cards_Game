package at.fhtw.mtcg_app.service.user;

import at.fhtw.mtcg_app.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAL {
    public UserDAL() {

    }

    public void addUser(User user){
        try(Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "TeMarcelo",
                "21222324");
            PreparedStatement statement= connection.prepareStatement("""
                INSERT INTO users(username, password, coins, elo, wins, losses)
                VALUES(?,?,?,?,?,?);
                """)
        ){
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, 20);
            statement.setInt(4, 100);
            statement.setInt(5, 0);
            statement.setInt(6, 0);

            statement.execute();

        } catch(SQLException exception){
            exception.printStackTrace();
        }

    }

    public User getUser(){
        return null;

    }
}
