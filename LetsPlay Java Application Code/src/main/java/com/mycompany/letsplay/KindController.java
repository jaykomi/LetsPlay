package com.mycompany.letsplay;

import Arduino.Scanner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dataBase.sqlDataSource;
import dataKlassen.Kind;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class KindController implements Initializable {

    @FXML
    private JFXTextField voorNaam, achterNaam, contactpersoon, contactpersoontel, geboortedatum, rfidTag;

    @FXML
    private TextField afbeeldingPad;

    @FXML
    private Pane kindPane;

    @FXML
    private JFXButton pasOphalen, registreerButton, selecteerAfbeelding, kindOphalen;

    @FXML
    private ImageView gebruikerImage;

    private final Scanner s = new Scanner();

    private Connection con;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    /* laat een gebruiker kiezen uit de volgende soort bestanden
    + open de file picker dialog en sla het pad op in het textveld bestandPad
    */

    @FXML
    public void bestandExplorer(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecteer afbeelding");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
        );

        File bestandPad = fileChooser.showOpenDialog(kindPane.getScene().getWindow());

        afbeeldingPad.setText(bestandPad.getPath());
    }

    @FXML
    public void pasDataOphalen(ActionEvent event) {

        s.startSerieleVerbinding(rfidTag);

    }
    
    /* laat data van het kind in object A en weergeef
    deze in de verschillende tekstvelden waaronder het ophalen van de foto. 
    */

    public void kindDataOphalen(ActionEvent event) {

        Kind a = new Kind();

        try {

            Kind k = a.getKindGegegvens(rfidTag.getText());

            DateFormat out = new SimpleDateFormat("dd-MM-yyyy");
            Date kindDatum = k.getGeboorteDatum();
            String s = out.format(kindDatum);

            voorNaam.setText(k.getVoorNaam());
            achterNaam.setText(k.getAchterNaam());
            contactpersoon.setText(k.getContactpersoonNaam());
            contactpersoontel.setText(k.getContactpersoonTelefoon());
            geboortedatum.setText(s);
            afbeeldingPad.setText(k.getProfielfotoPad());

            File file = new File(k.getProfielfotoPad());
            Image image = new Image(file.toURI().toString());

            gebruikerImage.setImage(image);

        } catch (NullPointerException ee) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Geen kind data gevonden.", ButtonType.OK);
            errorAlert.setTitle("Let's Play applicatie");
            errorAlert.setHeaderText("Foutmelding!");
            errorAlert.showAndWait();

        }

    }

    @FXML
    public void kindVerwijderen(ActionEvent event) {

        int result = 0;
        Kind c = new Kind();

        try {
            c.setRFID(rfidTag.getText());

            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement(
                    "DELETE FROM kindaccount WHERE kindRFID = '" + c.getRFID() + "';");
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
                        "Kind met RFID tag: " + c.getRFID() + " verwijderd uit systeem.", ButtonType.OK);
                alert4.setTitle("Let's Play applicatie");
                alert4.setHeaderText("Informatie!");
                alert4.showAndWait();

                // maak de text velden leeg, als soort van tweede bevestiging dat er
                // daadwerkelijk iets gebeurd is met de gegevens.
                voorNaam.clear();
                achterNaam.clear();
                contactpersoon.clear();
                contactpersoontel.clear();
                geboortedatum.clear();
                afbeeldingPad.clear();
                rfidTag.clear();
                gebruikerImage.setImage(null);
            }

        }

    

    @FXML
    public void registreerKind(ActionEvent event) {

        Kind b = new Kind();
        int result = 0;

        // Controleert voor lege velden en blokkeert invoer. 
        if (voorNaam.getText().isEmpty() | achterNaam.getText().isEmpty() | contactpersoon.getText().isEmpty() | contactpersoontel.getText().isEmpty() | geboortedatum.getText().isEmpty() | rfidTag.getText().isEmpty()) {
            Alert alertLeeg = new Alert(Alert.AlertType.ERROR, "Velden mogen niet leeg zijn", ButtonType.OK);
            alertLeeg.setTitle("Let's Play applicatie");
            alertLeeg.setHeaderText("Error!");
            alertLeeg.showAndWait();
        }

        // controleert of het ingevoerde voldoet aan het verwachte namelijk dd-mm-yyyy formaat.
        // als dit niet het geval is krijg ik later parse exception errors.
        boolean resultaat = Kind.isValidEmail(geboortedatum.getText());
        if (!resultaat) {
            Alert alertDatum = new Alert(AlertType.INFORMATION, "Verkeerde datum formaat ingevoerd. Gebruik dd-mm-yyyy!", ButtonType.OK);
            alertDatum.setTitle("Let's Play applicatie");
            alertDatum.setHeaderText("Error!");
            alertDatum.showAndWait();

            // bovenstaande mogelijkheden niet voorgekomen. start met de rest van de transactie.     
        } else {

            try {
                b.setVoorNaam(voorNaam.getText());
                b.setAchterNaam(achterNaam.getText());
                b.setRFID(rfidTag.getText());

                // data type gelijk krijgen met hoe het in sql opgeslagen wordt. yyyy-mm-dd
                java.util.Date utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(geboortedatum.getText());
                java.sql.Date date = new java.sql.Date(utilDate.getTime());

                b.setGeboorteDatum(date);
                b.setContactpersoonNaam(contactpersoon.getText());
                b.setContactpersoonTelefoon(contactpersoontel.getText());
                b.setProfielfotoPad(afbeeldingPad.getText());

                /*
		* Maak verbinding met de database en insert in de
		* tabel kindaccount de gegevens uit de variabelen.
                 */
                con = sqlDataSource.getConnection();
                PreparedStatement stat = con.prepareStatement(
                        "INSERT INTO kindaccount(kindRFID,voornaam ,achternaam,geboortedatum, profielfotopad, contactpersoonnaam, contactpersoontelefoon) VALUES(?,?,?,?,?,?,?)");
                stat.setString(1, b.getRFID());
                stat.setString(2, b.getVoorNaam());
                stat.setString(3, b.getAchterNaam());
                stat.setDate(4, b.getGeboorteDatum());
                stat.setString(5, b.getProfielfotoPad());
                stat.setString(6, b.getContactpersoonNaam());
                stat.setString(7, b.getContactpersoonTelefoon());

                result = stat.executeUpdate();
            } catch (SQLException | ParseException e) {
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
                        "Kind: " + b.getVoorNaam() + " met RFID: " + b.getRFID() + " succesvol geregistreerd", ButtonType.OK);
                alert4.setTitle("Let's Play applicatie");
                alert4.setHeaderText("Informatie!");
                alert4.showAndWait();

                // maak de text velden leeg, als soort van tweede bevestiging dat er
                // daadwerkelijk iets gebeurd is met de gegevens.
                voorNaam.clear();
                achterNaam.clear();
                contactpersoon.clear();
                contactpersoontel.clear();
                geboortedatum.clear();
                afbeeldingPad.clear();
                rfidTag.clear();
                gebruikerImage.setImage(null);
            }

        }
    }
}
