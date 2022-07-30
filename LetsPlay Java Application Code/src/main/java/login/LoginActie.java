/**
 * @author      Jeremy Doeve <j.doeve1@student.avans.nl>
 * @version 1.0
 * @since 1.0
 *
 */
package login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import dataBase.sqlDataSource;

public class LoginActie {

    private static Connection con;
    
    /*
    spreek de functie aan en stuur een gebruikersnaam en wachtwoord mee. 
    Komt de data voor in de database return dan de boolean waarde true zo niet return false. 
    */

    public static boolean login(String gebruikersNaam, String wachtWoord) {
         
        try {
            con = sqlDataSource.getConnection();
            Statement stat = con.createStatement();
            ResultSet result = stat.executeQuery(
                    "select gebruikersnaam, wachtwoord from begeleideraccount WHERE gebruikersnaam = '"
                    + gebruikersNaam + "' AND wachtwoord = '" + wachtWoord + "'");

            while (result.next()) {
                LoginSessie.gebruikersNaam = result.getString("gebruikersnaam");
                LoginSessie.gebruikersWachtwoord = result.getString("wachtwoord");

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

};
