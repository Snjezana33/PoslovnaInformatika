package gui.dialogs.generic;

import gui.custom.JLimitTextField;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.eclipse.jdt.core.compiler.InvalidInputException;

@SuppressWarnings("serial")
public class DrzavaDialog extends GenericDialog {

	private JTextField tfNaziv;
	private JLimitTextField tfSifra;

	
	public DrzavaDialog(JFrame parent) {
		super(parent, "Drzave", "DRZAVA", false);

	}
		 
	@Override
	protected void initializeFormInputPanel() {

		tfSifra = new JLimitTextField(3, 3);
		tfSifra.setName("DR_SIFRA");
		addComponentToFormInputPanel(tfSifra, "Šifra države",true);
		
		tfNaziv = new JTextField(20);
		tfNaziv.setName("DR_NAZIV");
		addComponentToFormInputPanel(tfNaziv, "Naziv države",false);
		
	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		String sifra=tfSifra.getText();
		if (sifra.trim().length()==0)
			throw new InvalidInputException("Obavezna polja nisu popunjena");
	}	
}
