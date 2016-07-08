package gui.tasks;

import db.DBConnection;
import exceptions.InvalidExportException;
import gui.tasks.entities.clearing.ClearingNalog;
import gui.tasks.entities.clearing.StavkaClearingNaloga;
import gui.tasks.entities.clearing.ZaglavljeClearingNaloga;
import gui.tasks.entities.rtgs.RTGSNalog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

public class ExportMedjubankarskiNalog {

	/**
	 * Main metoda za testiranje klase
	 */
	public static void main(String[] args) {
		
		
		try {
			File file = new File("./testExportResults");
			exportRTGSNalog(4, new FileOutputStream(new File(file,"rtgsNalog.xml")));
			exportClearingNalog(17, new FileOutputStream(new File(file,"clearingNalog.xml")));
			
			System.out.println("SUCCESS");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static RTGSNalog exportRTGSNalog(int mbnId, OutputStream output) 
			throws SQLException, InvalidExportException, JAXBException{
		
		RTGSNalog rtgsNalog = new RTGSNalog();

		checkTipAndPoslatoNaloga(mbnId, "R");
		putZaglavljeInNalogRTGS(rtgsNalog, mbnId);
		putStavkeInNalogRTGS(rtgsNalog, mbnId);
		
		JAXBContext jaxbContext = JAXBContext
								.newInstance(RTGSNalog.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				true);
		jaxbMarshaller.marshal(rtgsNalog, output);
		
		updateNalogPoslato(mbnId);
		DBConnection.getConnection().commit();
		System.out.println("Uspesno eksportovan RTGS nalog!");

		return rtgsNalog;
	}

	public static ClearingNalog exportClearingNalog(int mbnId, OutputStream output) 
			throws SQLException, InvalidExportException, JAXBException{
		
		ClearingNalog clearingNalog = new ClearingNalog();
		clearingNalog.setZaglavlje(new ZaglavljeClearingNaloga());
		checkTipAndPoslatoNaloga(mbnId, "C");
			
		BigDecimal ukupanIznos = putStavkeInNalogClearing(clearingNalog, mbnId);
		putZaglavljeInNalogClearing(clearingNalog, mbnId, ukupanIznos);
		
		JAXBContext jaxbContext = JAXBContext
				.newInstance(ClearingNalog.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				true);
		jaxbMarshaller.marshal(clearingNalog, output);
		
		updateNalogPoslato(mbnId);
		DBConnection.getConnection().commit();
		System.out.println("Uspesno eksportovan Clearing nalog!");
			
		return clearingNalog;
		
	}
	

	

	private static void checkTipAndPoslatoNaloga(int mbnId, String tipNaloga) 
			throws SQLException, InvalidExportException{
		
		String sql = "SELECT" + 
				" MBN_TIP, MBN_POSLATO" + 
				" FROM MEDJUBANKARSKI_NALOG WHERE" +
				" MBN_ID = " + mbnId;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			String currentTip = rset.getString(1);
			if(!currentTip.equals(tipNaloga)){
				String exceptionMessagePart = "";
				if(tipNaloga.equals("R"))
					exceptionMessagePart = "RTGS";
				else if(tipNaloga.equals("C")){
					exceptionMessagePart = "Clearing";
				}
				throw new InvalidExportException("Izabrani nalog nije tipa " + exceptionMessagePart);
			}
			if(rset.getBoolean(2)){
				throw new InvalidExportException("Izabrani nalog je već poslat ");
			}
		}
		else
			throw new SQLException("Nije nadjen traženi medjubankarski nalog");
		rset.close();
		stmt.close();
		
		
	}
	
	private static void putZaglavljeInNalogRTGS(RTGSNalog nalog, int mbnId) throws SQLException {
		
		String sql = "SELECT bankDuz.BANK_SWIFT, bankDuz.BANK_OBR_RACUN, " +
		"bankPov.BANK_SWIFT, bankPov.BANK_OBR_RACUN " +
		"FROM MEDJUBANKARSKI_NALOG mbn " +
		"JOIN BANKA bankDuz ON bankDuz.BANK_SIFRA = mbn.BANK_SIFRA_DUZNIK " +
		"JOIN BANKA bankPov ON bankPov.BANK_SIFRA = mbn.BANK_SIFRA_POVERIOC " +
		"WHERE mbn.MBN_ID = " + mbnId;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			nalog.setIDPoruke("" + mbnId);
			nalog.setSWIFTKodBankeDuznika(rset.getString(1));
			nalog.setObracunskiRacunBankeDuznika(rset.getString(2));
			nalog.setSWIFTKodBankePoverioca(rset.getString(3));
			nalog.setObracunskiRacunBankePoverioca(rset.getString(4));
		}
		else
			throw new SQLException("Nije nadjen traženi medjubankarski nalog");
		rset.close();
		stmt.close();
		
	}
	
	private static void putZaglavljeInNalogClearing(
			ClearingNalog nalog, int mbnId, BigDecimal ukupanIznos) throws SQLException{
		
		ZaglavljeClearingNaloga zaglavlje = nalog.getZaglavlje();
		zaglavlje.setUkupanIznos(ukupanIznos);
		String sql = "SELECT bankDuz.BANK_SWIFT, bankDuz.BANK_OBR_RACUN, " +
				"bankPov.BANK_SWIFT, bankPov.BANK_OBR_RACUN , mbn.MBN_DATUM " +
				"FROM MEDJUBANKARSKI_NALOG mbn " +
				"JOIN BANKA bankDuz ON bankDuz.BANK_SIFRA = mbn.BANK_SIFRA_DUZNIK " +
				"JOIN BANKA bankPov ON bankPov.BANK_SIFRA = mbn.BANK_SIFRA_POVERIOC " +
				"WHERE mbn.MBN_ID = " + mbnId;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			zaglavlje.setIDPoruke("" + mbnId);
			zaglavlje.setSWIFTKodBankeDuznika(rset.getString(1));
			zaglavlje.setObracunskiRacunBankeDuznika(rset.getString(2));
			zaglavlje.setSWIFTKodBankePoverioca(rset.getString(3));
			zaglavlje.setObracunskiRacunBankePoverioca(rset.getString(4));
			GregorianCalendar calendarValute = new GregorianCalendar();
			calendarValute.setTime(rset.getDate(5));
			try {
				zaglavlje.setDatumValute(DatatypeFactory.newInstance().
						newXMLGregorianCalendar(calendarValute));
				zaglavlje.getDatumValute().setTimezone(DatatypeConstants.FIELD_UNDEFINED);
				zaglavlje.setDatum(DatatypeFactory.newInstance().
						newXMLGregorianCalendar(calendarValute));
				zaglavlje.getDatum().setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			
		}
		else
			throw new SQLException("Nije nadjen traženi medjubankarski nalog");
		rset.close();
		stmt.close();
		
	}
	
	private static void putStavkeInNalogRTGS(RTGSNalog nalog, int mbnId) throws SQLException {
		String sql = "SELECT asi.ASI_DUZNIK, asi.ASI_SVRHA, asi.ASI_POVERILAC, " +
				"asi.ASI_DATPRI, asi.DSR_DATUM, asi.ASI_RACDUZ, asi.ASI_MODZAD, asi.ASI_PBZAD, " +
				"asi.ASI_RACPOV, asi.ASI_MODODOB, asi.ASI_PBODO, asi.ASI_IZNOS, asi.VA_SIFRA " +
				"FROM VEZA_MEDJUBANKARSKOG_NALOGA_I_STAVKI vmb " +
				"JOIN ANALITIKA_IZVODA asi  ON asi.BAR_RACUN = vmb.BAR_RACUN " +
				"AND asi.DSR_DATUM = vmb.DSR_DATUM AND asi.DSR_IZVOD = vmb.DSR_IZVOD " +
				"AND asi.ASI_BROJSTAVKE = vmb.ASI_BROJSTAVKE " +
				"WHERE vmb.MBN_ID = " + mbnId;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			nalog.setDuznikNalogodavac(rset.getString(1));
			nalog.setSvrhaPlacanja(rset.getString(2));
			nalog.setPrimalacPoverilac(rset.getString(3));
			GregorianCalendar calendarNaloga = new GregorianCalendar();
			calendarNaloga.setTime(rset.getDate(4));
			try {
				nalog.setDatumNaloga(DatatypeFactory.newInstance().
						newXMLGregorianCalendar(calendarNaloga));
				nalog.getDatumNaloga().setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			GregorianCalendar calendarValute = new GregorianCalendar();
			calendarValute.setTime(rset.getDate(5));
			try {
				nalog.setDatumValute(DatatypeFactory.newInstance().
						newXMLGregorianCalendar(calendarValute));
				nalog.getDatumValute().setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			nalog.setRacunDuznika(rset.getString(6));
			nalog.setModelZaduzenja(BigInteger.valueOf(rset.getLong(7)));
			nalog.setPozivNaBrojZaduzenja(rset.getString(8));
			nalog.setRacunPoverioca(rset.getString(9));
			nalog.setModelOdobrenja(BigInteger.valueOf(rset.getLong(10)));
			nalog.setPozivNaBrojOdobrenja(rset.getString(11));
			nalog.setIznos(rset.getBigDecimal(12));
			nalog.setSifraValute(rset.getString(13));
			if(rset.next()){
				throw new SQLException("RTGS Nalog ima vise od jedne stavke izvoda u bazi podataka!");
			}
		}
		else
			throw new SQLException("Nije nadjen traženi medjubankarski nalog");
		rset.close();
		stmt.close();
		
	}
	
	private static BigDecimal putStavkeInNalogClearing(
			ClearingNalog nalog, int mbnId) throws SQLException {

		BigDecimal ukupanIznos = new BigDecimal(0);
		
		String sql = "SELECT asi.ASI_DUZNIK, asi.ASI_SVRHA, asi.ASI_POVERILAC, " +
				"asi.ASI_DATPRI, asi.ASI_RACDUZ, asi.ASI_MODZAD, asi.ASI_PBZAD, " +
				"asi.ASI_RACPOV, asi.ASI_MODODOB, asi.ASI_PBODO, asi.ASI_IZNOS, asi.VA_SIFRA, asi.ASI_BROJSTAVKE " +
				"FROM VEZA_MEDJUBANKARSKOG_NALOGA_I_STAVKI vmb " +
				"JOIN ANALITIKA_IZVODA asi  ON asi.BAR_RACUN = vmb.BAR_RACUN " +
				"AND asi.DSR_DATUM = vmb.DSR_DATUM AND asi.DSR_IZVOD = vmb.DSR_IZVOD " +
				"AND asi.ASI_BROJSTAVKE = vmb.ASI_BROJSTAVKE " +
				"WHERE vmb.MBN_ID = " + mbnId;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		while(rset.next()){
			StavkaClearingNaloga stavka = new StavkaClearingNaloga();
			stavka.setDuznikNalogodavac(rset.getString(1));
			stavka.setSvrhaPlacanja(rset.getString(2));
			stavka.setPrimalacPoverilac(rset.getString(3));
			GregorianCalendar calendarNaloga = new GregorianCalendar();
			calendarNaloga.setTime(rset.getDate(4));
			try {
				stavka.setDatumNaloga(DatatypeFactory.newInstance().
						newXMLGregorianCalendar(calendarNaloga));
				stavka.getDatumNaloga().setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			stavka.setRacunDuznika(rset.getString(5));
			stavka.setModelZaduzenja(BigInteger.valueOf(rset.getLong(6)));
			stavka.setPozivNaBrojZaduzenja(rset.getString(7));
			stavka.setRacunPoverioca(rset.getString(8));
			stavka.setModelOdobrenja(BigInteger.valueOf(rset.getLong(9)));
			stavka.setPozivNaBrojOdobrenja(rset.getString(10));
			stavka.setIznos(rset.getBigDecimal(11));
			stavka.setSifraValute(rset.getString(12));
			stavka.setIDNalogaZaPlacanje(rset.getString(13));
			ukupanIznos = ukupanIznos.add(stavka.getIznos());
			nalog.getStavka().add(stavka);
			nalog.getZaglavlje().setSifraValute(stavka.getSifraValute());
		}
		rset.close();
		stmt.close();
		
		return ukupanIznos;
	}
	
	private static void updateNalogPoslato(int mbnId) throws SQLException{
		
		String sql = "UPDATE MEDJUBANKARSKI_NALOG SET MBN_POSLATO = ? WHERE MBN_ID = ? ";
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
		stmt.setBoolean(1, true);
		stmt.setInt(2, mbnId);
		int affectedRows = stmt.executeUpdate();
		if(affectedRows != 1)
			throw new SQLException("Affected rows u updateNalogPoslato nije jednako 1 : " + affectedRows);
		stmt.close();
	}

	
}
