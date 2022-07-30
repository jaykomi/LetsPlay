package dataKlassen;

import dataBase.sqlDataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;


public class Kind extends Persoon {
	
	private String  profielfotoPad, contactpersoonNaam, contactpersoonTelefoon;
	private Date geboorteDatum;
        
        Connection con;
	
	public Kind() {
		
	}
	
	public String getProfielfotoPad() {
		return profielfotoPad;
	}
	public void setProfielfotoPad(String profielfotoPad) {
		this.profielfotoPad = profielfotoPad;
	}
	public String getContactpersoonNaam() {
		return contactpersoonNaam;
	}
	public void setContactpersoonNaam(String contactpersoonNaam) {
		this.contactpersoonNaam = contactpersoonNaam;
	}
	public String getContactpersoonTelefoon() {
		return contactpersoonTelefoon;
	}
	public void setContactpersoonTelefoon(String contactpersoonTelefoon) {
		this.contactpersoonTelefoon = contactpersoonTelefoon;
	}
	public Date getGeboorteDatum() {
		return geboorteDatum;
	}
	public void setGeboorteDatum(Date geboorteDatum) {
		this.geboorteDatum = geboorteDatum;
	}
        
      public static boolean isValidEmail(String date) {
        {
            // voldoet de geboortedatum aan de gestelde eisen?
            String regex = "^([0]?[1-9]|[1|2][0-9]|[3][0|1])[./-]([0]?[1-9]|[1][0-2])[./-]([0-9]{4}|[0-9]{2})$";
            Pattern pattern = Pattern.compile(regex);
            if (date == null)
                return false;
            return pattern.matcher(date).matches();
        }
    }
        
        public Kind getKindGegegvens(String r) {
        
            Kind k = new Kind();
	  
        try {

            // maak verbinding met de database en selecteer de data uit iedere kolom uit de
            // tabel statussen.
            con = sqlDataSource.getConnection();
            
            PreparedStatement stat = con.prepareStatement("select * from kindaccount where kindRFID = '" + r + "';");

            ResultSet res = stat.executeQuery();

            while (res.next()) {
                k.setRFID(res.getString(1));
                k.setVoorNaam(res.getString(2));
                k.setAchterNaam(res.getString(3));
                k.setGeboorteDatum(res.getDate(4));
                k.setProfielfotoPad(res.getString(5));
                k.setContactpersoonNaam(res.getString(6));
                k.setContactpersoonTelefoon(res.getString(7));
                
            }

        } catch (SQLException | NullPointerException se) {
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
        return k;
    }
        
}
