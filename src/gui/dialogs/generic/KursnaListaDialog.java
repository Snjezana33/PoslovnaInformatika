package gui.dialogs.generic;

import gui.MainFrame;
import gui.dialogs.model.Lookup;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.DateFormat;
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

import org.eclipse.jdt.core.compiler.InvalidInputException;

import components.JDigitsTextField;

@SuppressWarnings("serial")
public class KursnaListaDialog extends GenericDialog {
	
	private JDigitsTextField tfSifraBanke;
	private JTextField tfNazivBanke;
	private JDigitsTextField tfBrKursneListe;
	private JDatePickerImpl dpDatum;
	private JDatePickerImpl dpPrimenjujeSeOd;
	
	private JButton btnZoom;
	
	private BankaDialog dlgBanka;

	public KursnaListaDialog(JFrame parent) {
		super(parent, "Kursna lista", "KURSNA_LISTA", false);
	}

	@Override
	protected void initializeFormInputPanel() {
		
		JPanel panBanka = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraBanke = new JDigitsTextField(3, 3, true, true);
		tfSifraBanke.setName("BANK_SIFRA");
		tfSifraBanke.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				String sifraBanke = tfSifraBanke.getText().trim();
				try {
					tfNazivBanke.setText(Lookup.getLookupColumnValue("BANKA", "BANK_NAZIV", "BANK_SIFRA", sifraBanke));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		btnZoom = new JButton("...");
		btnZoom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgBanka = new BankaDialog(MainFrame.getInstance());
				dlgBanka.setColumnsForZoom(getColumnsForZoom());
				dlgBanka.getToolbar().getZoomAction().setEnabled(true);
				dlgBanka.setVisible(true);
				tfSifraBanke.setText(dlgBanka.getZoomMap().get(tfSifraBanke.getName()));
				tfNazivBanke.setText(dlgBanka.getZoomMap().get(tfNazivBanke.getName()));
			}
		});
		
		tfNazivBanke = new JTextField(20);
		tfNazivBanke.setName("BANK_NAZIV");
		tfNazivBanke.setEditable(false);
		panBanka.add(tfSifraBanke);
		panBanka.add(btnZoom);
		panBanka.add(tfNazivBanke);
		addComponentToFormInputPanel(panBanka, "Šifra banke", true);
		
		JPanel panDatum = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePanelImpl datePanel1 = new JDatePanelImpl(new UtilDateModel());
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
		
		dpDatum = new JDatePickerImpl(datePanel1, formatter);
		dpDatum.getJFormattedTextField().setName("KL_DATUM");
		panDatum.add(dpDatum);
		addComponentToFormInputPanel(panDatum, "Datum", true);
		
		tfBrKursneListe = new JDigitsTextField(3, 3, true, false);
		tfBrKursneListe.setName("KL_BROJ");
		addComponentToFormInputPanel(tfBrKursneListe, "Broj kursne liste", true);
		
		JPanel panPrimenjujeSeOd = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePanelImpl datePanel2 = new JDatePanelImpl(new UtilDateModel());
		dpPrimenjujeSeOd = new JDatePickerImpl(datePanel2, formatter);
		dpPrimenjujeSeOd.getJFormattedTextField().setName("KL_DATPR");
		panPrimenjujeSeOd.add(dpPrimenjujeSeOd);
		addComponentToFormInputPanel(panPrimenjujeSeOd, "Primenjuje se od", true);

	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		//boolean retVal = true;
		
		String nazivBanke=tfNazivBanke.getText();
		String redniBroj=tfBrKursneListe.getText();
		String datumPravljenja=dpDatum.getJFormattedTextField().getText();
		String datumPrimene=dpPrimenjujeSeOd.getJFormattedTextField().getText();
		
		if ((nazivBanke.trim().length()==0) || (redniBroj.trim().length()==0) || (datumPravljenja.trim().length()==0) || (datumPrimene.trim().length()==0))
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		String sdate1 = dpDatum.getJFormattedTextField().getText().trim();
		String sdate2 = dpPrimenjujeSeOd.getJFormattedTextField().getText().trim();
		Date date1 = null;
		Date date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date1 = sdf.parse(sdate1);
			date2 = sdf.parse(sdate2);
			if(date2.before(date1)){
				InvalidInputException ex=new InvalidInputException("Datum ne moza biti pre datuma kreiranja");
				throw ex;
				
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
