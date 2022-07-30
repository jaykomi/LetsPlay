package com.mycompany.letsplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dataBase.sqlDataSource;
import dataKlassen.Rapportage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kemik
 */
public class RapportageController implements Initializable {

    @FXML
    private TextArea humeur, motivatie, situatie, verbetering, waarnemingen;

    @FXML
    private JFXTextField kindrfid;

    @FXML
     JFXButton verzendRapport;

    @FXML
    private JFXButton sluitButton, miniemButton;
    
    private Connection con;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /* sluit de nieuwe geladen stage
    
    */
    @FXML
    public void sluitApplicatie(ActionEvent event) {
        Stage stage = (Stage) sluitButton.getScene().getWindow();
        stage.close();
    }
    
    /* verzend het opgebouwde rapport naar de database zodat 
    het opgeslagen kan worden. Wordt gekoppeld aan het juiste kind
    doordat de kindrfid bij scannen mee wordt gestuurd met de nieuwe scene
    */

    @FXML
    public void verzendRapport(ActionEvent event) {
        
        Rapportage rap = new Rapportage();
        int result = 0;

        // Controleert voor lege velden en blokkeert invoer. 
        if (humeur.getText().isEmpty() | motivatie.getText().isEmpty()) {
            Alert alertLeeg = new Alert(Alert.AlertType.ERROR, "Velden humeurverloop & motivatie mogen niet leeg zijn", ButtonType.OK);
            alertLeeg.setTitle("Let's Play applicatie");
            alertLeeg.setHeaderText("Foutmelding!");
            alertLeeg.showAndWait();
        
            // bovenstaande mogelijkheden niet voorgekomen. start met de rest van de transactie.     
        } else {

            try {
                rap.setKindRFID(kindrfid.getText());
                rap.setHumeurVerloop(humeur.getText());
                rap.setMotivatie(motivatie.getText());
                rap.setSituatie(situatie.getText());
                rap.setVerbetering(verbetering.getText());
                rap.setOverigeWaarnemingen(waarnemingen.getText());
                /*
		* Maak verbinding met de database en insert in de
		* tabel kindaccount de gegevens uit de variabelen.
                 */
                con = sqlDataSource.getConnection();
                PreparedStatement stat = con.prepareStatement(
                        "INSERT INTO rapportage(kindRFID, humeurverloop, motivatie, situatie, verbetering, overigewaarnemingen) VALUES(?,?,?,?,?,?)");
                stat.setString(1, rap.getKindRFID());   
                stat.setString(2, rap.getHumeurVerloop());
                stat.setString(3, rap.getMotivatie());
                stat.setString(4, rap.getSituatie());
                stat.setString(5, rap.getVerbetering());
                stat.setString(6, rap.getOverigeWaarnemingen());

                result = stat.executeUpdate();
            } catch (SQLException e) {
                /*
	    vang de errors op 
                 */
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Foutmelding" + e + ButtonType.OK);
                errorAlert.setTitle("Let's Play applicatie");
                errorAlert.setHeaderText("Foutmelding!");
                errorAlert.showAndWait();
            } finally {
                // als bovenstaande taken volbracht zijn:
                try {
                    /*
		probeer de connectie naar de database te sluiten
                     */
                    con.close();
                } catch (SQLException se) {

                    Alert alert3 = new Alert(Alert.AlertType.ERROR, "Foutmelding: " + se + ".", ButtonType.OK);
                    alert3.setTitle("Let's Play applicatie");
                    alert3.setHeaderText("Foutmelding!");
                    alert3.showAndWait();
                }
            }
            // als alle taken gelukt zijn, presenteer beheerder met een alert dat de
            // ingevoerde kind gegevens toegevoegd zijn aan de database.

            if (result == 1) {
                Alert alert4 = new Alert(Alert.AlertType.INFORMATION,
                        "Rapportage toegevoegd aan kind met RFID: " + rap.getKindRFID() + ".", ButtonType.OK);
                alert4.setTitle("Let's Play applicatie");
                alert4.setHeaderText("Informatie!");
                alert4.showAndWait();

                Stage stage = (Stage) verzendRapport.getScene().getWindow();
                stage.close();
            }
        
    }
    }

    // minimalizeer de applicatie
    @FXML
    public void miniemApplicatie(ActionEvent event) {
        Stage stage = (Stage) miniemButton.getScene().getWindow();
        stage.setIconified(true);
    }
    
    /*
    laad de opgehaalde data in observable list 
    */
    public void displayData(ObservableList l) {
        kindrfid.setText(l.get(0).toString());
        humeur.setText(l.get(2).toString());
        motivatie.setText(l.get(3).toString());
        situatie.setText(l.get(4).toString());
        verbetering.setText(l.get(5).toString());
        waarnemingen.setText(l.get(6).toString());
    }
    
    public void displayRFID(String r) {
        kindrfid.setText(r);
    }

}
