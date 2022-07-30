package com.mycompany.letsplay;

import Arduino.Scanner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dataKlassen.Rapportage;
import dataKlassen.ResultaatLetsPlay;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DataController implements Initializable {

    @FXML
    private ListView listRapporten;

    @FXML
    private JFXButton kindOphalen;

    @FXML
    private Label langTijd, snelTijd, gemTijd, totaalVerbruikt;

    @FXML
    private JFXButton pasOphalen;

    @FXML
    private JFXTextField rfidTag;

    @FXML
    private XYChart<String, Number> tijdLineChart;

    // Variabelen voor het verschuiven van de X en Y as.
    private double xSet = 0;
    private double ySet = 0;

    // scan klasse geinstancieerd voor het starten van de scanner.
    private final Scanner sa = new Scanner();
    Rapportage rap = new Rapportage();


    /*
    haal de verschillende resultaten van het kind op en weergeef deze in string vorm / listview / chart
     */
    
    @FXML
    void kindDataOphalen(ActionEvent event) {

        try {
            ResultaatLetsPlay res = new ResultaatLetsPlay();
            Rapportage ra = new Rapportage();

            ObservableList kindTijd = res.getKindTijd(rfidTag.getText());
            ObservableList dbRapportage = FXCollections.observableArrayList(ra.getRapporten(rfidTag.getText()));
            ObservableList<XYChart.Data<String, Number>> dataKind = res.getKindTotaal(rfidTag.getText());
            XYChart.Series<String, Number> series = new XYChart.Series<>(dataKind);

            langTijd.setText(kindTijd.get(0).toString().substring(3));
            snelTijd.setText(kindTijd.get(1).toString().substring(3));
            gemTijd.setText(kindTijd.get(2).toString().substring(3));
            totaalVerbruikt.setText(kindTijd.get(3).toString());

            listRapporten.setItems(dbRapportage);
            tijdLineChart.getData().addAll(series);

        } catch (Exception e) {

        }

        /*
        zodra er op een item uit de listview geklikt wordt laad dan de data die daarbij hoort uit de database op en weergeef deze in 
        de rapport fxml GUI. Waarbij de 2de GUI in zijn eigen scene geladen wordt zodat er tegelijkertijd gebruik van gemaakt
        kan worden
         */
        listRapporten.setOnMouseClicked((MouseEvent eventKlik) -> {
            String datum = listRapporten.getSelectionModel().getSelectedItem().toString();
            Date date = Date.valueOf(datum);
            ObservableList laadRapport = rap.getKindRap(rfidTag.getText(), date);
            try {
                
                FXMLLoader loader = new FXMLLoader(DataController.this.getClass().getResource("Rapportage.fxml"));
                BorderPane root;
                root = loader.load();
                Stage doubleStage = new Stage();
                
                /*
                * Wanneer met de muis geklikt wordt haal hij de X en Y coordinaten van de
                * applicatie op dat moment op
                 */
                root.setOnMousePressed((MouseEvent eventDubbel) -> {
                    xSet = eventDubbel.getSceneX();
                    ySet = eventDubbel.getSceneY();
                });
                /*
                * Zodra er met de muis de applicatie versleept wordt beweeg de applicatie dan
                * naar de coordinaten van dat moment.
                 */
                root.setOnMouseDragged((MouseEvent eventDubbel) -> {
                    doubleStage.setX(eventDubbel.getScreenX() - xSet);
                    doubleStage.setY(eventDubbel.getScreenY() - ySet);
                });
                Scene sceneMainMenu = new Scene(root);
                // zorg er voor dat de primarystage geen windows borders krijgt.
                doubleStage.initStyle(StageStyle.UNDECORATED);
                doubleStage.setScene(sceneMainMenu);
                doubleStage.show();
                
                //zet de controller als die van de Rapportage klasse zodat die functies aangesproken en gebruikt kunnen worden vanuit hier
                RapportageController sceneRapportageController = loader.getController();
                sceneRapportageController.displayData(laadRapport);
                sceneRapportageController.verzendRapport.setDisable(true);
            } catch (NullPointerException | IOException e) {
                System.out.println(e);
            }
        });
    }

    @FXML
    void pasDataOphalen(ActionEvent event) {
        
        // start de seriele verbinding
        sa.startSerieleVerbinding(rfidTag);

        // leeg de mogelijk vooraf opgehaalde rfid tag. 
        rfidTag.clear();

        //leeg de listview zodat nieuw kind data geladen kan worden
        listRapporten.setItems(null);

        // leeg de textfields
        langTijd.setText("");
        snelTijd.setText("");
        gemTijd.setText("");
        totaalVerbruikt.setText("");

        // clear de chart
        tijdLineChart.getData().clear();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

}
