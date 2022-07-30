package com.mycompany.letsplay;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.LoginActie;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import dataKlassen.Begeleider;
import java.io.PrintWriter;
import javafx.scene.control.Label;
import login.HashingActivity;
import login.LoginActieRFID;

public final class LoginUIController {

    @FXML
    private JFXButton closeButton, minimizeButton;
    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXPasswordField gebruikersWachtwoord;
    @FXML
    private JFXTextField gebruikersNaam;
    @FXML
    public Label labelMelding;

    // rootPane = ID van de pane op dit moment.
    @FXML
    private StackPane rootPane;

    // Data voor serial port communicatie. Zal vervangen moeten worden zodra BT
    // gebruikt wordt.
    String readline;
    SerialPort comPort;
    String commPort = "COM4";
    int baudrate = 9600;

    // Variabelen voor het verschuiven van de X en Y as.
    private double xSet = 0;
    private double ySet = 0;

    @FXML
    public void closeApplicationAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void minimizeApplicationAction(ActionEvent event) {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML // Functionaliteit voor inloggen + het laden van de nieuwe Pane.
    public void userLoginAction(ActionEvent event) throws IOException {

        String gebruikersNaamStr = gebruikersNaam.getText();
        // functie vervangen door passwordHashed
        String wachtWoordStr = gebruikersWachtwoord.getText();

        boolean resultaat = Begeleider.isValidPassword(wachtWoordStr);
        if (!resultaat) {
            labelMelding.setText("Wachtwoord incorrect!");

        } else {

            try {
                String passwordHashed = HashingActivity.main(wachtWoordStr);

                // test code voor testen password hashing functie
                //System.out.println("gehashte wachtwoord " + passwordHashed);
                
                if (LoginActie.login(gebruikersNaamStr, passwordHashed)) {
                    RedirectLogin();
                     closeSerieleVerbinding();

                } else {
                    labelMelding.setText("Combinatie van gebruikersnaam en wachtwoord fout");
                }
            } catch (IOException se) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Foutmelding" + se, ButtonType.OK);
                alert.setTitle("Lets Play Applicatie");
                alert.setHeaderText("Er is een fout opgetreden!");
                alert.showAndWait();
                System.out.println(se.getMessage());
            } finally {
                gebruikersNaam.clear();
                gebruikersWachtwoord.clear();

            }
        }
    }

    public void RedirectLogin() throws IOException {

        // Sluit de huidige scene af voordat er in een try catch consutrctie de nieuwe
        // FXML scene word gelaten.
        ((Stage) rootPane.getScene().getWindow()).close();
        try {

            // De reden voor een nieuwe scene in zijn geheel is het feit dat de pixelgrootte veranderd. 
            Parent root = FXMLLoader.load(getClass().getResource("HoofdMenu.fxml"));
            Stage secondaryStage = new Stage();

            /*
			 * Wanneer met de muis geklikt wordt haal hij de X en Y coordinaten van de
			 * applicatie op dat moment op
             */
            root.setOnMousePressed((MouseEvent event) -> {
                xSet = event.getSceneX();
                ySet = event.getSceneY();
            });

            /*
			 * Zodra er met de muis de applicatie versleept wordt beweeg de applicatie dan
			 * naar de coordinaten van dat moment.
             */
            root.setOnMouseDragged((MouseEvent event) -> {
                secondaryStage.setX(event.getScreenX() - xSet);
                secondaryStage.setY(event.getScreenY() - ySet);
            });

            Scene sceneMainMenu = new Scene(root);

            // sceneMainMenu.getStylesheets().add(getClass().getResource("/application/LetsPlayStyleSheet.css").toExternalForm());
            // zorg er voor dat de primarystage geen windows borders krijgt. 
            secondaryStage.initStyle(StageStyle.UNDECORATED);
            secondaryStage.setScene(sceneMainMenu);
            secondaryStage.show();

        } catch (NullPointerException e) {
            System.out.println(e);
        }

    }

    public LoginUIController() {

        startSerieleVerbinding();

    }

    // start het binnenlaten van data over de seriel poort
    public void startSerieleVerbinding() {

        System.out.println("Verbinding met " + commPort + " met baudrate " + baudrate);
        comPort = SerialPort.getCommPort(commPort);
        comPort.openPort();
        comPort.setBaudRate(baudrate);
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                // als er geen data beschikbaar komt print bericht.
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    System.err.println("Geen data over poort");
                    return;
                }
                int dataUit = comPort.bytesAvailable();
                if (dataUit < 1) {
                    System.err.println("Kan geen data uitlezen");
                    return;
                }
                byte[] data = new byte[dataUit];
                int dataInt = comPort.readBytes(data, data.length);
                if (dataInt > 0) {
                    for (int i = 0; i < data.length; ++i) {
                        if ((char) data[i] == '\n' || (char) data[i] == '\r') {
                            readline = readline.trim();
                            if (readline.length() > 0) {
                                ontvangData(readline);

                            }
                            readline = "";
                        } else {
                            readline = readline + (char) data[i];
                        }
                    }
                }
            }
        });
    }

    public void closeSerieleVerbinding() {
        comPort.closePort();
    }

    // wordt opgeroepen zodra er data over de seriele poort verschijnt. 
    public void ontvangData(String line) {
        if (line == null) {
            return;
        }
        // controleer of er data aankomt die begint met Card UID
        if (line.startsWith("Card UID")) {
            rfidScan(line.substring(10).trim());
        }
    }

    public void send(String s) {
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 100, 0);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
        }
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        pout.print(s);
        pout.flush();
    }

    public void rfidScan(String id) {

        System.out.println("rfid=" + id + ".");
        loginRFID(id);

    }

    public void loginRFID(String RFID) {
        System.out.println(RFID);
        Thread.currentThread().interrupt(); //belangrijk om het bericht te behouden

        if (LoginActieRFID.loginRFID(RFID)) {
            try {
                Integer i = 1;
                comPort.getOutputStream().write(i.byteValue());
                comPort.getOutputStream().flush();

                // stel de taak uit anders crasht de thread vanwege een interrupted exception
                Platform.runLater(() -> {

                    try {
                        closeSerieleVerbinding();
                        RedirectLogin();

                    } catch (IOException ex) {
                        Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                Integer i = 2;
                comPort.getOutputStream().write(i.byteValue());
                comPort.getOutputStream().flush();

                Platform.runLater(() -> {
                    try {
                        labelMelding.setText("RFID pas niet herkend!");
                    } catch (Exception ex) {
                        Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                });
            } catch (IOException ex) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
