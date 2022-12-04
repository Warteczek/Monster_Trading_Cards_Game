package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepo {
    public boolean addPackage(Card[] cards, UnitOfWork newUnit){
        for (Card card : cards)
        {
            if(checkCardExists(card.getId(), newUnit)){
                return false;
            }
            try{
                PreparedStatement statement= newUnit.getStatement("INSERT INTO cards(name, type, id, damage, element_type, package_id) VALUES(?,?,?,?,?,?)");
                statement.setString(1, card.getName());
                statement.setString(2, card.getType());
                statement.setString(3, card.getId());
                statement.setInt(4, card.getDamage());
                statement.setString(5, card.getElement());
                statement.setInt(6, card.getPackageID());

                statement.execute();

            } catch(SQLException exception){
                exception.printStackTrace();
            }
        }

        return true;
    }

    public boolean checkCardExists(String cardID, UnitOfWork newUnit){

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM users WHERE username=?");
            statement.setString(1, cardID);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                return true;
            }
        } catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }
}
