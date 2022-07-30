
package login;

import dataBase.sqlDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class LoginActieRFID {
    
    private static Connection con;
    
       /*
    spreek de functie aan en stuur een RFID tag string mee. 
    Komt de data voor in de database return dan de boolean waarde true zo niet return false. 
    */
    
    public static boolean loginRFID(String gebruikersRFID) {
		try {
			con = sqlDataSource.getConnection();
			Statement stat = con.createStatement();
			ResultSet result = stat.executeQuery(
					"select begeleiderRFID from begeleideraccount WHERE begeleiderRFID = '"
							+ gebruikersRFID + "'");

			while (result.next()) {
				LoginSessie.RFID = result.getString("begeleiderRFID");
				
				return true;
			}
		} catch (SQLException se) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Database error: " + se.getMessage(), ButtonType.OK);
			alert.setTitle("Lets Play Applicatie");
			alert.setHeaderText("Er is een fout opgetreden!");
			alert.showAndWait();
			System.out.println(se.getMessage());

			return false;

		} finally {
			try {
				con.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}

		return false;

	}
    
}
