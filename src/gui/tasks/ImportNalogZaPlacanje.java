package gui.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import utils.CurrentBank;
import db.DBConnection;
import exceptions.InvalidNalogZaPlacanjeException;
import gui.tasks.entities.nalog.za.placanje.NalogZaPlacanje;



public class ImportNalogZaPlacanje {
	
	private static enum RacunPostojiOdgovor{
		POSTOJI,
		NE_POSTOJI,
		UKINUT_JE;
	}
	
	private static enum AzurirajRacunTip{
		DUZNIK,
		POVERILAC;
	}
	
	private static final int BROJ_IZVODA = 1;
	private static final BigDecimal RTGS_LIMIT = BigDecimal.valueOf(250000);

	/**
	 * Test main metoda
	 */
	public static void main(String[] args) {
		File[] xmlFiles = new File("./testPrimeri").listFiles();
		for(File testFile : xmlFiles){
			System.out.println("Testiranje za fajl : " + testFile.getName());
			try {
				System.out.println();
				System.out.println(importNalog(new FileInputStream(testFile)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidNalogZaPlacanjeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		try {
//			importNalog(new FileInputStream(new File("./testPrimeri/nalogNevalidnaShema.xml")));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidNalogZaPlacanjeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	public static String importNalog(InputStream xmlStream) 
			throws SQLException, InvalidNalogZaPlacanjeException{
		
		//DBConnection.getConnection().rollback();	
		StringBuilder response = new StringBuilder();
		NalogZaPlacanje nalog;
		try {
			nalog = validateAndUnmarshallXml(xmlStream);
		} catch (UnmarshalException e) {
			throw new InvalidNalogZaPlacanjeException(
					"Greska prilikom validacije xml naloga! : \n" + e.getLinkedException().getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new InvalidNalogZaPlacanjeException(
					"Greska prilikom validacije xml naloga! : \n" + e.getMessage());
		}
		response.append("XML fajl je validan.\n");
		
		String oznakaValute = nalog.getOznakaValute();
		
		//Provera da li valuta postoji
		boolean postojiValuta = checkOznakaValute(oznakaValute);
		if(!postojiValuta)
			throw new InvalidNalogZaPlacanjeException("Ne postoji oznaka za valutu koja se nalazi u nalogu: " + oznakaValute);
		
		Date datumPrijema = new java.sql.Date(nalog.getDatumNaloga().toGregorianCalendar().getTime().getTime());
		//System.out.println("Datum prijema: " + datumPrijema);
		java.sql.Date datumValute = new java.sql.Date(nalog.getDatumValute().toGregorianCalendar().getTime().getTime());
		//System.out.println("Datum valute: " + datumValute);
		
		int tipGreske = ErrorType.ISPRAVAN;
		
		//Provera da li je datum valute pre datuma prijema
		if(datumValute.before(datumPrijema))
			throw new InvalidNalogZaPlacanjeException("Datum valute je pre datuma prijema.");
		
		BigDecimal iznos = nalog.getIznos();
//		System.out.println(iznos);
		if(iznos.compareTo(BigDecimal.ZERO) < 0 )
			throw new InvalidNalogZaPlacanjeException("Iznos je manji od nule.");
		
		String racunDuznika = nalog.getRacunDuznika();
		String racunPoverioca = nalog.getRacunPoverioca();
		//Provera da li su racuni isti
		if(racunDuznika.equals(racunPoverioca))
			throw new InvalidNalogZaPlacanjeException("Racuni dužnika i poverioca su isti");
		response.append("Racun duznika: " + racunDuznika + "\n");
		response.append("Racun poverioca: " + racunPoverioca + "\n");
		
		
		//Provera da li racuni postoje u nasoj banci ili da li postoji druga banka u nasem registru
		boolean postojiRacunDuznika = false;
		boolean postojiRacunPoverioca = false;
		String idBankeDuznika = checkBank(racunDuznika);
		String idBankePoverioca = checkBank(racunPoverioca);
		if(idBankeDuznika != null){
			if(idBankeDuznika.equals(CurrentBank.getId())){
				idBankeDuznika = CurrentBank.getId();
				RacunPostojiOdgovor odgovor = doesRacunExistInOurBank(racunDuznika);
				if(odgovor == RacunPostojiOdgovor.POSTOJI){
					response.append("Račun dužnika se nalazi u našoj banci\n");
					postojiRacunDuznika = true;
				}
				else if(odgovor == RacunPostojiOdgovor.NE_POSTOJI && tipGreske == ErrorType.ISPRAVAN){
					response.append("PAŽNJA: Račun dužnika bi trebao da se nalazi u banci, ali nije nadjen.\n");
					tipGreske = ErrorType.POGRESAN_NALOG;
				}
				else if(odgovor == RacunPostojiOdgovor.UKINUT_JE && tipGreske == ErrorType.ISPRAVAN){
					response.append("PAŽNJA: Račun dužnika je ukinut.\n");
					tipGreske = ErrorType.RACUN_UKINUT;
				}
			}
			else{
				response.append("Račun dužnika se nalazi u drugoj banci\n");
			}
		}
		else{
			response.append("PAŽNJA: Banka duznika nije nadjena u nasoj bazi podataka!\n");
			if(tipGreske == ErrorType.ISPRAVAN)
				tipGreske = ErrorType.NEPOSTOJECA_BANKA_RACUNA;
		}
		
		if(idBankePoverioca != null){
			if(idBankePoverioca.equals(CurrentBank.getId())){
				idBankePoverioca = CurrentBank.getId();
				RacunPostojiOdgovor odgovor = doesRacunExistInOurBank(racunPoverioca);
				if(odgovor == RacunPostojiOdgovor.POSTOJI){
					response.append("Račun poverioca se nalazi u našoj banci\n");
					postojiRacunPoverioca = true;
				}
				else if(odgovor == RacunPostojiOdgovor.NE_POSTOJI && tipGreske == ErrorType.ISPRAVAN){
					response.append("PAŽNJA: Račun poverioca bi trebao da se nalazi u banci, ali nije nadjen.\n");
					tipGreske = ErrorType.POGRESAN_NALOG;
				}
				else if(odgovor == RacunPostojiOdgovor.UKINUT_JE && tipGreske == ErrorType.ISPRAVAN){
					response.append("PAŽNJA: Račun poverioca je ukinut!\n");
					tipGreske = ErrorType.RACUN_UKINUT;
				}
				
			}
			else{
				response.append("Račun poverioca se nalazi u drugoj banci\n");
			}
		}
		else{
			response.append("PAŽNJA: Banka poverioca nije nadjena u nasoj bazi podataka!\n");
			if(tipGreske == ErrorType.ISPRAVAN)
				tipGreske = ErrorType.NEPOSTOJECA_BANKA_RACUNA;
		}
		
		if(!postojiRacunDuznika && !postojiRacunPoverioca)
			throw new InvalidNalogZaPlacanjeException("Ni jedan ni drugi račun ne pripada našoj banci");
		
		//Provera valuta u racunima
		if(postojiRacunDuznika){
			if(!checkOznakaValuteURacunu(oznakaValute, racunDuznika))
				throw new InvalidNalogZaPlacanjeException("Ne odgovara valuta računu dužnika");
		}
		if(postojiRacunPoverioca){
			if(!checkOznakaValuteURacunu(oznakaValute, racunPoverioca))
				throw new InvalidNalogZaPlacanjeException("Ne odgovara valuta računu poverioca");
		}
		
		//System.out.println("Stari datum valute: " + datumValute);
		datumValute = updateDatumValute(racunDuznika, racunPoverioca, datumValute);
		response.append("Datum valute: " + datumValute + "\n");
		
		//Inicijalizujemo dnevno stanje
		if(postojiRacunDuznika){
			initialiseDnevnoStanje(racunDuznika, datumValute);
		}
		if(postojiRacunPoverioca){
			initialiseDnevnoStanje(racunPoverioca, datumValute);
		}		
		//radimo commit ovde jer u slucaju da u racunu duznika sve prodje dobro, 
		//a da se u racunu poverioca desi SQLException, da ne dodje do commit-a.
		DBConnection.getConnection().commit();
		
		Boolean isDovoljnoNovcaDuznik = null;
		if(postojiRacunDuznika && tipGreske == ErrorType.ISPRAVAN){
			isDovoljnoNovcaDuznik = dovoljnoNovcaNaRacunuDuznika(racunDuznika, datumValute, iznos);
			if(!isDovoljnoNovcaDuznik){
				response.append("PAŽNJA: Račun dužnika nema dovoljno novca na računu za izvršenje naloga\n");
				tipGreske = ErrorType.NELIKVIDNOST_PODRACUNA_KORISNIKA;
			}
		}
		//System.out.println("Da li ima dovoljno novca na racunu duznika: " + isDovoljnoNovcaDuznik);
		
		//Azuriranje stanja racuna
		if(postojiRacunDuznika && tipGreske == ErrorType.ISPRAVAN){
			azurirajRacun(racunDuznika, datumValute, iznos, AzurirajRacunTip.DUZNIK);
			response.append("Uspešno ažuriran račun dužnika\n");
		}
		if(postojiRacunPoverioca && tipGreske == ErrorType.ISPRAVAN){
			azurirajRacun(racunPoverioca, datumValute, iznos, AzurirajRacunTip.POVERILAC);
			response.append("Uspešno ažuriran račun poverioca\n");
		}
		
		if(tipGreske == ErrorType.ISPRAVAN)
			DBConnection.getConnection().commit();
		
		//Insert stavke Analitike Izvoda
		Integer brojStavkeAnalitikeDuznika = null;
		Integer brojStavkeAnalitikePoverioca = null;
		if(postojiRacunDuznika){
			brojStavkeAnalitikeDuznika = insertAnalitikaIzvoda(racunDuznika, datumValute, BROJ_IZVODA, nalog, tipGreske);
			DBConnection.getConnection().commit();
			response.append("Uspešno je unesen slog analitike izvoda za račun dužnika.\n");
		}
		if(postojiRacunPoverioca){
			brojStavkeAnalitikePoverioca = insertAnalitikaIzvoda(racunPoverioca, datumValute, BROJ_IZVODA, nalog, tipGreske);
			DBConnection.getConnection().commit();
			response.append("Uspešno je unesen slog analitike izvoda za račun poverioca.\n");
		}
		
		//Medjubankarski nalog
		if(postojiRacunDuznika && tipGreske == ErrorType.ISPRAVAN && 
				idBankePoverioca != null && !idBankePoverioca.equals(CurrentBank.getId())){
			//System.out.println("POCETAK KREIRANJA MEDJUBANKARSKOG NALOGA");
			boolean hitno = nalog.isHitno();
			String tipNaloga;
			if(hitno || iznos.compareTo(RTGS_LIMIT) >= 0 )
				tipNaloga = "R";
			else
				tipNaloga = "C";
			generateMedjubankarskiNalog(idBankePoverioca, idBankeDuznika, racunDuznika,
					datumValute, BROJ_IZVODA, brojStavkeAnalitikeDuznika, tipNaloga);
		    DBConnection.getConnection().commit();
		    
			//System.out.println("USPESNO IZVRSENO GENERISANJE RTGS NALOGA ZA POVERIOCA: TIP_NALOGA = " + tipNaloga);
			response.append("Uspešno je kreiran medjubankarski ");
			if(tipNaloga.equals("R"))
				response.append("RTGS ");
			else if(tipNaloga.equals("C"))
				response.append("Clearing ");
			response.append("nalog za banku poverioca\n");
		    
		}
		if(postojiRacunPoverioca && tipGreske == ErrorType.ISPRAVAN && 
				idBankeDuznika != null && !idBankeDuznika.equals(CurrentBank.getId())){
//			System.out.println("POCETAK KREIRANJA MEDJUBANKARSKOG NALOGA");
			boolean hitno = nalog.isHitno();
			String tipNaloga;
			if(hitno || iznos.compareTo(RTGS_LIMIT) >= 0 )
				tipNaloga = "R";
			else
				tipNaloga = "C";
			
			generateMedjubankarskiNalog(idBankePoverioca, idBankeDuznika, racunPoverioca, 
					datumValute, BROJ_IZVODA, brojStavkeAnalitikePoverioca, tipNaloga);
		    DBConnection.getConnection().commit();
		    
			//System.out.println("USPESNO IZVRSENO GENERISANJE RTGS NALOGA ZA DUZNIKA: TIP_NALOGA = " + tipNaloga);
			response.append("Uspešno je kreiran medjubankarski ");
			if(tipNaloga.equals("R"))
				response.append("RTGS ");
			else if(tipNaloga.equals("C"))
				response.append("Clearing ");
			response.append("nalog za banku dužnika\n");
		}

		
		response.append("KRAJ: Nalog za uplatu je uspešno evidentiran, ");
		if(tipGreske == ErrorType.ISPRAVAN)
			response.append("i nalog je ispravan");
		if(tipGreske == ErrorType.NELIKVIDNOST_PODRACUNA_KORISNIKA)
			response.append("uz grešku: nelikvidnosti podračuna korisnika");
		if(tipGreske == ErrorType.NEPOSTOJECA_BANKA_RACUNA)
			response.append("uz grešku: nepostojeÄ‡a banka računa");
		if(tipGreske == ErrorType.POGRESAN_NALOG)
			response.append("uz grešku: pogrešan nalog");
		if(tipGreske == ErrorType.RACUN_UKINUT)
			response.append("uz grešku: račun ukinut");
		
		return response.toString();
		
	}
	
	

	private static NalogZaPlacanje validateAndUnmarshallXml(InputStream xmlStream) throws SAXException, JAXBException{
		JAXBContext context = JAXBContext.newInstance(NalogZaPlacanje.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		//postavljanje validacije
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new File("./xsd/nalogZaPlacanje.xsd"));
		unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(new MyValidationEventHandler());
		
        //ucitava se objektni model, a da se pri tome radi i validacija
		NalogZaPlacanje nalog = (NalogZaPlacanje) unmarshaller.unmarshal(xmlStream);
		return nalog;
		
	}
	
	private static RacunPostojiOdgovor doesRacunExistInOurBank(String racun) throws SQLException{
		RacunPostojiOdgovor odgovor;
		String sql = "SELECT BAR_RACUN, BAR_VAZI " +
				"FROM RACUN_POSLOVNIH_LICA " +
				"WHERE BAR_RACUN = ? AND BANK_SIFRA = ? ";
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
		stmt.setString(1, racun);
		stmt.setString(2, CurrentBank.getId());
		ResultSet rset = stmt.executeQuery();
		if(rset.next()){
			boolean vazi = rset.getBoolean(2);
			if(vazi)
				odgovor = RacunPostojiOdgovor.POSTOJI;
			else
				odgovor = RacunPostojiOdgovor.UKINUT_JE;
			if(rset.next()){
				throw new SQLException("Vraceno je više od jednog računa!");
			}
		}
		else
			odgovor = RacunPostojiOdgovor.NE_POSTOJI;
		
		rset.close();
		stmt.close();
		return odgovor;
	}
	
	/**
	 * Ukoliko postoji banka (koja je izdala racun koji se prosledjuje) u bazi podataka, vraca njen ID </br>
	 * U suprotnom vraca <code>null</code>
	 */
	private static String checkBank(String racun) throws SQLException{
		
		String idBankQuery = racun.substring(0, 3);
		String sql = "SELECT BANK_SIFRA " +
				"FROM BANKA " +
				"WHERE BANK_SIFRA = " + idBankQuery;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next())
			return rset.getString(1);
		
		return null;
	}
	
	private static boolean checkOznakaValute(String oznakaValute) throws SQLException{
		boolean retVal;
		String sql = "SELECT VA_SIFRA " +
				"FROM VALUTA " +
				"WHERE VA_SIFRA = ? ";
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
		stmt.setString(1, oznakaValute);
		ResultSet rset = stmt.executeQuery();
		if(rset.next()){
			retVal = true;
			if(rset.next()){
				throw new SQLException("Vraceno je više od jedne šifre valute!");
			}
		}
		else
			retVal = false;
		
		rset.close();
		stmt.close();
		return retVal;
	}
	
	private static boolean checkOznakaValuteURacunu(String oznakaValute, String racun) throws SQLException{
		boolean retVal;
		String sql = "SELECT VA_SIFRA " +
				"FROM RACUN_POSLOVNIH_LICA " +
				"WHERE VA_SIFRA = ? AND BAR_RACUN = ? AND BANK_SIFRA = ? ";
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
		stmt.setString(1, oznakaValute);
		stmt.setString(2, racun);
		stmt.setString(3, CurrentBank.getId());
		ResultSet rset = stmt.executeQuery();
		if(rset.next()){
			retVal = true;
			if(rset.next()){
				throw new SQLException("Vraceno je više od jedne šifre valute!");
			}
		}
		else
			retVal = false;
		
		rset.close();
		stmt.close();
		return retVal;
	}
	
	private static Date updateDatumValute(String racunDuznika, String racunPoverioca, Date datumValute) 
			throws SQLException{
//		System.out.println("Update valute pomocu DNEVNO_STANJE_RACUNA");
		Date noviDatumValute = datumValute;
		String sqlDnevnoStanje = "SELECT MAX(DSR_DATUM) " +
				"FROM DNEVNO_STANJE_RACUNA " +
				"WHERE BAR_RACUN = ? OR BAR_RACUN = ? ";
		PreparedStatement stmtDnevnoStanje = DBConnection.getConnection().prepareStatement(sqlDnevnoStanje);
		stmtDnevnoStanje.setString(1, racunDuznika);
		stmtDnevnoStanje.setString(2, racunPoverioca);
		ResultSet rsetDnevnoStanje = stmtDnevnoStanje.executeQuery();
		if(rsetDnevnoStanje.next()){
			Date currentDate = rsetDnevnoStanje.getDate(1);
			
			if(currentDate != null && currentDate.after(noviDatumValute))
				noviDatumValute = currentDate;
		}
		rsetDnevnoStanje.close();
		stmtDnevnoStanje.close();
		
//		System.out.println("Update valute pomocu RACUN_POSLOVNOG_LICA");
		String sqlCheckRacunPoslovnih = "SELECT MAX(BAR_DATOTV) " +
				"FROM RACUN_POSLOVNIH_LICA " +
				"WHERE BAR_RACUN = ? OR BAR_RACUN = ? ";
		PreparedStatement stmtRacunPoslovnih = DBConnection.getConnection().prepareStatement(sqlCheckRacunPoslovnih);
		stmtRacunPoslovnih.setString(1, racunDuznika);
		stmtRacunPoslovnih.setString(2, racunPoverioca);
		ResultSet rsetRacunPoslovnih = stmtRacunPoslovnih.executeQuery();
		if(rsetRacunPoslovnih.next()){
			Date currentDate = rsetRacunPoslovnih.getDate(1);
			if(currentDate.after(noviDatumValute))
				noviDatumValute = currentDate;
		}
		rsetRacunPoslovnih.close();
		stmtRacunPoslovnih.close();
		
		return noviDatumValute;
	}
	
	private static void initialiseDnevnoStanje(String racun, Date datumValute) 
			throws SQLException{
		
		//Check datumPrometa in Dnevno_Stanje_Racuna
		String sqlCheck = "SELECT DISTINCT DSR_DATUM " +
				"FROM DNEVNO_STANJE_RACUNA " +
				"WHERE BAR_RACUN = ? ";
		PreparedStatement stmtCheck = DBConnection.getConnection().prepareStatement(sqlCheck);
		stmtCheck.setString(1, racun);
		ResultSet rsetCheck = stmtCheck.executeQuery();
		Vector<Date> datesInDnevnoStanje = new Vector<Date>();
		while(rsetCheck.next()){
			Date currentDate = rsetCheck.getDate(1);
//			System.out.println("Current date: " + currentDate);
			datesInDnevnoStanje.add(currentDate);
		}
		
		rsetCheck.close();
		stmtCheck.close();
		
		//Nema nijednog dnevnog stanja u racunu, inicijalizuj sve
		if(datesInDnevnoStanje.contains(datumValute)){
			System.out.println("INFO: Datum valute je sadrzan u tabeli DNEVNO_STANJE_RACUNA, nije potrebno inicijalizovati");
			return;
		}
		
		String sqlInsert = "INSERT INTO DNEVNO_STANJE_RACUNA" +
				"(BAR_RACUN, DSR_IZVOD, DSR_DATUM, DSR_PRETHODNO, " +
				"DSR_UKORIST, DSR_NATERET, DSR_NOVOSTANJE)" +
				"VALUES (?,?,?,?,?,?,?)";
		int izvod = BROJ_IZVODA;
		BigDecimal prethodno;
		BigDecimal uKorist;
		BigDecimal naTeret;
		BigDecimal novoStanje;
		if(datesInDnevnoStanje.size() == 0){
			prethodno = new BigDecimal(0);
			uKorist = new BigDecimal(0);
			naTeret = new BigDecimal(0);
			novoStanje = new BigDecimal(0);
		}
		else{
			Date maxPreviousDate = datesInDnevnoStanje.firstElement();
			for(int i = 1; i < datesInDnevnoStanje.size(); i++){
				if(datesInDnevnoStanje.get(i).after(maxPreviousDate))
					maxPreviousDate = datesInDnevnoStanje.get(i);
			}
			
			String sqlState = "SELECT DSR_NOVOSTANJE " +
					"FROM DNEVNO_STANJE_RACUNA " +
					"WHERE BAR_RACUN = ? AND DSR_DATUM = ? AND DSR_IZVOD = ? ";
			PreparedStatement stmtState = DBConnection.getConnection().prepareStatement(sqlState);
			stmtState.setString(1, racun);
			stmtState.setDate(2, maxPreviousDate);
			stmtState.setInt(3, izvod);
			ResultSet rsetState = stmtState.executeQuery();
			if(rsetState.next()){
				prethodno = rsetState.getBigDecimal(1);
				uKorist = new BigDecimal(0);
				naTeret = new BigDecimal(0);
				novoStanje = new BigDecimal(prethodno.doubleValue());
				if(rsetState.next()){
					throw new SQLException("Nadjena su dva nova stanja za zadate kljuceve!");
				}
			}
			else
				throw new SQLException("Nije dobijeno nijedno novo stanje za zadate kljuceve!");
			rsetState.close();
			stmtState.close();
		}
		PreparedStatement stmtInsert = DBConnection.getConnection().prepareStatement(sqlInsert);
		stmtInsert.setString(1, racun);
		stmtInsert.setInt(2, izvod);
		stmtInsert.setDate(3, datumValute);
		stmtInsert.setBigDecimal(4, prethodno);
		stmtInsert.setBigDecimal(5, uKorist);
		stmtInsert.setBigDecimal(6, naTeret);
		stmtInsert.setBigDecimal(7, novoStanje);
		int rowsAffectedInsert = stmtInsert.executeUpdate();
		stmtInsert.close();
		if(rowsAffectedInsert != 1)
			throw new SQLException("rowsAffectedInsert != 1");
		System.out.println("INFO: USPESNO DODAT SLOG U DNEVNO_STANJE_RACUNA");
	}
	
	/**
	 * Vraca true ukoliko ima dovoljno novca na dnevnom stanju racuna duznika. U suprotnom vraca false.
	 */
	private static boolean dovoljnoNovcaNaRacunuDuznika(String racunDuznika,
			Date datumValute, BigDecimal iznos) throws SQLException {
		
		int izvod = BROJ_IZVODA;
		String sqlState = "SELECT DSR_NOVOSTANJE " +
				"FROM DNEVNO_STANJE_RACUNA " +
				"WHERE BAR_RACUN = ? AND DSR_DATUM = ? AND DSR_IZVOD = ? ";
		PreparedStatement stmtState = DBConnection.getConnection().prepareStatement(sqlState);
		stmtState.setString(1, racunDuznika);
		stmtState.setDate(2, datumValute);
		stmtState.setInt(3, izvod);
		ResultSet rsetState = stmtState.executeQuery();
		if(rsetState.next()){
			BigDecimal novoStanje = rsetState.getBigDecimal(1);
			if(novoStanje.compareTo(iznos) < 0)
				return false;
			else
				return true;
		}
		else
			throw new SQLException("Nije nadjen slog tokom provere iznosa stanja racuna duznika");
	}
	
	private static void azurirajRacun(String racun, Date datumValute, BigDecimal iznos, 
			AzurirajRacunTip azurirajRacunTip) throws SQLException{
		
		int izvod = BROJ_IZVODA;
		String sqlQuery = "SELECT DSR_PRETHODNO, DSR_UKORIST, DSR_NATERET, DSR_NOVOSTANJE " +
				"FROM DNEVNO_STANJE_RACUNA " +
				"WHERE BAR_RACUN = ? AND DSR_DATUM = ? AND DSR_IZVOD = ?";
		PreparedStatement stmtQuery = DBConnection.getConnection().prepareStatement(sqlQuery);
		stmtQuery.setString(1, racun);
		stmtQuery.setDate(2, datumValute);
		stmtQuery.setInt(3, izvod);
		ResultSet rsetQuery = stmtQuery.executeQuery();
		if(rsetQuery.next()){
			BigDecimal prethodnoStanje = rsetQuery.getBigDecimal(1);
			BigDecimal uKorist = rsetQuery.getBigDecimal(2);
			BigDecimal naTeret = rsetQuery.getBigDecimal(3);
			BigDecimal novoStanje = rsetQuery.getBigDecimal(4);
			if(azurirajRacunTip == AzurirajRacunTip.DUZNIK)
				naTeret = naTeret.add(iznos);
			else
				uKorist = uKorist.add(iznos);
			novoStanje = prethodnoStanje.add(uKorist).subtract(naTeret);
			
			String sqlUpdate = "UPDATE DNEVNO_STANJE_RACUNA " +
					"SET DSR_UKORIST = ? , DSR_NATERET = ? , DSR_NOVOSTANJE = ? " +
					"WHERE BAR_RACUN = ? AND DSR_DATUM = ? AND DSR_IZVOD = ? ";
			PreparedStatement stmtUpdate = DBConnection.getConnection().prepareStatement(sqlUpdate);
			stmtUpdate.setBigDecimal(1, uKorist);
			stmtUpdate.setBigDecimal(2, naTeret);
			stmtUpdate.setBigDecimal(3, novoStanje);
			stmtUpdate.setString(4, racun);
			stmtUpdate.setDate(5, datumValute);
			stmtUpdate.setInt(6, izvod);
			int rowsAffected = stmtUpdate.executeUpdate();
			stmtUpdate.close();
			if(rowsAffected != 1)
				throw new SQLException("rowsAffected kod azuriranjaDnevnogRacuna nije = 1 : " + rowsAffected);
//			System.out.println("Uspesno azuriran racun kod : " + azurirajRacunTip);
		}
		else
			throw new SQLException("Nije nadjeno dnevno stanje racuna za azuriranje!" + azurirajRacunTip);
		rsetQuery.close();
		stmtQuery.close();
		
	}
	
	/**
	 * Pravi slog u analitici izvoda i vraca brojStavke (deo primarnog kljuca pomocu kog je kreiran slog)
	 */
	private static int insertAnalitikaIzvoda(String racun, Date datumValute, int brojIzvoda,
			NalogZaPlacanje nalog, int tipGreske) throws SQLException{
		
		int brojStavke;
		String sqlBrojStavke = "SELECT MAX(ASI_BROJSTAVKE) " +
				"FROM ANALITIKA_IZVODA " +
				"WHERE BAR_RACUN = ? AND DSR_IZVOD = ? AND DSR_DATUM = ? ";
		PreparedStatement stmtBrojStavke = DBConnection.getConnection().prepareStatement(sqlBrojStavke);
		stmtBrojStavke.setString(1, racun);
		stmtBrojStavke.setInt(2, brojIzvoda);
		stmtBrojStavke.setDate(3, datumValute);
		ResultSet rsetBrojStavke = stmtBrojStavke.executeQuery();
		if(rsetBrojStavke.next())
			brojStavke = rsetBrojStavke.getInt(1) + 1;
		else
			brojStavke = 1;
		rsetBrojStavke.close();
		stmtBrojStavke.close();
		
		String sqlInsert = "INSERT INTO ANALITIKA_IZVODA " +
				"(BAR_RACUN, DSR_IZVOD, DSR_DATUM, ASI_BROJSTAVKE, VA_SIFRA, ASI_DUZNIK, " +
				"ASI_SVRHA, ASI_POVERILAC, ASI_DATPRI, ASI_RACDUZ, ASI_MODZAD, ASI_PBZAD, " +
				"ASI_RACPOV, ASI_MODODOB, ASI_PBODO, ASI_HITNO, ASI_IZNOS, ASI_TIPGRESKE, " +
				"ASI_STATUS) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        PreparedStatement stmtInsert = DBConnection.getConnection().prepareStatement(sqlInsert);
        stmtInsert.setString(1, racun);
        stmtInsert.setInt(2, brojIzvoda);
        stmtInsert.setDate(3, datumValute);
        stmtInsert.setInt(4, brojStavke);
        stmtInsert.setString(5, nalog.getOznakaValute());
        stmtInsert.setString(6, nalog.getDuznikNalogodavac());
        stmtInsert.setString(7, nalog.getSvrhaPlacanja());
        stmtInsert.setString(8, nalog.getPrimalacPoverilac());
        stmtInsert.setDate(9, new java.sql.Date(nalog.getDatumNaloga().toGregorianCalendar().getTime().getTime()));
        stmtInsert.setString(10, nalog.getRacunDuznika());
        stmtInsert.setInt(11, nalog.getModelZaduzenja().intValue());
        stmtInsert.setString(12, nalog.getPozivNaBrojZaduzenja());
        stmtInsert.setString(13, nalog.getRacunPoverioca());
        stmtInsert.setInt(14, nalog.getModelOdobrenja().intValue());
        stmtInsert.setString(15, nalog.getPozivNaBrojOdobrenja());
        stmtInsert.setBoolean(16, nalog.isHitno());
        stmtInsert.setBigDecimal(17, nalog.getIznos());
        stmtInsert.setInt(18, tipGreske);
        stmtInsert.setString(19, "E");
        int rowsAffected = stmtInsert.executeUpdate();
        stmtInsert.close();
        if(rowsAffected != 1){
        	throw new SQLException("RowsAffected in createAndInsertAnalitikaIzvoda method is not 1 : " + rowsAffected);
        }
//        System.out.println("Uspesno napravljen slog u analitici izvoda!");
        return brojStavke;
	}
	
	public static void generateMedjubankarskiNalog(String idBankePoverioca, String idBankeDuznika, String racun,
			Date datumValute, int brojIzvoda, int brojStavke, String tipNaloga) throws SQLException{
		
		CallableStatement proc = DBConnection.getConnection().prepareCall(
				"{ call GENERATE_MEDJUBANKARSKI_NALOG(?,?,?,?,?,?,?)}");
		proc.setString(1, idBankePoverioca);
		proc.setString(2, idBankeDuznika);
		proc.setString(3, racun);
		proc.setDate(4, datumValute);
		proc.setInt(5, brojIzvoda);
		proc.setInt(6, brojStavke);
		proc.setString(7, tipNaloga);
		proc.execute();
		
	}
	
}
