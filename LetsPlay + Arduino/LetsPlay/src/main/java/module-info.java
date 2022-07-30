module com.mycompany.letsplay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.jfoenix;
    requires de.jensd.fx.glyphs.fontawesome;
    requires de.jensd.fx.glyphs.commons;
    requires commons.dbcp2;
    requires commons.logging;
    requires commons.pool2;
    requires mysql.connector.java;
    requires java.management;
    requires com.fazecast.jSerialComm;
    requires java.base;
    requires activation;
    requires java.mail;
    requires javafx.media;
    
    
    opens com.mycompany.letsplay to javafx.fxml;
    exports com.mycompany.letsplay;
    
  }
    

