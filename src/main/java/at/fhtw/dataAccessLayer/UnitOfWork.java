package at.fhtw.dataAccessLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnitOfWork {
    private Connection connection;

    public UnitOfWork(){
        this.connection = DBSingleton.INSTANCE.establishConnection();
    }

    public PreparedStatement getStatement(String statement){
        try{
            return this.connection.prepareStatement(statement);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void commit() {
        try{
            this.connection.commit();
            this.connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }

    public void rollback() {
        try{
            this.connection.rollback();
            this.connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void close() {
        try{
            this.connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
