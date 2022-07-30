package dataKlassen;

import dataBase.sqlDataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Rapportage {

    private String kindRFID, humeurVerloop, motivatie, situatie, verbetering, overigeWaarnemingen;
    private Date spelDatum;

    private Connection con;

    public Rapportage() {

    }

    public String getKindRFID() {
        return kindRFID;
    }

    public void setKindRFID(String kindRFID) {
        this.kindRFID = kindRFID;
    }

    public String getHumeurVerloop() {
        return humeurVerloop;
    }

    public void setHumeurVerloop(String humeurVerloop) {
        this.humeurVerloop = humeurVerloop;
    }

    public String getMotivatie() {
        return motivatie;
    }

    public void setMotivatie(String motivatie) {
        this.motivatie = motivatie;
    }

    public String getSituatie() {
        return situatie;
    }

    public void setSituatie(String situatie) {
        this.situatie = situatie;
    }

    public String getVerbetering() {
        return verbetering;
    }

    public void setVerbetering(String verbetering) {
        this.verbetering = verbetering;
    }

    public String getOverigeWaarnemingen() {
        return overigeWaarnemingen;
    }

    public void setOverigeWaarnemingen(String overigeWaarnemingen) {
        this.overigeWaarnemingen = overigeWaarnemingen;
    }

    public Date getSpelDatum() {
        return spelDatum;
    }

    public void setSpelDatum(Date spelDatum) {
        this.spelDatum = spelDatum;
    }

    public ArrayList getRapporten(String kin) {
        ArrayList<Date> data = new ArrayList<>();
        try {

            // maak verbinding met de database en selecteer de data uit iedere kolom uit de
            // tabel statussen.
            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement("select speeldatum from rapportage where kindRFID = '" + kin + "';");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                Date gameDatum = res.getDate(1);

                data.add(gameDatum);

            }

        } catch (SQLException se) {
            /*
			 * mocht er iets mis gaan bij het verbinden met de database of ophalen van de
			 * gegevens, geef een foutmelding in de console.
             */
            System.out.println(se.getMessage());
        } finally {
            try {
                // probeer de verbinding te sluiten.
                con.close();
            } catch (SQLException se) {
                // mocht er iets mis gaan bij het afsluiten, geef een foutmelding in de console.
                System.out.println(se.getMessage());
            }
        }
        return data;
    }
    
    public ObservableList getKindRap(String r, Date d) {
        ObservableList data = FXCollections.observableArrayList();
        
        Rapportage ra = new Rapportage();

        try {

            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement("select * from rapportage where kindRFID = '" + r + "' AND speeldatum = '" + d + "';");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                ra.setKindRFID(res.getString(1));
                ra.setSpelDatum(res.getDate(2));
                ra.setHumeurVerloop(res.getString(3));
                ra.setMotivatie(res.getString(4));
                ra.setSituatie(res.getString(5));
                ra.setVerbetering(res.getString(6));
                ra.setOverigeWaarnemingen(res.getString(6));

                data.add(ra.getKindRFID());
                data.add(ra.getSpelDatum());
                data.add(ra.getHumeurVerloop());
                data.add(ra.getMotivatie());
                data.add(ra.getSituatie());
                data.add(ra.getVerbetering());
                data.add(ra.getOverigeWaarnemingen());
             
               
            }

        } catch (SQLException se) {
            /*
			 * mocht er iets mis gaan bij het verbinden met de database of ophalen van de
			 * gegevens, geef een foutmelding in de console.
             */
            System.out.println(se.getMessage());
        } finally {
            try {
                // probeer de verbinding te sluiten.
                con.close();
            } catch (SQLException se) {
                // mocht er iets mis gaan bij het afsluiten, geef een foutmelding in de console.
                System.out.println(se.getMessage());
            }
        }
        return data;
    }
    

}
