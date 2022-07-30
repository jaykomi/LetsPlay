package Arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.jfoenix.controls.JFXTextField;
import java.io.InputStream;
import java.io.PrintWriter;



public class Scanner {
    
   // data voor de poort + snelheid waarop data uit de arduino opgehaald wordt
    
    String readline;
    public SerialPort comPort;
    public String commPort = "COM4";
    int baudrate = 9600;
    
  
    public Scanner(){
        
    }

    public Scanner(InputStream in) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    // Probeer de verbinding te starten met de variabelen commport en baudrate. 
    // geef feedback aan de hand van data die uitgelezen wordt of niet
    
     public void startSerieleVerbinding(JFXTextField rfidTag) {
         
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
                                ontvangData(readline, rfidTag);
                               
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
     
     // sluit de verbinding zodat deze in een andere scene weer gebruikt / geopend kan worden
    
    public void closeSerieleVerbinding(){
        comPort.closePort();
    }
    
    /*
    als er data over de verbinding heen loopt die begint met Card UID
    stuur deze dan door naar de RFIDscan functie die de variabele returnt. 
    */

    // wordt opgeroepen zodra er data over de seriele poort verschijnt. 
    public void ontvangData(String line, JFXTextField rfidTag) {
        if (line == null) {
            return;
        }
        // controleer of er data aankomt die begint met Card UID
        // sluit de verbinding zodra de data door is gestuurd naar de functie rfidscan.
        if (line.startsWith("Card UID")) {
           String output = rfidScan(line.substring(10).trim());
           rfidTag.setText(output);
           closeSerieleVerbinding();
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
    
    // stuur de rfid tag terug naar waar die opgeroepen wordt. 
    // + extra sluit verbinding

    public String rfidScan(String id)  {

        System.out.println("rfid=" + id + ".");
        closeSerieleVerbinding();
        return id;  

           
}


}
