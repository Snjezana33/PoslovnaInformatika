package users;

public class User {
	
	protected String ime;
	protected String sifra;
	
	
	public String getIme() {
		return ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getSifra() {
		return sifra;
	}
	public void setSifra(String sifra) {
		this.sifra = sifra;
	}
	public User(String ime, String sifra) {
		super();
		this.ime = ime;
		this.sifra = sifra;
	}
	public User() {
		super();
		
	}

}
