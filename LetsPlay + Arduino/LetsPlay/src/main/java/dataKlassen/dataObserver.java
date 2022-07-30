
package dataKlassen;

import java.util.ArrayList;
import java.util.List;

public class dataObserver {

    public interface Observer {

        void update(String event);
    }

    private final List<Observer> observers = new ArrayList<>();

    private void notifyObservers(String event) {
        observers.forEach(observer -> observer.update(event));
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void berichtWatcher(dataOntvangen c){
        notifyObservers(c.getBericht());
    }
}
