package gui.dialogs;

import gui.actions.dialog.GenerateControlNumberUkidanjeAction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import components.JDigitsTextField;

import db.DBConnection;

public class PotvrdaUkidanjaDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected JPanel panInput;
	protected JPanel formButtonsPanel;
	protected JPanel panBig;
	protected String brojRacunaKojiSeUkida;
	protected JTextField txtBrojRacunaKojiSeUkida;
	protected JDigitsTextField txtRacunNaKojiSePrenosi;
    protected java.sql.Date datumOtvaranja;
    protected  JDatePickerImpl dpDatum;
    protected boolean kontrolniBrojGenerisan=false;
	protected JButton btnGenerisiKontrolniBroj;
	


	public PotvrdaUkidanjaDialog(final JDialog parent, String brojRacuna,java.sql.Date datumOtvaranja) {
		super(parent);

		brojRacunaKojiSeUkida = brojRacuna;
		setTitle("Ukidanje računa");
		//setPreferredSize(new Dimension(400, 300));
		setLocationRelativeTo(parent);
		setModal(true);
		this.datumOtvaranja=datumOtvaranja;
		panInput = new JPanel();
		panInput.setLayout(new BoxLayout(panInput, BoxLayout.Y_AXIS));

		JPanel panRacunKojiSeUkida = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblRacunKojiSeUkida = new JLabel("Broj računa :");
		lblRacunKojiSeUkida.setPreferredSize(new Dimension(150, 20));
		txtBrojRacunaKojiSeUkida = new JTextField(brojRacunaKojiSeUkida);
		txtBrojRacunaKojiSeUkida.setEditable(false);
		panRacunKojiSeUkida.add(lblRacunKojiSeUkida);
		panRacunKojiSeUkida.add(txtBrojRacunaKojiSeUkida);
		JLabel lblObavezanRacun=new JLabel("*");
		lblObavezanRacun.setForeground(Color.RED);
		panRacunKojiSeUkida.add(lblObavezanRacun);
		

		panInput.add(panRacunKojiSeUkida);

		JPanel panDatum = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblDatum = new JLabel("Datum ukidanja");
		lblDatum.setPreferredSize(new Dimension(150, 20));
		JDatePanelImpl datePanel = new JDatePanelImpl(new UtilDateModel());
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		AbstractFormatter formatter = new AbstractFormatter() {

			@Override
			public String valueToString(Object value) throws ParseException {
				Calendar cal = (Calendar) value;
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
		panDatum.add(lblDatum);
		panDatum.add(dpDatum);
		
		JLabel lblObavezanDatum=new JLabel("*");
		lblObavezanDatum.setForeground(Color.RED);
		panDatum.add(lblObavezanDatum);

		panInput.add(panDatum);

		JPanel panRacunNaKojiSePrenosi = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		JLabel lblRacunKojiSePrenosi = new JLabel("Sredstva se prenose na :");
		lblRacunKojiSePrenosi.setPreferredSize(new Dimension(150, 20));
		txtRacunNaKojiSePrenosi = new JDigitsTextField(14, 18, false, true);
		
		btnGenerisiKontrolniBroj = new JButton(
				"Generisanje kontrolnog broja");
		btnGenerisiKontrolniBroj
				.addActionListener(new GenerateControlNumberUkidanjeAction(
						txtRacunNaKojiSePrenosi,PotvrdaUkidanjaDialog.this));
		btnGenerisiKontrolniBroj.setEnabled(false);
		
		txtRacunNaKojiSePrenosi.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				kontrolniBrojGenerisan=false;
				if (txtRacunNaKojiSePrenosi.getText().trim().length()>=16)
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

		

		panRacunNaKojiSePrenosi.add(lblRacunKojiSePrenosi);
		panRacunNaKojiSePrenosi.add(txtRacunNaKojiSePrenosi);
		panRacunNaKojiSePrenosi.add(btnGenerisiKontrolniBroj);
		
		JLabel lblObaveznoPrenosNaRacun=new JLabel("*");
		lblObaveznoPrenosNaRacun.setForeground(Color.RED);
		panRacunNaKojiSePrenosi.add(lblObaveznoPrenosNaRacun);
		

		panInput.add(panRacunNaKojiSePrenosi);

		panBig = new JPanel();
		panBig.setLayout(new BoxLayout(panBig, BoxLayout.Y_AXIS));

		panBig.add(panInput);

		formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk = new JButton();
		btnOk.setIcon(new ImageIcon("images/ok.gif"));
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					validateInputs();
				} catch (InvalidInputException e1) {
					JOptionPane.showMessageDialog(PotvrdaUkidanjaDialog.this,
							"Desila se greška: \n" + e1.getMessage(), "Greska",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
				CallableStatement proc = DBConnection.getConnection().prepareCall("{ call CloseAccount(?,?,?,?)}");
										    
				proc.setObject(1, txtBrojRacunaKojiSeUkida.getText().trim());	
				proc.setObject(2, PotvrdaUkidanjaDialog.this.datumOtvaranja);
						
				
				proc.setObject(3, dpDatum.getJFormattedTextField().getText().trim());
				proc.setObject(4, txtRacunNaKojiSePrenosi.getText().trim());
				
				proc.execute();
				proc.close();
				DBConnection.getConnection().commit();
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(PotvrdaUkidanjaDialog.this, e2.getMessage(),
							"Greska", JOptionPane.ERROR_MESSAGE);
					return;
					
				}
			
				dispose();
				
				//((GenericDialog)parent).sync();
				
			}
		});

		JButton btnCancel = new JButton();
		btnCancel.setIcon(new ImageIcon("images/no.gif"));
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		formButtonsPanel.add(btnOk);
		formButtonsPanel.add(btnCancel);

		panBig.add(formButtonsPanel);

		add(panBig);
		pack();

	}
	
	protected void validateInputs() throws InvalidInputException {
		String datumUkidanja=dpDatum.getJFormattedTextField().getText();
		String racunPrenosa=txtRacunNaKojiSePrenosi.getText();
		
		if ((datumUkidanja.trim().length()==0) || (racunPrenosa.trim().length()==0))
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		
		if (racunPrenosa.length()!=18)
			throw new InvalidInputException("Račun na koji se sredstva prenose mora imati 18 cifara");
		
		if (!kontrolniBrojGenerisan)
			throw new InvalidInputException(
					"Kontrolni broj računa na koji se sredstva prenose nije generisan");
	}
	
	public boolean isKontrolniBrojGenerisan() {
		return kontrolniBrojGenerisan;
	}

	public void setKontrolniBrojGenerisan(boolean kontrolniBrojGenerisan) {
		this.kontrolniBrojGenerisan = kontrolniBrojGenerisan;
	}

	public JButton getBtnGenerisiKontrolniBroj() {
		return btnGenerisiKontrolniBroj;
	}

}
