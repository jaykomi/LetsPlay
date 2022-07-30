package dataKlassen;

import dataBase.sqlDataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ResultaatLetsPlay {

    private String kindRFID;
    private Integer sessieID, kiloCalorien;
    private Date startDatum;
    private Time startTijd;
    private Time totaleSpeelsessieTijd;

    Connection con;

    public ResultaatLetsPlay() {

    }

    public String getKindRFID() {
        return kindRFID;
    }

    public void setKindRFID(String kindRFID) {
        this.kindRFID = kindRFID;
    }

    public Integer getSessieID() {
        return sessieID;
    }

    public void setSessieID(Integer sessieID) {
        this.sessieID = sessieID;
    }

    public Integer getKiloCalorien() {
        return kiloCalorien;
    }

    public void setKiloCalorien(Integer kiloCalorien) {
        this.kiloCalorien = kiloCalorien;
    }

    public Date getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(Date startDatum) {
        this.startDatum = startDatum;
    }

    public Time getStartTijd() {
        return startTijd;
    }

    public void setStartTijd(Time startTijd) {
        this.startTijd = startTijd;
    }

    public Time getTotaleSpeelsessieTijd() {
        return totaleSpeelsessieTijd;
    }

    public void setTotaleSpeelsessieTijd(Time totaleSpeelsessieTijd) {
        this.totaleSpeelsessieTijd = totaleSpeelsessieTijd;
    }

    // vage constructie om van de laatste 7 dagen de totaal verbruikte calorieën op te halen
    // dit kan absoluut beter alleen heb geen flauw idee hoe. 
    public ObservableList<XYChart.Data<String, Integer>> getWeekTotaal() {
        ObservableList<XYChart.Data<String, Integer>> data = FXCollections.observableArrayList();

        try {

            // maak verbinding met de database en selecteer de data uit iedere kolom uit de
            // tabel resultaatletsplay.
            con = sqlDataSource.getConnection();

            PreparedStatement stat = con.prepareStatement("select kilocalorien, sum(case when startdatum = '" + LocalDate.now() + "' then kilocalorien end) as today,"
                    + " sum(case when startdatum = '" + LocalDate.now().minusDays(1) + "' then kilocalorien end) as yesterday,"
                    + " sum(case when startdatum = '" + LocalDate.now().minusDays(2) + "' then kilocalorien end) as twodays,"
                    + " sum(case when startdatum = '" + LocalDate.now().minusDays(3) + "' then kilocalorien end) as threedays,"
                    + " sum(case when startdatum = '" + LocalDate.now().minusDays(4) + "' then kilocalorien end) as fourdays"
                    + " from resultaatletsplay");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                int count = res.getInt("today");
                int count1 = res.getInt("yesterday");
                int count2 = res.getInt("twodays");
                int count3 = res.getInt("threedays");
                int count4 = res.getInt("fourdays");

                data.add(new XYChart.Data<>(LocalDate.now() + "", count));
                data.add(new XYChart.Data<>(LocalDate.now().minusDays(1) + "", count1));
                data.add(new XYChart.Data<>(LocalDate.now().minusDays(2) + "", count2));
                data.add(new XYChart.Data<>(LocalDate.now().minusDays(3) + "", count3));
                data.add(new XYChart.Data<>(LocalDate.now().minusDays(4) + "", count4));

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

    public ObservableList getTotaalData() {
        ObservableList data = FXCollections.observableArrayList();

        try {

            // maak verbinding met de database en selecteer de data uit iedere kolom uit de
            // tabel resultaatletsplay.
            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement("select COUNT(sessieID) as totaalsessie, IFNULL(SEC_TO_TIME(SUM(TIME_TO_SEC(totalespeelsessietijd))), '00:00:00') as totaaltijd, SUM(kilocalorien) as totaalcalorieen, COUNT(DISTINCT(kindRFID)) as totaalkinderen from resultaatletsplay");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                int sessies = res.getInt("totaalsessie");
                String sessieTijd = res.getString("totaaltijd");
                int calorieen = res.getInt("totaalcalorieen");

                data.add(sessies);
                data.add(sessieTijd);
                data.add(calorieen);

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

    public ObservableList getTotaalKind() {
        ObservableList data = FXCollections.observableArrayList();

        try {

            // maak verbinding met de database en selecteer de data uit iedere kolom uit de
            // tabel statussen.
            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement("select COUNT(DISTINCT(kindRFID)) as totaalkinderen from kindaccount");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                int kinderen = res.getInt("totaalkinderen");

                data.add(kinderen);
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

    public ObservableList getKindTijd(String k) {
        ObservableList data = FXCollections.observableArrayList();

        try {

            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement("select Max(totalespeelsessietijd),Min(totalespeelsessietijd),SEC_TO_TIME((TIME_TO_SEC(Max(totalespeelsessietijd))+TIME_TO_SEC(Min(totalespeelsessietijd)))/2) as avgtime, SUM(kilocalorien) as kcal from resultaatletsplay where kindRFID = '" + k + "';");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                String snelTijd = res.getString(1);
                String langTijd = res.getString(2);
                String gemTijd = res.getString(3);
                int kilocal = res.getInt(4);

                data.add(snelTijd);
                data.add(langTijd);
                data.add(gemTijd);
                data.add(kilocal);
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

    public ObservableList<XYChart.Data<String, Number>> getKindTotaal(String ki) {
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();

        try {

            // maak verbinding met de database en selecteer de data uit iedere kolom uit de
            // tabel statussen.
            con = sqlDataSource.getConnection();

            PreparedStatement stat = con.prepareStatement("(select totalespeelsessietijd, sessieID, startdatum from resultaatletsplay where kindRFID = '" + ki + "' ORDER BY sessieID DESC LIMIT 10) ORDER BY sessieID ASC;");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                java.sql.Time tijd = res.getTime(1);
                int sessie = res.getInt(2);
                Date datum = res.getDate(3);

                String stringDate = tijd.toString();

                // het opdelen van de date string in secties die gesplits worden per :
                // dikke vette 10x kut functie
                String[] tijdDeel = stringDate.split(":");

                String sessies = String.valueOf(sessie);

                int minutes = Integer.parseInt(tijdDeel[0]) * 3600 + Integer.parseInt(tijdDeel[1]) * 60 + Integer.parseInt(tijdDeel[2]);

                data.add(new XYChart.Data<>(sessies + "  " + datum, minutes));

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

    public void verzendResultaat(ResultaatLetsPlay p) {

        int result = 0;

        try {

            con = sqlDataSource.getConnection();
            PreparedStatement stat = con.prepareStatement(
                    "INSERT INTO resultaatletsplay(kindRFID, totalespeelsessietijd, kilocalorien) VALUES(?,?,?)");
            stat.setString(1, p.getKindRFID());
            stat.setTime(2, p.getTotaleSpeelsessieTijd());
            stat.setInt(3, p.getKiloCalorien());

            result = stat.executeUpdate();
        } catch (SQLException e) {
            /*
	    vang de errors op 
             */
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Foutmelding" + e + ButtonType.OK);
            errorAlert.setTitle("Let's Play applicatie");
            errorAlert.setHeaderText("Foutmelding!");
            errorAlert.showAndWait();
        } finally {
            // als bovenstaande taken volbracht zijn:
            try {
                /*
		probeer de connectie naar de database te sluiten
                 */
                con.close();
            } catch (SQLException se) {

                Alert alert3 = new Alert(Alert.AlertType.ERROR, "Foutmelding: " + se + ".", ButtonType.OK);
                alert3.setTitle("Let's Play applicatie");
                alert3.setHeaderText("Foutmelding!");
                alert3.showAndWait();
            }
        }

    }

}
