package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PackageRepo {
    public void addPackage(, UnitOfWork newUnit){
        try{
            PreparedStatement statement= newUnit.getStatement("INSERT INTO cards(name, type, id, damage, element_type, package_id) VALUES(?,?,?,?,?,?)");
            statement.setString(1, "");
            statement.setString(2, "");
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");

            statement.execute();

        } catch(SQLException exception){
            exception.printStackTrace();
        }

    }
}
