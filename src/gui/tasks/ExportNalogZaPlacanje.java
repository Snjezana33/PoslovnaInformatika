package gui.tasks;

import db.DBConnection;
import exceptions.InvalidExportException;
import gui.tasks.entities.nalog.za.placanje.NalogZaPlacanje;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

public class ExportNalogZaPlacanje {

	/**
	 * Main metoda za testiranje klase
	 */
	public static void main(String[] args) {
		
		
		try {
			File file = new File("./testExportResults");
			exportNalog(new FileOutputStream(new File(file,"NalogZaPlacanje.xml")));
			
			System.out.println("SUCCESS");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static NalogZaPlacanje exportNalog(OutputStream output) 
			throws SQLException, InvalidExportException, JAXBException{
		
		NalogZaPlacanje Nalog = new NalogZaPlacanje();

		putStavkeInNalog(Nalog);
		
		JAXBContext jaxbContext = JAXBContext
								.newInstance(NalogZaPlacanje.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				true);
		jaxbMarshaller.marshal(Nalog, output);
		
		DBConnection.getConnection().commit();
		System.out.println("Uspesno eksportovan nalog za placanje!");

		return Nalog;
	}
	
	
	
	private static void putStavkeInNalog(NalogZaPlacanje nalog) throws SQLException {
		String sql = "SELECT asi.ASI_DUZNIK, asi.ASI_SVRHA, asi.ASI_POVERILAC, " +
				"asi.ASI_DATPRI, asi.DSR_DATUM, asi.ASI_RACDUZ, asi.ASI_MODZAD, asi.ASI_PBZAD, " +
				"asi.ASI_RACPOV, asi.ASI_MODODOB, asi.ASI_PBODO, asi.ASI_IZNOS, asi.VA_SIFRA " +
				"FROM VEZA_MEDJUBANKARSKOG_NALOGA_I_STAVKI vmb " +
				"JOIN ANALITIKA_IZVODA asi  ON asi.BAR_RACUN = vmb.BAR_RACUN " +
				"AND asi.DSR_DATUM = vmb.DSR_DATUM AND asi.DSR_IZVOD = vmb.DSR_IZVOD " +
				"AND asi.ASI_BROJSTAVKE = vmb.ASI_BROJSTAVKE " ;
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			nalog.setIDPoruke(""+ Math.random());
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
			nalog.setOznakaValute(rset.getString(13));
			/*if(rset.next()){
				throw new SQLException("Nalog ima vise od jedne stavke izvoda u bazi podataka!");
			}*/
		}
		else
			throw new SQLException("Nije nadjen traženi nalog");
		rset.close();
		stmt.close();
		
	}
	

	
}
