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

import org.eclipse.jdt.core.compiler.InvalidInputException;

import components.JDigitsTextField;

@SuppressWarnings("serial")
public class UkidanjeDialog extends GenericDialog {

	private JDigitsTextField tfBrojRacuna;
	private JDigitsTextField tfRacunPrenosa;
	private JDatePickerImpl dpDatum;
	
	public UkidanjeDialog(JFrame parent) {
		super(parent, "Ukidanje", "UKIDANJE", true);
	}

	@Override
	protected void initializeFormInputPanel() {
		JPanel panBrojRacuna = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		dpDatum.getJFormattedTextField().setName("UK_DATUKIDANJA");
		panDatum.add(dpDatum);
		addComponentToFormInputPanel(panDatum, "Datum ukidanja", true);
		
		tfRacunPrenosa = new JDigitsTextField(14, 20, false, true);
		tfRacunPrenosa.setName("UK_NARACUN");
		addComponentToFormInputPanel(tfRacunPrenosa, "Sredstva se prenose na račun", true);

	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		String datumUkidanja=dpDatum.getJFormattedTextField().getText();
		String racunPrenosa=tfRacunPrenosa.getText();
		
		if ((datumUkidanja.trim().length()==0) || (racunPrenosa.trim().length()==0))
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		
		if (racunPrenosa.length()!=18)
			throw new InvalidInputException("Račun na koji se sredstva prenose mora imati 18 cifara");
	}

}
