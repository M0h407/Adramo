package generarNomina;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	public static Connection connectDatabase() {
    	Connection conn = null;
        String servidor = "jdbc:postgresql://192.168.1.69:5432/adramo";
        String user = "oriadix";
        String password = "Fat/3232";
	    	try {
	    		Class.forName("org.postgresql.Driver").newInstance();
	    		conn = DriverManager.getConnection(servidor, user, password);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    return conn;
    }
}