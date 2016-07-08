package gui.dialogs.generic;

import gui.MainFrame;
import gui.custom.JLimitTextField;
import gui.dialogs.model.Lookup;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.jdt.core.compiler.InvalidInputException;

public class ValutaDialog extends GenericDialog {

	
	private static final long serialVersionUID = 1L;
	private JLimitTextField tfSifraValute;
	private JTextField tfNaziv;
	private JLimitTextField tfSifraDrzave;
	private JTextField tfNazivDrzave;
	private JCheckBox cbDomicilna;
	private DrzavaDialog dlgDrzave;
	
	public ValutaDialog(JFrame parent){
		super(parent, "Valute", "VALUTA", false);
	}
	
	@Override
	protected void initializeFormInputPanel() {
		
		
		tfSifraValute = new JLimitTextField(3, 3);
		tfSifraValute.setName("VA_SIFRA");
		addComponentToFormInputPanel(tfSifraValute, "Šifra valute",true);
		
		
		tfNaziv = new JTextField(20);
		tfNaziv.setName("VA_NAZIV");
		addComponentToFormInputPanel(tfNaziv, "Naziv valute",true);
		
		JPanel panDrzavaLookUp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tfSifraDrzave = new JLimitTextField(3, 3);
		tfSifraDrzave.setName("DR_SIFRA");
		tfSifraDrzave.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String sifraDrzave = tfSifraDrzave.getText().trim();
				try {
					tfNazivDrzave.setText(Lookup.getLookupColumnValue("DRZAVA", "DR_NAZIV", "DR_SIFRA", sifraDrzave));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnDrzava = new JButton("...");
		btnDrzava.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgDrzave = new DrzavaDialog(MainFrame.getInstance());
				dlgDrzave.setColumnsForZoom(getColumnsForZoom());
				dlgDrzave.getToolbar().getZoomAction().setEnabled(true);
				dlgDrzave.setVisible(true);
				tfSifraDrzave.setText(dlgDrzave.getZoomMap().get(tfSifraDrzave.getName()));
				tfNazivDrzave.setText(dlgDrzave.getZoomMap().get(tfNazivDrzave.getName()));
			}
		});
		tfNazivDrzave = new JTextField(20);
		tfNazivDrzave.setName("DR_NAZIV");
		tfNazivDrzave.setEditable(false);
		panDrzavaLookUp.add(tfSifraDrzave);
		panDrzavaLookUp.add(btnDrzava);
		panDrzavaLookUp.add(tfNazivDrzave);
		addComponentToFormInputPanel(panDrzavaLookUp, "Šifra države",true);

//		formInputPanel.add(panDrzavaLookUp);
//		formInputPanel.add(Box.createHorizontalStrut(5));
		
		cbDomicilna = new JCheckBox();
		cbDomicilna.setName("VA_DOMICILNA");
		addComponentToFormInputPanel(cbDomicilna, "Domicilna",false);
		
		

	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		String sifra=tfSifraValute.getText();
		String naziv=tfNaziv.getText();
		String nazivDrzave=tfNazivDrzave.getText();
		if ((sifra.trim().length()==0) || (naziv.trim().length()==0) || (nazivDrzave.trim().length()==0))
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		
		if (!isAllLetters(naziv))
			throw new InvalidInputException("Naziv valute se mora sastojati iskljuÄ?ivo iz slova");
	}

}
