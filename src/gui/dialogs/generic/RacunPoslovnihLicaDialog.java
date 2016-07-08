package gui.dialogs.generic;

import gui.MainFrame;
import gui.actions.dialog.GenerateControlNumberAction;
import gui.custom.JLimitTextField;
import gui.dialogs.PotvrdaUkidanjaDialog;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import utils.CurrentBank;
import components.JDigitsTextField;

@SuppressWarnings("serial")
public class RacunPoslovnihLicaDialog extends GenericDialog {

	private JDigitsTextField tfBrojRacuna;
	private JDigitsTextField tfIDBrojPoslovnogLica;
	private JLimitTextField tfSifraValute;
	private JDigitsTextField tfSifraBanke;
	private JCheckBox cbVazeci;
	private JDatePickerImpl dpDatum;
	
	public RacunPoslovnihLicaDialog(JFrame parent) {
		super(parent, "Račun poslovnih lica", "RACUN_POSLOVNIH_LICA", false);

		btnDelete.setIcon(new ImageIcon("images/delete2a.gif"));
		btnDelete.setVisible(false);
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row=tableGrid.getSelectedRow();
				
				PotvrdaUkidanjaDialog dialog=new PotvrdaUkidanjaDialog(thisDialog, tableGrid.getModel().getValueAt(row, 0).toString(),(java.sql.Date) tableGrid.getModel().getValueAt(row, 4));
				dialog.setVisible(true);
				try {
					getGenTableModel().open();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		formButtonsPanel.add(btnDelete);
	}

	@Override
	protected void initializeFormInputPanel() {
		tfBrojRacuna = new JDigitsTextField(14, 18, false, true);
		tfBrojRacuna.setName("BAR_RACUN");
		
		btnGenerisiKontrolniBroj = new JButton(
				"Generisanje kontrolnog broja");
		btnGenerisiKontrolniBroj
				.addActionListener(new GenerateControlNumberAction(
						tfBrojRacuna,RacunPoslovnihLicaDialog.this));
		btnGenerisiKontrolniBroj.setEnabled(false);
		
		tfBrojRacuna.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				kontrolniBrojGenerisan=false;
				if (tfBrojRacuna.getText().trim().length()>=16)
					btnGenerisiKontrolniBroj.setEnabled(true);
				else
					btnGenerisiKontrolniBroj.setEnabled(false);

			}

			@Override
			public void keyPressed(KeyEvent e) {
			
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
		JPanel panelRacun = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelRacun.add(tfBrojRacuna);
		panelRacun.add(btnGenerisiKontrolniBroj);
		addComponentToFormInputPanel(panelRacun, "Broj računa", true);
		
		
		
		
		JPanel panIDBroj = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfIDBrojPoslovnogLica = new JDigitsTextField(8, 10, true, true);
		tfIDBrojPoslovnogLica.setName("L_ID");
		tfIDBrojPoslovnogLica.setEditable(false);
		JButton btnZoom1 = new JButton("...");
		btnZoom1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PoslovnoLiceDialog dialog = new PoslovnoLiceDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfIDBrojPoslovnogLica.setText(dialog.getZoomMap().get(tfIDBrojPoslovnogLica.getName()));
			}
		});
		panIDBroj.add(tfIDBrojPoslovnogLica);
		panIDBroj.add(btnZoom1);
		addComponentToFormInputPanel(panIDBroj, "ID broj poslovnog lica", true);
		
		JPanel panSifraValute = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraValute = new JLimitTextField(3, 3);
		tfSifraValute.setName("VA_SIFRA");
		tfSifraValute.setEditable(false);
		JButton btnZoom2 = new JButton("...");
		btnZoom2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ValutaDialog dialog = new ValutaDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfSifraValute.setText(dialog.getZoomMap().get(tfSifraValute.getName()));
			}
		});
		panSifraValute.add(tfSifraValute);
		panSifraValute.add(btnZoom2);
		addComponentToFormInputPanel(panSifraValute, "Šifra valute", true);
		
		JPanel panSifraBanke = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraBanke = new JDigitsTextField(3, 3, true, true);
		tfSifraBanke.setName("BANK_SIFRA");
		tfSifraBanke.setEditable(false);
		tfSifraBanke.setText(CurrentBank.getId());

		panSifraBanke.add(tfSifraBanke);
		addComponentToFormInputPanel(panSifraBanke, "Šifra banke", true);
		
		JPanel panDatum = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePanelImpl datePanel = new JDatePanelImpl(new UtilDateModel());
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		AbstractFormatter formatter = new AbstractFormatter() {
			
			@Override
			public String valueToString(Object value) throws ParseException {
				Calendar cal = (Calendar)value;
				if (cal == null) {
					return "";
				}
				return format.format(cal.getTime());
			}
			
			@Override
			public Object stringToValue(String text) throws ParseException {
				if (text == null || text.equals("")) {
					return null;
				}
				Date date = format.parse(text);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				return calendar;
			}
		};
		dpDatum = new JDatePickerImpl(datePanel, formatter);
		dpDatum.getJFormattedTextField().setName("BAR_DATOTV");
		panDatum.add(dpDatum);
		addComponentToFormInputPanel(panDatum, "Datum otvaranja", true);
		
		cbVazeci = new JCheckBox();
		cbVazeci.setName("BAR_VAZI");
		cbVazeci.setSelected(true);
		cbVazeci.setEnabled(false);
		
		addComponentToFormInputPanel(cbVazeci, "Važeći?", false);

	}
	
	public JDigitsTextField getTfBrojRacuna() {
		return tfBrojRacuna;
	}

	@Override
	public void emptyTextField() {

		super.emptyTextField();
		btnDelete.setVisible(false);
		
		tfSifraBanke.setText(CurrentBank.getId());
	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		String idLica=tfIDBrojPoslovnogLica.getText();
		String brojRacuna=tfBrojRacuna.getText();
		String sifraBanke=tfSifraBanke.getText();
		String sifraValute=tfSifraValute.getText();
		String datumOtvaranja=dpDatum.getJFormattedTextField().getText();
		
		if ((idLica.trim().length()==0) || 
		(brojRacuna.trim().length()==0) ||
		(sifraBanke.trim().length()==0) ||
		(sifraValute.trim().length()==0) ||
		datumOtvaranja.trim().length()==0)
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		
		if (brojRacuna.length()!=18)
			throw new InvalidInputException("Račun mora imati tačno 18 cifara");
		String prvaTriZnakaRacuna=brojRacuna.substring(0,3 );
		
		if (!(prvaTriZnakaRacuna.equals(sifraBanke)))
			throw new InvalidInputException("Prve tri cifre računa moraju biti iste kao šifra banke");	
		if (!kontrolniBrojGenerisan)
			throw new InvalidInputException(
					"Kontrolni broj računa nije generisan");
	}

}
