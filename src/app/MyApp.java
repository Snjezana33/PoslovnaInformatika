package app;

import gui.MainFrame;
import gui.dialogs.LoginDialog;

import java.sql.SQLException;

import table.property.PropertiesContainer;
import utils.CurrentBank;
import xml.managment.TableExtractionManager;

public class MyApp {

	public static void main(String[] args) {
		PropertiesContainer.getInstance();
		MainFrame mf = MainFrame.getInstance();
		mf.setVisible(true);
		LoginDialog log = new LoginDialog();
		log.setVisible(true);

	}
	
/*	public static void main(String[] args) {
		
		TableExtractionManager.generateXML();
		
		//Pokretanje PropertiesContainera prvi put da bi odmah izgenerisali TablePropertije, da ne bi cekali kasnije u programu.
		try {
			CurrentBank.insertCurrentBankToDatabase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inicializovanje PropertiesContainera");
		PropertiesContainer.getInstance();
		MainFrame mf = MainFrame.getInstance();
		mf.setVisible(true);
		LoginDialog log = new LoginDialog();
		log.setVisible(true);
		
	}
*/
}
