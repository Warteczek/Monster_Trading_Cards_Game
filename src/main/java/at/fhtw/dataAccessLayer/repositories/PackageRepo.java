package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageRepo {
    public boolean addPackage(Card[] cards, String packageID, UnitOfWork newUnit){
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
                statement.setString(6, packageID);

                statement.execute();

            } catch(SQLException exception){
                exception.printStackTrace();
            }
        }

        return true;
    }

    public boolean checkCardExists(String cardID, UnitOfWork newUnit){

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM cards WHERE id=?");
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

    public boolean checkPackageExists(String packageID, UnitOfWork newUnit){

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM cards WHERE package_id=?");
            statement.setString(1, packageID);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                return true;
            }
        } catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public List<String> getPackages(UnitOfWork newUnit) {
        List<String> allPackages= new ArrayList<String>();

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT package_id FROM cards");

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                allPackages.add(resultSet.getString("package_id"));
            }
        } catch(SQLException exception){
            exception.printStackTrace();
        }

        return allPackages;

    }

    public void addPackageToStack(String username, String buyPackageID, UnitOfWork newUnit) {

    }
}
