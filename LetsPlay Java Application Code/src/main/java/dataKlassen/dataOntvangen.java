package dataKlassen;

import com.mycompany.letsplay.GameController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class dataOntvangen {


    private String data = "";
    private dataObserver observer;
    private GameController g;
    
    public dataOntvangen(GameController g) {
        this.g = g;
                
    }
    
    public void test(){
    this.observer = new dataObserver();
        observer.addObserver(event -> {
            Platform.runLater(() -> {
                try {
                    g.wachtOpAntwoord();
                } catch (Exception ex) {
                    Logger.getLogger(dataOntvangen.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
    }

    public dataOntvangen(String data) {
        this.observer = new dataObserver();
        this.data = data;
    }

    public String getBericht() {
        return data;
    }

    public void setBericht(String bericht) {
        this.data = data;
        observer.berichtWatcher(this);
    }

}
