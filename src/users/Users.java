package users;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Users {
	private Map<String, User> mapaKorisnika = new HashMap<String, User>();
	
	public boolean checkKorisnik(String username) {
		return mapaKorisnika.containsKey(username);
	}
	
	public boolean checkUsernameAndPassword(String username, String password) {
		if (!checkKorisnik(username))
			return false;
		
		User korisnik=mapaKorisnika.get(username);
		
		if (korisnik.getSifra().equalsIgnoreCase((password))){
			return true;
		}else{
			return false;
		}
	}
	
	public Map<String, User> getMapaKorisnika() {
		return mapaKorisnika;
	}

	public void setMapaKorisnika(Map<String, User> mapaKorisnika) {
		this.mapaKorisnika = mapaKorisnika;
	}

	public void load(BufferedReader in) throws IOException {
		String line=null;
		mapaKorisnika = new HashMap<String, User>();
		
		
		while((line=in.readLine())!=null){
		String ime=line.substring(0,line.indexOf(':'));
		String sifra=line.substring(line.indexOf(':')+1,line.length());
		User kor=new User(ime,sifra);
		
		mapaKorisnika.put(kor.ime, kor);
		}
	}
}
