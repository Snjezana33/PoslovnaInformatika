package gui.dialogs.generic;

import gui.MainFrame;
import gui.actions.dialog.ExportMedjubankarskiNalogAction;
import gui.actions.dialog.ExportNalogZaPlacanjeAction;
import gui.custom.JLimitTextField;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import components.JDigitsTextField;
import components.JIznosTextField;

@SuppressWarnings("serial")
public class AnalitikaIzvodaDialog extends GenericDialog {
	
	private JDigitsTextField tfBrojRacuna;
	private JDigitsTextField tfBrojIzvoda;
	private JDigitsTextField tfBrojStavke;
	private JLimitTextField tfSifraValute;
	private JTextField tfDuznikNalogodavac;
	private JTextField tfSvrhaPlacanja;
	private JTextField tfPoverilacPrimalac;
	private JDigitsTextField tfRacunDuznika;
	private JDigitsTextField tfModelZaduzenja;
	private JDigitsTextField tfPozivNaBrojZaduzenja;
	private JDigitsTextField tfRacunPoverioca;
	private JDigitsTextField tfModelOdobrenja;
	private JDigitsTextField tfPozivNaBrojOdobrenja;
	private JCheckBox cbHitno;
	private JIznosTextField tfIznos;
	@SuppressWarnings("rawtypes")
	private JComboBox cbTipGreske;
	@SuppressWarnings("rawtypes")
	private JComboBox cbStatus;
	
	private DnevnoStanjeRacunaDialog dlgDnevnoStanjeRacuna;
	private ValutaDialog dlgValuta;
	private JButton btnExport;
	
	public AnalitikaIzvodaDialog(JFrame parent) {
		
		super(parent, "Analitika izvoda", "ANALITIKA_IZVODA", true);
		tableGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//btnExport.setVisible(false);
		formButtonsPanel.add(btnExport, 0);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void initializeFormInputPanel() {
		JPanel panBrojRacuna = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfBrojRacuna = new JDigitsTextField(14, 18, false, true);
		tfBrojRacuna.setName("BAR_RACUN");
		tfBrojIzvoda = new JDigitsTextField(3, 3, true, true);
		tfBrojIzvoda.setName("DSR_IZVOD");
		btnExport = new JButton(new ExportNalogZaPlacanjeAction(this));
		JButton btnZoom1 = new JButton("...");
		btnZoom1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgDnevnoStanjeRacuna = new DnevnoStanjeRacunaDialog(MainFrame.getInstance());
				dlgDnevnoStanjeRacuna.setColumnsForZoom(getColumnsForZoom());
				dlgDnevnoStanjeRacuna.getToolbar().getZoomAction().setEnabled(true);
				dlgDnevnoStanjeRacuna.setVisible(true);
				tfBrojRacuna.setText(dlgDnevnoStanjeRacuna.getZoomMap().get(tfBrojRacuna.getName()));
				tfBrojIzvoda.setText(dlgDnevnoStanjeRacuna.getZoomMap().get(tfBrojIzvoda.getName()));}
		});
		
		panBrojRacuna.add(tfBrojRacuna);
		panBrojRacuna.add(btnZoom1);
		addComponentToFormInputPanel(panBrojRacuna, "Broj računa", true);
		addComponentToFormInputPanel(tfBrojIzvoda, "Broj izvoda", true);
		
		JPanel panDatumPrometa = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		JDatePickerImpl dpDatumPrometa = new JDatePickerImpl(datePanel, formatter);
		dpDatumPrometa.getJFormattedTextField().setName("DSR_DATUM");
		panDatumPrometa.add(dpDatumPrometa);
		addComponentToFormInputPanel(panDatumPrometa, "Datum prometa", true);
		
		tfBrojStavke = new JDigitsTextField(6, 8, true, true);
		tfBrojStavke.setName("ASI_BROJSTAVKE");
		addComponentToFormInputPanel(tfBrojStavke, "Broj stavke", true);
		
		JPanel panSifraValute = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraValute = new JLimitTextField(3, 3);
		tfSifraValute.setName("VA_SIFRA");
		JButton btnZoom2 = new JButton("...");
		btnZoom2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgValuta = new ValutaDialog(MainFrame.getInstance());
				dlgValuta.setColumnsForZoom(getColumnsForZoom());
				dlgValuta.getToolbar().getZoomAction().setEnabled(true);
				dlgValuta.setVisible(true);
				tfSifraValute.setText(dlgValuta.getZoomMap().get(tfSifraValute.getName()));}
		});
		
		panSifraValute.add(tfSifraValute);
		panSifraValute.add(btnZoom2);
		addComponentToFormInputPanel(panSifraValute, "Šifra valute", false);
		
		tfDuznikNalogodavac = new JTextField(30);
		tfDuznikNalogodavac.setName("ASI_DUZNIK");
		addComponentToFormInputPanel(tfDuznikNalogodavac, "Dužnik - nalogodavac", true);
		
		tfSvrhaPlacanja = new JTextField(30);
		tfSvrhaPlacanja.setName("ASI_SVRHA");
		addComponentToFormInputPanel(tfSvrhaPlacanja, "Svrha plaćanja", true);
		
		tfPoverilacPrimalac = new JTextField(30);
		tfPoverilacPrimalac.setName("ASI_POVERILAC");
		addComponentToFormInputPanel(tfPoverilacPrimalac, "Poverilac - primalac", true);
		
		/***********************************************************/
		
		JPanel panDatumValute = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePanelImpl datePanel2 = new JDatePanelImpl(new UtilDateModel());
		JDatePickerImpl dpDatumValute = new JDatePickerImpl(datePanel2, formatter);
		dpDatumValute.getJFormattedTextField().setName("ASI_DATPRI");
		panDatumValute.add(dpDatumValute);
		addComponentToFormInputPanel(panDatumValute, "Datum prijema", true);
		
		tfRacunDuznika = new JDigitsTextField(14, 18, false, true);
		tfRacunDuznika.setName("ASI_RACDUZ");
		addComponentToFormInputPanel(tfRacunDuznika, "Račun dužnika", true);
		
		tfModelZaduzenja = new JDigitsTextField(2, 2, true, true);
		tfModelZaduzenja.setName("ASI_MODZAD");
		addComponentToFormInputPanel(tfModelZaduzenja, "Model zaduženja", false);
		
		tfPozivNaBrojZaduzenja = new JDigitsTextField(14, 20, false, true);
		tfPozivNaBrojZaduzenja.setName("ASI_PBZAD");
		addComponentToFormInputPanel(tfPozivNaBrojZaduzenja, "Poziv na broj zaduženja", false);
		
		tfRacunPoverioca = new JDigitsTextField(14, 18, false, true);
		tfRacunPoverioca.setName("ASI_RACPOV");
		addComponentToFormInputPanel(tfRacunPoverioca, "Račun poverioca", true);
		
		tfModelOdobrenja = new JDigitsTextField(2, 2, true, true);
		tfModelOdobrenja.setName("ASI_MODODOB");
		addComponentToFormInputPanel(tfModelOdobrenja, "Model odobrenja", false);
		
		tfPozivNaBrojOdobrenja = new JDigitsTextField(14, 20, false, true);
		tfPozivNaBrojOdobrenja.setName("ASI_PBODO");
		addComponentToFormInputPanel(tfPozivNaBrojOdobrenja, "Poziv na broj odobrenja", false);
		
		cbHitno = new JCheckBox();
		cbHitno.setName("ASI_HITNO");
		addComponentToFormInputPanel(cbHitno, "Hitno?", true);
		
		tfIznos = new JIznosTextField(14, 17, 2, true);
		tfIznos.setName("ASI_IZNOS");
		addComponentToFormInputPanel(tfIznos, "Iznos", true);
		
		String[] items = new String[] {"1", "2", "3", "8", "9"};
		cbTipGreske = new JComboBox(items);
		cbTipGreske.setName("ASI_TIPGRESKE");
		addComponentToFormInputPanel(cbTipGreske, "Tip greške", true);
		
		String[] items2 = new String[] {"E", "P"};
		cbStatus = new JComboBox(items2);
		cbStatus.setName("ASI_STATUS");
		addComponentToFormInputPanel(cbStatus, "Status", false);
		
		

	}

	@Override
	protected void validateInputs() {
		
	}

}
