package utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DBConnection;

public class CurrentBank {

	private static String id = "111";
	
	private static String swiftKod = "CODERS22";
	private static String naziv = "Banka";
	private static String obracunskiRacun = "020123366799123431";
	private static String adresa = "Trg Dositeja Obradovica 6, Novi Sad";
	private static String email = "mail@bank.com";
	private static String web = "banka.com";
	private static String telefon = "0718234597";
	private static String fax = "0215243367";
	
	public static String getId(){
		return id;
	}
	
	public static void insertCurrentBankToDatabase() throws SQLException{
		if(!isCurrentBankInDatabase()){
			System.out.println("Creating CurrentBank");
			String sql = "INSERT INTO BANKA(BANK_SIFRA," +
					"BANK_SWIFT," +
					"BANK_OBR_RACUN," +
					"BANK_NAZIV," +
					"BANK_ADRESA," +
					"BANK_EMAIL," +
					"BANK_WEB," +
					"BANK_TEL," +
					"BANK_FAX) VALUES (?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = DBConnection
					.getConnection()
					.prepareStatement(sql);
			stmt.setObject(1, id);
			stmt.setObject(2, swiftKod);
			stmt.setObject(3, obracunskiRacun);
			stmt.setObject(4, naziv);
			stmt.setObject(5, adresa);
			stmt.setObject(6, email);
			stmt.setObject(7, web);
			stmt.setObject(8, telefon);
			stmt.setObject(9, fax);
			int rows = stmt.executeUpdate();
			stmt.close();
			DBConnection.getConnection().commit();
			System.out.println("CurrentBank successfully created! rowsAffected =  " + rows);
		}
		else
			System.out.println("CurrentBank already exists");
	}
	
	public static boolean isCurrentBankInDatabase() throws SQLException{
		boolean retVal;
		Statement stmt = DBConnection.getConnection().createStatement();
		String sql = "SELECT BANK_SIFRA FROM BANKA WHERE BANK_SIFRA = '" + id + "'";
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next())
			retVal = true;
		else
			retVal = false;
		rset.close();
		stmt.close();
		return retVal;
		
	}
}
