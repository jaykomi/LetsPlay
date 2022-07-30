package com.mycompany.letsplay;

import Arduino.Scanner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author kemik
 */
public class StartschermGameController implements Initializable {

    @FXML
    private MediaView instructieVideo;

    @FXML
    private Pane startschermPane;

    @FXML
    private JFXTextField rfidTag;

    @FXML
    private JFXButton startKnop;

    private Media instructieMedia;
    private File instructieBestand;
    private MediaPlayer videoPlayer;

    private final Scanner sc = new Scanner();

    // Variabelen voor het verschuiven van de X en Y as.
    private double xSet = 0;
    private double ySet = 0;

    @FXML
    void pauzeerInstructie(ActionEvent event) {
        videoPlayer.pause();
    }

    @FXML
    void resetInstructie(ActionEvent event) {
        videoPlayer.seek(Duration.seconds(0));
    }

    @FXML
    void speelInstructie(ActionEvent event) {

        videoPlayer.play();
    }

    public void disposeSpeler() {

        videoPlayer.stop();
    }

    @FXML
    void pasDataOphalen(ActionEvent event) {
        sc.startSerieleVerbinding(rfidTag);
    }

    @FXML
    void startGame(ActionEvent event) {

        videoPlayer.pause();
        try {

            //Pane newLoadedPane = (Pane) FXMLLoader.load(getClass().getResource("Game.fxml"));
            FXMLLoader loaders = new FXMLLoader(
                    getClass().getResource(
                            "Game.fxml"));
              loaders.setController(loaders.getController());
              Parent parent = (Parent) loaders.load();
               
            
            startschermPane.getChildren().clear();


            startschermPane.getChildren().setAll(parent);
  
            GameController controller = loaders.getController();
            controller.displayRFID(rfidTag.getText());

            // De reden voor een nieuwe scene in zijn geheel is het feit dat de pixelgrootte veranderd. 
            FXMLLoader loader = new FXMLLoader(StartschermGameController.this.getClass().getResource("Rapportage.fxml"));
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

            // sceneMainMenu.getStylesheets().add(getClass().getResource("/application/LetsPlayStyleSheet.css").toExternalForm());
            // zorg er voor dat de primarystage geen windows borders krijgt. 
            doubleStage.initStyle(StageStyle.UNDECORATED);
            doubleStage.setScene(sceneMainMenu);
            doubleStage.show();
            doubleStage.setX(50);
            doubleStage.setY(50);
            RapportageController sceneRapportageController = loader.getController();
            sceneRapportageController.displayRFID(rfidTag.getText());

        } catch (NullPointerException | IOException e) {
            System.out.println(e);
        }

    }

    public void mediaSpelen() {
        instructieBestand = new File("C:/Users/kemik/Desktop/applicatie afbeeldingen/Nieuwe video.mp4");
        instructieMedia = new Media(instructieBestand.toURI().toString());
        videoPlayer = new MediaPlayer(instructieMedia);
        

        instructieVideo.setMediaPlayer(videoPlayer);
       
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        mediaSpelen();

        // disable de knop standaard voor het starten van de game
        // pas als pasdata is opgehaald dan ontgrendel het. 
        startKnop.setDisable(true);

        rfidTag.textProperty().addListener((ObservableValue<? extends String> ov, String a, String tag) -> {
            if (rfidTag.getText().isEmpty()) {
                startKnop.setDisable(true);
            } else {
                startKnop.setDisable(false);
            }
        });
    }

    public String displayRFID() {
        return this.rfidTag.getText();
    }

}
