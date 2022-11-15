package at.fhtw.dataAccessLayer;

import java.sql.Connection;

public class UnitOfWork {
    private Connection connection;

    public UnitOfWork(){
        this.connection = DBSingleton.INSTANCE.establishConnection();
    }
}
