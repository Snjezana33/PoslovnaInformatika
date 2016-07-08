package gui.dialogs.generic;

import gui.MainFrame;
import gui.custom.JLimitTextField;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import components.JDigitsTextField;
import components.JIznosTextField;

@SuppressWarnings("serial")
public class KursUValutiDialog extends GenericDialog {
	
	private JDigitsTextField tfSifraBanke;
	private JTextField tfDatum;
	private JDigitsTextField tfRedniBr;
	private JLimitTextField tfSifraValutePrema;
	private JLimitTextField tfSifraOsnovneValute;
	private JIznosTextField tfKupovni;
	private JIznosTextField tfSrednji;
	private JIznosTextField tfProdajni;
	
	private KursnaListaDialog dlgKursnaLista;
	private ValutaDialog dlgValuta;

	public KursUValutiDialog(JFrame parent) {
		super(parent, "Kurs u valuti", "KURS_U_VALUTI", false);
	}

	@Override
	protected void initializeFormInputPanel() {
		tableGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JPanel panBanka = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraBanke = new JDigitsTextField(3, 3, true, true);
		tfSifraBanke.setName("BANK_SIFRA");
		tfSifraBanke.setEditable(false);
		tfDatum = new JTextField(10);
		tfDatum.setName("KL_DATUM");
		tfDatum.setEditable(false);
		JButton btnZoom1 = new JButton("...");
		btnZoom1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgKursnaLista = new KursnaListaDialog(MainFrame.getInstance());
				dlgKursnaLista.setColumnsForZoom(getColumnsForZoom());
				dlgKursnaLista.getToolbar().getZoomAction().setEnabled(true);
				dlgKursnaLista.setVisible(true);
				tfSifraBanke.setText(dlgKursnaLista.getZoomMap().get(tfSifraBanke.getName()));
				tfDatum.setText(dlgKursnaLista.getZoomMap().get(tfDatum.getName()));
				
			}
		});
		panBanka.add(tfSifraBanke);
		panBanka.add(btnZoom1);
		addComponentToFormInputPanel(panBanka, "Šifra banke", true);
		addComponentToFormInputPanel(tfDatum, "Datum", true);
		
		tfRedniBr = new JDigitsTextField(2, 2, true, false);
		tfRedniBr.setName("KLS_RBR");
		addComponentToFormInputPanel(tfRedniBr, "Redni broj", true);
		
		JPanel panSifraValutePrema = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraValutePrema = new JLimitTextField(3, 3);
		tfSifraValutePrema.setName("VA_SIFRA_PREMA");
		tfSifraValutePrema.setEditable(false);
		JButton btnZoom2 = new JButton("...");
		btnZoom2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgValuta = new ValutaDialog(MainFrame.getInstance());
				dlgValuta.setColumnsForZoom(getColumnsForZoom());
				dlgValuta.getToolbar().getZoomAction().setEnabled(true);
				dlgValuta.setVisible(true);
				tfSifraValutePrema.setText(dlgValuta.getZoomMap().get("VA_SIFRA"));
			}
		});
		panSifraValutePrema.add(tfSifraValutePrema);
		panSifraValutePrema.add(btnZoom2);
		addComponentToFormInputPanel(panSifraValutePrema, "Šifra valute prema", true);
		
		JPanel panSifraOsnovneValute = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraOsnovneValute = new JLimitTextField(3, 3);
		tfSifraOsnovneValute.setName("VA_SIFRA_OSNOVNA");
		tfSifraOsnovneValute.setEditable(false);
		JButton btnZoom3 = new JButton("...");
		btnZoom3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgValuta = new ValutaDialog(MainFrame.getInstance());
				dlgValuta.setColumnsForZoom(getColumnsForZoom());
				dlgValuta.getToolbar().getZoomAction().setEnabled(true);
				dlgValuta.setVisible(true);
				tfSifraOsnovneValute.setText(dlgValuta.getZoomMap().get("VA_SIFRA"));
			}
		});
		panSifraOsnovneValute.add(tfSifraOsnovneValute);
		panSifraOsnovneValute.add(btnZoom3);
		addComponentToFormInputPanel(panSifraOsnovneValute, "Šifra osnovne valute", true);
		
		tfKupovni = new JIznosTextField(13, 9, 4, false);
		tfKupovni.setName("KLS_KUPOVNI");
		addComponentToFormInputPanel(tfKupovni, "Kupovni", true);
		
		tfSrednji = new JIznosTextField(13, 9, 4, false);
		tfSrednji.setName("KLS_SREDNJI");
		addComponentToFormInputPanel(tfSrednji, "Srednji", true);
		
		tfProdajni = new JIznosTextField(13, 9, 4, false);
		tfProdajni.setName("KLS_PRODAJNI");
		addComponentToFormInputPanel(tfProdajni, "Prodajni", true);

	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		String sifraBanke=tfSifraBanke.getText();
		String redniBroj=tfRedniBr.getText();
		String srednji =tfSrednji.getText();
		String kupovni=tfKupovni.getText();
		String prodajni=tfProdajni.getText();
		String datum=tfDatum.getText();
		String osnovnaValuta=tfSifraOsnovneValute.getText();
		String premaValuti=tfSifraValutePrema.getText();
		
		if ((sifraBanke.trim().length()==0) ||
		(redniBroj.trim().length()==0) ||
		(srednji.trim().length()==0) ||
		(kupovni.trim().length()==0) ||
		(prodajni.trim().length()==0) ||
		(datum.trim().length()==0) ||
		(osnovnaValuta.trim().length()==0)||
		(premaValuti.trim().length()==0))
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		
		
	}

}
