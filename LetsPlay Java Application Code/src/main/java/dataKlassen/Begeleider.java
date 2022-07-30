package dataKlassen;

import java.util.regex.Pattern;

public class Begeleider extends Persoon{
	
	private String gebruikersnaam, wachtwoord, email;
	
	public Begeleider() {
		
	}

	public String getGebruikersnaam() {
		return gebruikersnaam;
	}

	public void setGebruikersnaam(String gebruikersnaam) {
		this.gebruikersnaam = gebruikersnaam;
	}

	public String getWachtwoord() {
		return wachtwoord;
	}

	public void setWachtwoord(String wachtwoord) {
		this.wachtwoord = wachtwoord;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
        
        public static boolean isValidPassword(String password) {
        {
            /* uit een breking van de regex controle =
            {.8,} staat voor minimaal acht karakters totaal
            (?=.*?[A-Z]) & (?=.*?[a-z]) staan voor minimaal 1 letters van a-z en minimaal 1 hoofdletter van A-Z
            (?=.*?[0-9]) staat voor minimaal 1 getal
            (?=.*?[?!@$%^&*]) staat voor minimaal 1 speciaal karakter
           */
            String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
            Pattern pattern = Pattern.compile(regex);
            if (password == null)
                return false;
            return pattern.matcher(password).matches();
        }
    }


}
