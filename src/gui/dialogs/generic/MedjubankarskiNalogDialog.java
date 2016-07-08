package gui.dialogs.generic;

import gui.MainFrame;
import gui.actions.dialog.ExportMedjubankarskiNalogAction;

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
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import components.JDigitsTextField;

@SuppressWarnings("serial")
public class MedjubankarskiNalogDialog extends GenericDialog {

	private JTextField tfIDNaloga;
	private JDigitsTextField tfSifraBankePoverioca;
	private JDigitsTextField tfSifraBankeDuznika;
	private JComboBox cbTipNaloga;
	private JDatePickerImpl dpDatumNaloga;
	private JButton btnExport;
	
	private JCheckBox checkBoxPoslat;
	
	public MedjubankarskiNalogDialog(JFrame parent) {
		
		super(parent, "Međubankarski nalog", "MEDJUBANKARSKI_NALOG", false);
		btnExport.setVisible(false);
		formButtonsPanel.add(btnExport, 0);
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void initializeFormInputPanel() {
		
		
		btnExport = new JButton(new ExportMedjubankarskiNalogAction(this));

		
		tfIDNaloga = new JTextField(30);
		tfIDNaloga.setName("MBN_ID");
		addComponentToFormInputPanel(tfIDNaloga, "ID naloga", false);
		
		JPanel panSifraBankePoverioca = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraBankePoverioca = new JDigitsTextField(3, 3, true, true);
		tfSifraBankePoverioca.setName("BANK_SIFRA_POVERIOC");
		JButton btnZoom1 = new JButton("...");
		btnZoom1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BankaDialog dialog = new BankaDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfSifraBankePoverioca.setText(dialog.getZoomMap().get("BANK_SIFRA"));
			}
		});
		panSifraBankePoverioca.add(tfSifraBankePoverioca);
		panSifraBankePoverioca.add(btnZoom1);
		addComponentToFormInputPanel(panSifraBankePoverioca, "Šifra banke poverioca", true);
		
		JPanel panSifraBankeDuznika = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraBankeDuznika = new JDigitsTextField(3, 3, true, true);
		tfSifraBankeDuznika.setName("BANK_SIFRA_DUZNIK");
		JButton btnZoom2 = new JButton("...");
		btnZoom2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BankaDialog dialog = new BankaDialog(MainFrame.getInstance());
				dialog.setColumnsForZoom(getColumnsForZoom());
				dialog.getToolbar().getZoomAction().setEnabled(true);
				dialog.setVisible(true);
				tfSifraBankeDuznika.setText(dialog.getZoomMap().get("BANK_SIFRA"));
			}
		});
		panSifraBankeDuznika.add(tfSifraBankeDuznika);
		panSifraBankeDuznika.add(btnZoom2);
		addComponentToFormInputPanel(panSifraBankeDuznika, "Šifra banke dužnika", true);
		
		String[] items = new String[] {"R", "C"};
		cbTipNaloga = new JComboBox(items);
		cbTipNaloga.setName("MBN_TIP");
		cbTipNaloga.setSelectedIndex(0);
		addComponentToFormInputPanel(cbTipNaloga, "Tip naloga", false);
		
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
		dpDatumNaloga = new JDatePickerImpl(datePanel, formatter);
		dpDatumNaloga.getJFormattedTextField().setName("MBN_DATUM");
		panDatum.add(dpDatumNaloga);
		addComponentToFormInputPanel(panDatum, "Datum naloga", true);
		
		
		checkBoxPoslat = new JCheckBox();
		checkBoxPoslat.setName("MBN_POSLATO");
		
		addComponentToFormInputPanel(checkBoxPoslat, "Poslato?", true);

	}
	
	@Override
	public void sync() {
		super.sync();
		btnExport.setVisible(true);
		if (tableGrid.getSelectedRow() != -1 && (boolean)tableGrid.getValueAt(tableGrid.getSelectedRow(), 5)==false)
			btnExport.setVisible(true);
		else
			btnExport.setVisible(false);
		
	}

	@Override
	protected void validateInputs() {
		
	}

}
