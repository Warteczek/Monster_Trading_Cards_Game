package at.fhtw.dataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DBSingleton {
    INSTANCE;

    public Connection establishConnection(){

        try{
            Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                    "TeMarcelo",
                    "21222324");

            connection.setAutoCommit(false);

            return connection;
        }
        catch(SQLException exception){
            exception.printStackTrace();
        }

        return null;
    }
}
