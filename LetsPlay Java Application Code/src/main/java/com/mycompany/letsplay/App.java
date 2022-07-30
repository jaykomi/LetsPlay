package com.mycompany.letsplay;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {
	
	   private double xSet = 0;
	   private double ySet = 0;
	    
	@Override
	public void start(Stage primaryStage) {
		try {
			StackPane root = (StackPane)FXMLLoader.load(getClass().getResource("Login.fxml"));

			/* 
			 Wanneer met de muis geklikt wordt haal hij de X en Y coordinaten van de applicatie op dat moment op
			 */
	        root.setOnMousePressed((MouseEvent event) -> {
                    xSet = event.getSceneX();
                    ySet = event.getSceneY();
                        });
	        

			/* 
			 Zodra er met de muis de applicatie versleept wordt beweeg de applicatie dan naar de coordinaten van dat moment. 
			 */
	        root.setOnMouseDragged((MouseEvent event) -> {
                    primaryStage.setX(event.getScreenX() - xSet);
                    primaryStage.setY(event.getScreenY() - ySet);
                        });
	        
			Scene scene = new Scene(root);
			
			// laad het css bestand in
			scene.getStylesheets().add(getClass().getResource("LetsPlayStyleSheet.css").toExternalForm());
			
			// zorg er voor dat de primarystage geen windows borders krijgt. 
			primaryStage.initStyle(StageStyle.UNDECORATED);
		      
                        
			primaryStage.setScene(scene);       
			primaryStage.show();
		} catch(IOException e) {
                    System.out.println("fout " + e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
