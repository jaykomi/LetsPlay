package com.mycompany.letsplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import dataKlassen.ResultaatLetsPlay;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class HoofdMenuController implements Initializable {

    @FXML
    private JFXButton minimizeButton, knopData, knopHome, knopRegistreer, knopStart, knopVergrendel, verzendFeedbackKnop;

    @FXML
    private Pane dataPane, rootMenuPane;

    @FXML
    private BarChart<String, Integer> hoofdmenuBarChart;

    @FXML
    private Label datumTijdLabel, totaalCalorieen, totaalKinderen, totaalSpeelsessie, totaalSpeeltijd;

    @FXML
    private BorderPane hoofdMenuPane;

    @FXML
    private JFXTextField onderwerpTekst;

    @FXML
    private JFXTextArea feedbackTekst;

    private double xSet = 0;
    private double ySet = 0;

    ExecutorService threadPool = Executors.newWorkStealingPool();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datumTijd();
        laadKindTotaal();
        laadWeekTotaalData();
        laadWeekTotaalCalorie();
    }

    private void laadWeekTotaalData() {

        /* hier controleer ik of een van de velden leeg is of niet
        geen een van de data kan namelijk bestaan zonder dat de ander bestaat
        dus ik kan op een Exception catchen. 
         */
        try {
            ResultaatLetsPlay re = new ResultaatLetsPlay();

            ObservableList dataTotaal = re.getTotaalData();

            totaalSpeelsessie.setText(dataTotaal.get(0).toString());
            totaalSpeeltijd.setText(dataTotaal.get(1).toString());
            totaalCalorieen.setText(dataTotaal.get(2).toString());

        } catch (Exception e) {

        }
    }

    private void laadKindTotaal() {

        try {
            ResultaatLetsPlay res = new ResultaatLetsPlay();

            ObservableList kindTotaal = res.getTotaalKind();

            totaalKinderen.setText(kindTotaal.get(0).toString());

        } catch (Exception e) {

        }
    }

    private void laadWeekTotaalCalorie() {

        /* hier controleer ik of een van de velden leeg is of niet
        geen een van de data kan namelijk bestaan zonder dat de ander bestaat
        dus ik kan op een Exception catchen. 
         */
        try {

            ResultaatLetsPlay r = new ResultaatLetsPlay();

            ObservableList<XYChart.Data<String, Integer>> dataWeek = r.getWeekTotaal();
            XYChart.Series<String, Integer> series = new XYChart.Series<>(dataWeek);

            hoofdmenuBarChart.getData().addAll(series);

        } catch (Exception e) {

        }

    }

    // weergegven van de huidige datum voor de gebruiker
    private void datumTijd() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();

        datumTijdLabel.setText(formatter.format(date));

    }

    @FXML
    public void closeApplicationAction(ActionEvent event) {
        Platform.exit();
    }

    // start de mail server en verzend feedback naar huidige email. 
    @FXML
    public void verzendFeedback(ActionEvent event) {

        // code voor de mail server (Niet zelf geschreven maar online gevonden en aangepast voor eigen gebruik)
        String naar = "j.doeve1@student.avans.nl";
        String van = "danjan181159@yahoo.com";
        String host = "smtp.mail.yahoo.com";

        //Get the session object  
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("danjan181159", "erfzptnsdvhpyeew");
            }
        });

        if (onderwerpTekst.getText().isEmpty() || feedbackTekst.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Foutmelding", ButtonType.OK);
            alert.setTitle("Let's Play Applicatie");
            alert.setHeaderText("Onderwerp of feedback mag niet leeg zijn !");
            alert.showAndWait();
        } else {

            try {

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(van));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(naar));
                message.setSubject(onderwerpTekst.getText());
                message.setText(feedbackTekst.getText());

                // Send message  
                Transport.send(message);
                Alert alertVerzend = new Alert(Alert.AlertType.INFORMATION, "Verzonden", ButtonType.OK);
                alertVerzend.setTitle("Let's Play Applicatie");
                alertVerzend.setHeaderText("U bericht is verzonden. Bedankt voor uw feedback!");
                alertVerzend.showAndWait();
                onderwerpTekst.clear();
                feedbackTekst.clear();

            } catch (MessagingException mex) {
            }
        }
    }

    // minimalizeer de huidige scene. 
    @FXML
    public void minimizeApplicationAction(ActionEvent event) {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    // Systeem voor het wisselen van panes op de scene. 
    @FXML
    public void menuItemClick(ActionEvent event) throws Exception {
        
      
        if (event.getSource() == knopHome) { 
            Pane newLoadedPane = (Pane) FXMLLoader.load(getClass().getResource("HoofdMenuPane.fxml"));
             rootMenuPane.getChildren().clear();  
            rootMenuPane.getChildren().setAll(newLoadedPane);
           
        }
        if (event.getSource() == knopStart) {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("StartschermGame.fxml"));
            rootMenuPane.getChildren().clear();
            rootMenuPane.getChildren().setAll(newLoadedPane);
        }
        if (event.getSource() == knopRegistreer) {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("Kind.fxml"));
            rootMenuPane.getChildren().clear();
            rootMenuPane.getChildren().setAll(newLoadedPane);
        }
        if (event.getSource() == knopData) {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("Data.fxml"));
            rootMenuPane.getChildren().clear();
            rootMenuPane.getChildren().setAll(newLoadedPane);
        }

    }

    // vergrenden van de applicatie door te returnen naar het login scherm. 
    @FXML
    public void returnLogin(ActionEvent event) {
        
        ((Stage) hoofdMenuPane.getScene().getWindow()).close();
              
        try {
            StackPane root = (StackPane) FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage backStage = new Stage();
            /* 
			 Wanneer met de muis geklikt wordt haal hij de X en Y coordinaten van de applicatie op dat moment op
             */
            root.setOnMousePressed((MouseEvent eventNew) -> {
                xSet = eventNew.getSceneX();
                ySet = eventNew.getSceneY();
            });


            /* 
			 Zodra er met de muis de applicatie versleept wordt beweeg de applicatie dan naar de coordinaten van dat moment. 
             */
            root.setOnMouseDragged((MouseEvent eventNew) -> {
                backStage.setX(eventNew.getScreenX() - xSet);
                backStage.setY(eventNew.getScreenY() - ySet);
            });

            Scene scene = new Scene(root);

            // laad het css bestand in
            scene.getStylesheets().add(getClass().getResource("LetsPlayStyleSheet.css").toExternalForm());

            // zorg er voor dat de primarystage geen windows borders krijgt. 
            backStage.initStyle(StageStyle.UNDECORATED);

            backStage.setScene(scene);
            backStage.show();
        } catch (IOException e) {
        }
    }
}
