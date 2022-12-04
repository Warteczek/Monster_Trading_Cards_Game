package at.fhtw.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectionTest {

    @Test
    void testDBConnection() throws Exception{
        String successCon;
        try(Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "TeMarcelo",
                "21222324");){

            successCon="Successful connection";

        } catch(SQLException exception){
            successCon="Connection failed";
            exception.printStackTrace();
        }

        assertEquals("Successful connection", successCon);
    }


}
