package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.User;
import jdk.jshell.spi.ExecutionControl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageRepo {
    public boolean addPackage(Card[] cards, String packageID, UnitOfWork newUnit) throws Exception {
        for (Card card : cards)
        {
            try{
                if(checkCardExists(card.getId(), newUnit)){
                    return false;
                }

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
                throw new Exception("Could not add Package");
            }
        }

        return true;
    }

    public boolean checkCardExists(String cardID, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM cards WHERE id=?");
            statement.setString(1, cardID);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                return true;
            }
        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not check if card exists");
        }
        return false;
    }

    public boolean checkPackageExists(String packageID, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM cards WHERE package_id=?");
            statement.setString(1, packageID);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                return true;
            }
        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not check if package exists");
        }
        return false;
    }

    public List<String> getPackages(UnitOfWork newUnit) throws Exception {
        List<String> allPackages= new ArrayList<String>();
        try{
            PreparedStatement statement= newUnit.getStatement("SELECT DISTINCT package_id FROM cards WHERE buyable=?");
            statement.setBoolean(1, true);

            ResultSet resultSet= statement.executeQuery();
            while (resultSet.next()){
                allPackages.add(resultSet.getString("package_id"));
            }
        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get packages");
        }
        return allPackages;
    }

    public void addPackageToStack(String username, String buyPackageID, UnitOfWork newUnit) throws Exception {
        try{

            PreparedStatement statementUpdate= newUnit.getStatement("UPDATE cards SET buyable=? WHERE package_id=?");
            statementUpdate.setBoolean(1, false);
            statementUpdate.setString(2, buyPackageID);
            statementUpdate.executeUpdate();


            PreparedStatement statementSelect= newUnit.getStatement("SELECT id FROM cards WHERE package_id=?");
            statementSelect.setString(1, buyPackageID);
            ResultSet resultSet=statementSelect.executeQuery();

            List<String> cardIDs=new ArrayList<String>();
            while(resultSet.next()){
                cardIDs.add(resultSet.getString("id"));
            }


            for (String cardID : cardIDs){
                PreparedStatement statement= newUnit.getStatement("INSERT INTO stack(username, card_id) VALUES(?,?)");
                statement.setString(1, username);
                statement.setString(2, cardID);

                statement.execute();
            }
        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not add cards to stack");
        }
    }
}
