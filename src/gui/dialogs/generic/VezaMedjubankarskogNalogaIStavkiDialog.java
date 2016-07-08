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
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import components.JDigitsTextField;

@SuppressWarnings("serial")
public class VezaMedjubankarskogNalogaIStavkiDialog extends GenericDialog {
	
	private JDigitsTextField tfBrojRacuna;
	private JDigitsTextField tfBrojIzvoda;
	private JDigitsTextField tfBrojStavke;
	private JTextField tfIDNaloga;

	public VezaMedjubankarskogNalogaIStavkiDialog(JFrame parent) {
		super(parent, "Veza medjubankarskog naloga i stavki", "VEZA_MEDJUBANKARSKOG_NALOGA_I_STAVKI", false);
	}

	@Override
	protected void initializeFormInputPanel() {
		JPanel panBrojRacuna = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfBrojRacuna = new JDigitsTextField(14, 18, false, true);
		tfBrojRacuna.setName("BAR_RACUN");
		JButton btnZoom1 = new JButton("...");
		btnZoom1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AnalitikaIzvodaDialog dialog = new AnalitikaIzvodaDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfBrojRacuna.setText(dialog.getZoomMap().get(tfBrojRacuna.getName()));
				tfBrojIzvoda.setText(dialog.getZoomMap().get(tfBrojIzvoda.getName()));
				tfBrojStavke.setText(dialog.getZoomMap().get(tfBrojStavke.getName()));
			}
		});
		panBrojRacuna.add(tfBrojRacuna);
		panBrojRacuna.add(btnZoom1);
		addComponentToFormInputPanel(panBrojRacuna, "Broj računa", false);
		
		tfBrojIzvoda = new JDigitsTextField(3, 3, true, true);
		tfBrojIzvoda.setName("DSR_IZVOD");
		addComponentToFormInputPanel(tfBrojIzvoda, "Broj izvoda", false);
		
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
		addComponentToFormInputPanel(tfBrojStavke, "Broj stavke", false);
		
		JPanel panIDNaloga = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfIDNaloga = new JTextField(30);
		tfIDNaloga.setName("MBN_ID");
		JButton btnZoom2 = new JButton("...");
		btnZoom2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MedjubankarskiNalogDialog dialog = new MedjubankarskiNalogDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfIDNaloga.setText(dialog.getZoomMap().get(tfIDNaloga.getName()));
			}
		});
		panIDNaloga.add(tfIDNaloga);
		panIDNaloga.add(btnZoom2);
		addComponentToFormInputPanel(panIDNaloga, "ID naloga", false);

	}

	@Override
	protected void validateInputs() {
		// TODO Auto-generated method stub
		
	}

}
