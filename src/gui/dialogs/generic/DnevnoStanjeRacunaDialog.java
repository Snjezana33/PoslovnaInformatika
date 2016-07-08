package gui.dialogs.generic;

import gui.MainFrame;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import components.JDigitsTextField;
import components.JIznosTextField;

@SuppressWarnings("serial")
public class DnevnoStanjeRacunaDialog extends GenericDialog {
	
	JDigitsTextField tfBrojRacuna;
	JDigitsTextField tfBrojIzvoda;
	JIznosTextField tfPrethodnoStanje;
	JIznosTextField tfPrometUKorist;
	JIznosTextField tfPrometNaTeret;
	JIznosTextField tfNovoStanje;
	JButton btnGeneratePresek;
	
	public DnevnoStanjeRacunaDialog(JFrame parent) {
		super(parent, "Dnevno stanje računa", "DNEVNO_STANJE_RACUNA", true);
	}

	@Override
	protected void initializeFormInputPanel() {
		
	
		JPanel panBrojRacuna=new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfBrojRacuna = new JDigitsTextField(14, 18, false, true);
		tfBrojRacuna.setName("BAR_RACUN");
		JButton btnZoom = new JButton("...");
		btnZoom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RacunPoslovnihLicaDialog dialog = new RacunPoslovnihLicaDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfBrojRacuna.setText(dialog.getZoomMap().get(tfBrojRacuna.getName()));
			}
		});
		panBrojRacuna.add(tfBrojRacuna);
		panBrojRacuna.add(btnZoom);
		addComponentToFormInputPanel(panBrojRacuna, "Broj računa", true);
		
		tfBrojIzvoda = new JDigitsTextField(3, 3, true, true);
		tfBrojIzvoda.setName("DSR_IZVOD");
		addComponentToFormInputPanel(tfBrojIzvoda, "Broj izvoda", true);
		
		
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
		JDatePickerImpl dpDatum = new JDatePickerImpl(datePanel, formatter);
		dpDatum.getJFormattedTextField().setName("DSR_DATUM");
		panDatum.add(dpDatum);
		addComponentToFormInputPanel(panDatum, "Datum prometa", true);

		tfPrethodnoStanje = new JIznosTextField(14, 17, 2, true);
		tfPrethodnoStanje.setName("DSR_PRETHODNO");
		addComponentToFormInputPanel(tfPrethodnoStanje, "Prethodno stanje", true);
		
		tfPrometUKorist = new JIznosTextField(14, 17, 2, true);
		tfPrometUKorist.setName("DSR_UKORIST");
		addComponentToFormInputPanel(tfPrometUKorist, "Promet u korist", true);
		
		tfPrometNaTeret = new JIznosTextField(14, 17, 2, true);
		tfPrometNaTeret.setName("DSR_NATERET");
		addComponentToFormInputPanel(tfPrometNaTeret, "Promet na teret", true);
		
		tfNovoStanje = new JIznosTextField(14, 17, 2, true);
		tfNovoStanje.setName("DSR_NOVOSTANJE");
		addComponentToFormInputPanel(tfNovoStanje, "Novo stanje", true);
	}

	@Override

	protected void validateInputs() {}
		

	public void sync() {
		super.sync();
		
	}
	

	

}
