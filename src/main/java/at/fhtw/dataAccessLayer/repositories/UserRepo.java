package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Stats;
import at.fhtw.mtcg_app.model.Trade;
import at.fhtw.mtcg_app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {

    public void addUser(User user, UnitOfWork newUnit) throws Exception {
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

            //creates user in Deck DB
            PreparedStatement statementDeck= newUnit.getStatement("INSERT INTO decks(owner_id) VALUES(?)");
            statementDeck.setString(1, user.getUsername());
            statementDeck.execute();

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("could not add user");
        }
    }

    public boolean checkUserExists(String username, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT name, bio, image FROM users WHERE username=?");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                return true;
            }else{
                return false;
            }
        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("could not update user data");
        }
    }

    public String checkCredentials(User user, UnitOfWork newUnit) throws Exception {
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
            throw new Exception("could not check login credentials");
        }
    }

    public User getUserData(String username, UnitOfWork newUnit) throws Exception {
        String usernameDB="",  name="", bio="", image="";
        int coins = 0, elo = 0, wins = 0, losses = 0;
        try{
            PreparedStatement statement= newUnit.getStatement("SELECT username, coins, elo, wins, losses, name, bio, image FROM users WHERE username=?");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();

            if (resultSet.next()) {
                usernameDB=resultSet.getString("username");
                coins=resultSet.getInt("coins");
                elo=resultSet.getInt("elo");
                wins=resultSet.getInt("wins");
                losses=resultSet.getInt("losses");
                name= resultSet.getString("name");
                bio= resultSet.getString("bio");
                image= resultSet.getString("image");
            }
            else{
                return new User();
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
            throw new Exception("could not get user data");
        }
    }

    public void updateUserData(String username, User user, UnitOfWork newUnit) throws Exception {
        try{
            PreparedStatement statement= newUnit.getStatement("UPDATE users SET bio=?, image=?, name=? WHERE username=?");
            statement.setString(1, user.getBio());
            statement.setString(2, user.getImage());
            statement.setString(3, user.getName());
            statement.setString(4, username);


            statement.executeUpdate();

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("could not update user data");
        }
    }

    public boolean checkUserHasEnoughMoneyForPackage(String username, UnitOfWork newUnit) throws Exception {

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
            throw new Exception("could not check user money");
        }
        return false;
    }

    public void subtractPackageCoinsFromUser(String username, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statementSelect= newUnit.getStatement("SELECT coins FROM users WHERE username=?");
            statementSelect.setString(1, username);

            ResultSet resultSet= statementSelect.executeQuery();
            int coins=0;
            if (resultSet.next()){
                coins=resultSet.getInt("coins");
            }else{
                throw new Exception("coins could not be found");
            }

            coins=coins-5;


            PreparedStatement statement= newUnit.getStatement("UPDATE users SET coins=? WHERE username=?");
            statement.setInt(1, coins);
            statement.setString(2, username);

            statement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new Exception("could not subtract coins from user");
        }
    }

    public Stats getUserStats(String username, UnitOfWork newUnit) throws Exception {
        String name="";
        int elo = 0, wins = 0, losses = 0;
        try{
            PreparedStatement statement= newUnit.getStatement("SELECT name, elo, wins, losses FROM users WHERE username=?");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();

            if (resultSet.next()) {
                elo=resultSet.getInt("elo");
                wins=resultSet.getInt("wins");
                losses=resultSet.getInt("losses");
                name= resultSet.getString("name");
            }
            else{
                return new Stats();
            }

            Stats statsOfUser = new Stats();

            statsOfUser.setElo(elo);
            statsOfUser.setWins(wins);
            statsOfUser.setLosses(losses);
            statsOfUser.setName(name);

            return statsOfUser;

        }catch(SQLException exception) {
            exception.printStackTrace();
            throw new Exception("could not get user data");
        }
    }

    public List<Stats> getScoreboard(String username, UnitOfWork newUnit) throws Exception {
        List<Stats> scoreboard = new ArrayList<>();

        String name="";
        int elo = 0, wins = 0, losses = 0;

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT name, elo, wins, losses FROM users ORDER BY elo DESC");

            ResultSet resultSet= statement.executeQuery();

            while(resultSet.next()){
                elo=resultSet.getInt("elo");
                wins=resultSet.getInt("wins");
                losses=resultSet.getInt("losses");
                name= resultSet.getString("name");



                Stats stats = new Stats();

                stats.setElo(elo);
                stats.setWins(wins);
                stats.setLosses(losses);
                stats.setName(name);

                scoreboard.add(stats);
            }

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get cards");
        }

        return scoreboard;
    }
}
