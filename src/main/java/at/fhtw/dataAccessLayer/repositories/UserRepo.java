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

    public boolean checkUserExists(String username, UnitOfWork newUnit){

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT name, bio, image FROM users WHERE username=?");
            statement.setString(1, username);

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

            boolean empty=true;

            while (resultSet.next()) {
                empty=false;
                password= resultSet.getString("password");
                token= resultSet.getString("mtcg_token");
            }
            if(empty){
                return "None";
            }

            if(password.equals(user.getPassword())){
                return token;
            }
            else{
                return "None";
            }

        } catch(SQLException exception){
            exception.printStackTrace();
        }

        return "None";
    }

    public User getUserData(String username, UnitOfWork newUnit) {
        String usernameDB="",  name="", bio="", image="";
        int coins = 0, elo = 0, wins = 0, losses = 0;
        try{
            PreparedStatement statement= newUnit.getStatement("SELECT username, coins, elo, wins, losses, name, bio, image FROM users WHERE username=?");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();

            while (resultSet.next()) {
                usernameDB=resultSet.getString("username");
                coins=resultSet.getInt("coins");
                elo=resultSet.getInt("elo");
                wins=resultSet.getInt("wins");
                losses=resultSet.getInt("losses");
                name= resultSet.getString("name");
                bio= resultSet.getString("bio");
                image= resultSet.getString("image");
            }

            User user = new User();

            user.setUsername(usernameDB);
            user.setCoins(coins);
            user.setElo(elo);
            user.setWins(wins);
            user.setLosses(losses);

            user.setName(name);
            user.setBio(bio);
            user.setImage(image);

            return user;

        }catch(SQLException exception) {
            exception.printStackTrace();
        }
        return new User();
    }

    public void updateUserData(String username, User user, UnitOfWork newUnit) {
        try{
            PreparedStatement statement= newUnit.getStatement("UPDATE users SET bio=?, image=?, name=? WHERE username=?");
            statement.setString(1, user.getBio());
            statement.setString(2, user.getImage());
            statement.setString(3, user.getName());
            statement.setString(4, username);


            statement.executeUpdate();

        } catch(SQLException exception){
            exception.printStackTrace();
        }
    }

    public boolean checkUserHasEnoughMoneyForPackage(String username, UnitOfWork newUnit){

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT coins FROM users WHERE username=?");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                if(resultSet.getInt("coins")>=5){
                    return true;
                }

            }
        } catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public void subtractPackageCoinsFromUser(String username, UnitOfWork newUnit) {

    }
}
