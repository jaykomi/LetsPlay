package com.mycompany.letsplay;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import dataKlassen.ResultaatLetsPlay;
import dataKlassen.dataOntvangen;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class GameController implements Initializable {

    @FXML
    private JFXButton blueButton, greenButton, redButton, yellowButton, nieuwButton;

    @FXML
    public ImageView imageBlauw, imageGeel, imageRood, imageGroen, eindeImage;

    @FXML
    public TextField kindTag;

    @FXML
    public Pane gamePane;
    
    // antwoorden die doorgestuurd worden naar de master
    String antwoordRood = "rood ";
    String antwoordGeel = "geel ";
    String antwoordGroen = "groen";
    String antwoordBlauw = "blauw";

    // bepaalde welke ronde gespeeld wordt
    int counter = 0;

    // verbinding met de master die de individuele boxen aanstuurt
    String readline;
    public SerialPort comPort;
    public String commPort = "COM8";
    int baudrate = 9600;

    // listener op setter van die aangesproken wordt zodra er data binnen komt via master arduino
    public dataOntvangen d;

    // images voor het spel
    Image imageAard = new Image("file:C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\aardbei.png");
    Image imageWort = new Image("file:C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\wortel.png");
    Image imageBan = new Image("file:C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\banaan.png");
    Image imageApp = new Image("file:C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\appel.png");
    Image imageTom = new Image("file:C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\tomaat.png");
    Image einde = new Image("file:C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\einde.png");

    // audio voor het spel 
    Media antwoordApp = new Media(new File("C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\Vraag 1 Appel.M4A").toURI().toString());
    Media antwoordBan = new Media(new File("C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\Vraag 2 Banaan.M4A").toURI().toString());
    Media antwoordTom = new Media(new File("C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\Vraag 3 tomaat.M4A").toURI().toString());
    Media antwoordAar = new Media(new File("C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\Vraag 4 aardbei.M4A").toURI().toString());
    Media antwoordWor = new Media(new File("C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\Vraag 5 wortel.M4A").toURI().toString());
    Media eindeGame = new Media(new File("C:\\Users\\kemik\\Desktop\\applicatie afbeeldingen\\celebrating.mp3").toURI().toString());

    MediaPlayer mediaPlayer;
    
    // speeltijd die omgerekend wordt
    long startMilli;
    long endMilli;
    long gameMilli;
    
    
    // database aansprak met object a
    ResultaatLetsPlay a = new ResultaatLetsPlay();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        d = new dataOntvangen(this) {
        };

        startSerieleVerbinding();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            stelVraag(counter);
            startMilli = System.currentTimeMillis();
        }

    }

    /* verbergt de knoppen aan het einde van ronde 
    5 zodat de gebruiken kan beslissen om een nieuwe ronde te starten of te stoppen
    */
    public void verbergKnoppen() {
        blueButton.setVisible(false);
        greenButton.setVisible(false);
        redButton.setVisible(false);
        yellowButton.setVisible(false);
        imageBlauw.setImage(null);
        imageGeel.setImage(null);
        imageRood.setImage(null);
        imageGroen.setImage(null);
        nieuwButton.setVisible(true);

    }
    
/*
    functie die aangesproken wordt zodra de master een antwoord van een van de boxen 
    terug krijgt. Dit kan alleen voorkomen als het juiste antwoordt ingedrukt wordt
    */
    public void wachtOpAntwoord() {

        try {

           
            afhandelen(counter);
            counter = counter + 1;
            System.out.println("" + counter);

            if (counter == 5) {
                counter = 0;
                endMilli = System.currentTimeMillis();
                registreerResultaat();
                verbergKnoppen();
                speelAnimatie();

            } else {
                stelVraag(counter);
            }
        } catch (Exception ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /* animatie aan het eind van de 5de ronde 
    om te laten zien dat de taak goed uitgevoerd is. 
    */
    public void speelAnimatie() {
         mediaPlayer = new MediaPlayer(eindeGame);
         mediaPlayer.play();
        eindeImage.setImage(einde);
        eindeImage.setVisible(true);
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(eindeImage);
        translate.setDuration(Duration.millis(1500));
        translate.setCycleCount(TranslateTransition.INDEFINITE);
        translate.setByX(-380);  
        translate.setAutoReverse(true);
        translate.play();
    }

    /* zorgt er voor dat het spel gereset wordt en weer opnieuw gespeeld
    kan worden. Timer wordt ook gereset.
    */
    @FXML
    public void herstartGame() {
        eindeImage.setVisible(false);
        eindeImage.setDisable(true);
        stelVraag(counter);
        startMilli = System.currentTimeMillis();

        blueButton.setVisible(true);
        greenButton.setVisible(true);
        redButton.setVisible(true);
        yellowButton.setVisible(true);
        nieuwButton.setVisible(false);

    }
    /* De applicatie berekent hier de totale tijd in milliseconden en format deze naar een 
    java sql time value zodat deze opgeslagen kan worden. Daarna worden de variabelen in object a opgeslagen
    en wordt de functie verzendresultaat aangesproken in ResultaatLetsPlay.
    */

    public void registreerResultaat() {

        try {
            gameMilli = endMilli - startMilli;

            double kiloCalorien = gameMilli * 0.000083;
            int value = (int) kiloCalorien;

            String hmsS;
            hmsS = String.format("%02d:%02d:%02d.%03d", TimeUnit.MILLISECONDS.toHours(gameMilli),
                    TimeUnit.MILLISECONDS.toMinutes(gameMilli) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(gameMilli)),
                    TimeUnit.MILLISECONDS.toSeconds(gameMilli) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(gameMilli)),
                    TimeUnit.MILLISECONDS.toMillis(gameMilli) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(gameMilli))
            );

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

            java.sql.Time timeValue = new java.sql.Time(formatter.parse(hmsS).getTime());

            a.setKindRFID(kindTag.getText());
            a.setTotaleSpeelsessieTijd(timeValue);
            a.setKiloCalorien(value);

            a.verzendResultaat(a);

        } catch (ParseException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* stuur de eerste keer de antwoord van het vraag op naar de master
    daarna laad een nieuwe media bestand en speel deze af en laad de nieuwe 
    afbeeldingen 
    */

    public void stelVraag(int i) {
        switch (i) {
            case 0:
                
                try {
                comPort.getOutputStream().write(antwoordBlauw.getBytes(), 0, antwoordBlauw.length());
                comPort.getOutputStream().flush();

            } catch (IOException e) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, e);
            }
            Platform.runLater(() -> {
                mediaPlayer = new MediaPlayer(antwoordApp);
                mediaPlayer.play();
                imageBlauw.setImage(imageApp);
                imageGeel.setImage(imageTom);
                imageRood.setImage(imageBan);
                imageGroen.setImage(imageWort);
            });
            break;

            case 1:

                Platform.runLater(() -> {

                    try {
                        mediaPlayer = new MediaPlayer(antwoordBan);
                        mediaPlayer.play();
                        imageBlauw.setImage(imageApp);
                        imageGeel.setImage(imageAard);
                        imageRood.setImage(imageBan);
                        imageGroen.setImage(imageTom);
                    } catch (NullPointerException e) {
                    }
                });
                break;

            case 2:
                Platform.runLater(() -> {

                    try {
                        mediaPlayer = new MediaPlayer(antwoordTom);
                        mediaPlayer.play();
                        imageBlauw.setImage(imageTom);
                        imageGeel.setImage(imageApp);
                        imageRood.setImage(imageWort);
                        imageGroen.setImage(imageAard);
                    } catch (NullPointerException e) {
                    }
                });
                break;

            case 3:
                Platform.runLater(() -> {

                    try {
                        mediaPlayer = new MediaPlayer(antwoordAar);
                        mediaPlayer.play();

                        imageBlauw.setImage(imageWort);
                        imageGeel.setImage(imageTom);
                        imageRood.setImage(imageBan);
                        imageGroen.setImage(imageAard);
                    } catch (NullPointerException e) {
                    }
                });
                break;

            case 4:
                Platform.runLater(() -> {

                    try {
                        mediaPlayer = new MediaPlayer(antwoordWor);
                        mediaPlayer.play();

                        imageBlauw.setImage(imageAard);
                        imageGeel.setImage(imageWort);
                        imageRood.setImage(imageApp);
                        imageGroen.setImage(imageTom);
                    } catch (NullPointerException e) {
                    }
                });
                break;

            default:
                break;
        }
    }
    
    /* stuur de nieuwe antwoord van de vraag op naar de master.
    
    */

    public void afhandelen(int i) {
        switch (i) {
            case 0:
               
                               try {
                Thread.sleep(2500);
                comPort.getOutputStream().write(antwoordRood.getBytes(), 0, antwoordRood.length());
                comPort.getOutputStream().flush();

            } catch (IOException | InterruptedException e) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, e);
            }

            break;
            case 1:
                
                try {
                Thread.sleep(2500);
                comPort.getOutputStream().write(antwoordBlauw.getBytes(), 0, antwoordBlauw.length());
                comPort.getOutputStream().flush();

            } catch (IOException | InterruptedException e) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, e);
            }
            break;
            case 2:
                
                   try {
                Thread.sleep(2500);
                comPort.getOutputStream().write(antwoordGroen.getBytes(), 0, antwoordGroen.length());
                comPort.getOutputStream().flush();

            } catch (IOException | InterruptedException e) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, e);
            }
            break;

            case 3:
                
                                               try {
                Thread.sleep(2500);
                comPort.getOutputStream().write(antwoordGeel.getBytes(), 0, antwoordGeel.length());
                comPort.getOutputStream().flush();

            } catch (IOException | InterruptedException e) {
                Logger.getLogger(LoginUIController.class.getName()).log(Level.SEVERE, null, e);
            }
            break;
            case 4:

                break;
            default:
                break;
        }
    }

    /* maak de seriele verbinding op dezelfde manier als met de scanner alleen wanneer er
    data binnenkomt spreek dan de de seton object listener op object b aan zodat de 
    wachtopantwoord functie aangesproken kan worden. 
    */
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
                                d.test();
                                d.setBericht("a");
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

    // sluit de verbinding
    public void closeSerieleVerbinding() {
        comPort.closePort();
    }

    // zet de opgehaalde pas data in het textveld
    public void displayRFID(String r) {
        kindTag.setText(r);
    }

}
