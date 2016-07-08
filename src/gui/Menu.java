package gui;

import gui.actions.ImportNalogZaPlacanjeAction;
import gui.dialogs.LoginDialog;
import gui.dialogs.generic.AnalitikaIzvodaDialog;
import gui.dialogs.generic.BankaDialog;
import gui.dialogs.generic.DnevnoStanjeRacunaDialog;
import gui.dialogs.generic.DrzavaDialog;
import gui.dialogs.generic.KursUValutiDialog;
import gui.dialogs.generic.KursnaListaDialog;
import gui.dialogs.generic.MedjubankarskiNalogDialog;
import gui.dialogs.generic.PoslovnoLiceDialog;
import gui.dialogs.generic.RacunPoslovnihLicaDialog;
import gui.dialogs.generic.UkidanjeDialog;
import gui.dialogs.generic.ValutaDialog;
import gui.dialogs.generic.VezaMedjubankarskogNalogaIStavkiDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import utils.LookAndFeel;
import db.DBConnection;

@SuppressWarnings("serial")
public class Menu extends JMenuBar {
	
	private JMenu mnuBanka = new JMenu("File");
	private JMenu mnuOperacije = new JMenu("Operacije");
	private JMenu mnuKorisnik=new JMenu("Gost");
	private JMenu mnuIzvestaji = new JMenu("Izveštaji");
	private JMenu Izgled=new JMenu("Izgled");
	private JMenuItem itemSpisakRacuna = new JMenuItem("Spisak računa zadate banke");
	private JMenuItem itemDrzave = new JMenuItem("Države");
	private JMenuItem itemValuta = new JMenuItem("Valuta");
	private JMenuItem itemBanka = new JMenuItem("Banka");
	private JMenuItem itemKursnaLista = new JMenuItem("Kursna lista");
	private JMenuItem itemKursUValuti = new JMenuItem("Kurs u valuti");
	private JMenuItem itemPoslovnoLice = new JMenuItem("Poslovno lice");
	private JMenuItem itemRacunPoslovnihLica = new JMenuItem("Račun poslovnih lica"); 
	private JMenuItem itemUkidanje = new JMenuItem("Ukidanje");
	private JMenuItem itemDnevnoStanjeRacuna=new JMenuItem("Dnevno stanje računa");
	private JMenuItem itemAnalitikaIzvoda=new JMenuItem("Analitika izvoda");
	private JMenuItem itemMedjubankarskiNalog = new JMenuItem("Međubankarski nalog");
	private JMenuItem itemVezaMedjubankarskogNalogaIStavke = new JMenuItem("Veza međubankarskog naloga i stavki");
	private JMenuItem itemOdjaviSe=new JMenuItem("Odjavi se");
	private Action actionImportNalog = new ImportNalogZaPlacanjeAction();
	
	private void fillLFS(){
		Map<String, String> lfs = LookAndFeel.list();
		for(final String className : lfs.keySet()){
			JMenuItem mn = new JMenuItem(lfs.get(className));
			mn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					LookAndFeel.setLookAdnFeel(className);
				}
			});
			Izgled.add(mn);
		}
	}
	
	public Menu() {
		super();
		
		fillLFS();
		
		mnuBanka.add(itemDrzave);
		mnuBanka.add(itemValuta);
		mnuBanka.add(itemBanka);
		mnuBanka.add(itemKursnaLista);
		mnuBanka.add(itemKursUValuti);
		mnuBanka.add(itemPoslovnoLice);
		mnuBanka.add(itemRacunPoslovnihLica);
		mnuBanka.add(itemUkidanje);
		mnuBanka.add(itemDnevnoStanjeRacuna);
		mnuBanka.add(itemAnalitikaIzvoda);
		mnuBanka.add(itemMedjubankarskiNalog);
		mnuBanka.add(itemVezaMedjubankarskogNalogaIStavke);
		mnuOperacije.add(actionImportNalog);
		mnuIzvestaji.add(itemSpisakRacuna);
		mnuKorisnik.add(itemOdjaviSe);
		add(mnuBanka);
		add(mnuOperacije);
		add(mnuIzvestaji);
		add(Izgled);
		add(mnuKorisnik);
		
		itemDrzave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DrzavaDialog dialog = new DrzavaDialog(MainFrame.getInstance());
				dialog.setVisible(true);
				
			}
			
			
		});
		
		itemValuta.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ValutaDialog dialog = new ValutaDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemBanka.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BankaDialog dialog = new BankaDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemKursnaLista.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				KursnaListaDialog dialog = new KursnaListaDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemKursUValuti.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				KursUValutiDialog dialog = new KursUValutiDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemPoslovnoLice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PoslovnoLiceDialog dialog=new PoslovnoLiceDialog(MainFrame.getInstance());
				dialog.setVisible(true);
				
			}
		});
		
		itemRacunPoslovnihLica.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RacunPoslovnihLicaDialog dialog = new RacunPoslovnihLicaDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemUkidanje.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UkidanjeDialog dialog = new UkidanjeDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemDnevnoStanjeRacuna.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DnevnoStanjeRacunaDialog dialog = new DnevnoStanjeRacunaDialog(MainFrame.getInstance());
				
				dialog.setVisible(true);
			}
		});
			
		itemAnalitikaIzvoda.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AnalitikaIzvodaDialog dialog = new AnalitikaIzvodaDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
			
		itemMedjubankarskiNalog.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MedjubankarskiNalogDialog dialog = new MedjubankarskiNalogDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemVezaMedjubankarskogNalogaIStavke.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				VezaMedjubankarskogNalogaIStavkiDialog dialog = new VezaMedjubankarskogNalogaIStavkiDialog(MainFrame.getInstance());
				dialog.setVisible(true);
			}
		});
		
		itemOdjaviSe.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				MainFrame.getInstance().setCurrentUser(null);
				mnuKorisnik.setText("Gost");
				LoginDialog dialog=new LoginDialog();
				dialog.setVisible(true);
			}
		});
		
		itemSpisakRacuna.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
			          //System.out.println(getClass().getResource("Izvestaji/SpisakRacuna.jasper"));
			          InputStream inputStream = new FileInputStream("Izvestaji/SpisakRacuna.jasper");
			          JasperPrint jp = JasperFillManager.fillReport(inputStream,
			          null, DBConnection.getConnection());
			          JasperViewer jpv = new JasperViewer(jp,false);
			          //JasperViewer.viewReport(jp, false);
			          jpv.setSize(1000, 800);
			          jpv.setVisible(true);
			        } catch (Exception ex) {
			          ex.printStackTrace();
			        }
			}
		});
		
	}

	public JMenu getMnuKorisnik() {
		return mnuKorisnik;
	}

	public void setMnuKorisnik(JMenu mnuKorisnik) {
		this.mnuKorisnik = mnuKorisnik;
	}
	
	

}
