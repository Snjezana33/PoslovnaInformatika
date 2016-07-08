package gui.dialogs.generic;

import gui.actions.dialog.GenerateControlNumberAction;
import gui.custom.JLimitTextField;

import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import utils.CurrentBank;
import components.JDigitsTextField;

@SuppressWarnings("serial")
public class BankaDialog extends GenericDialog {

	JDigitsTextField tfSifra;
	JLimitTextField tfSwiftKod;
	JDigitsTextField tfObracunskiRacun;

	JTextField tfNaziv;
	JTextField tfAdresa;
	JTextField tfEmail;
	JTextField tfWeb;
	JDigitsTextField tfTelefon;
	JLimitTextField tfFax;

	public BankaDialog(JFrame parent) {
		super(parent, "Banke", "BANKA", false);

	}

	public JDigitsTextField getTfObracunskiRacun() {
		return tfObracunskiRacun;
	}

	@Override
	protected void initializeFormInputPanel() {
		tableGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tfSifra = new JDigitsTextField(3, 3, false, true);
		tfSifra.setName("BANK_SIFRA");
		addComponentToFormInputPanel(tfSifra, "Šifra banke", true);

		tfSwiftKod = new JLimitTextField(6, 8);
		tfSwiftKod.setName("BANK_SWIFT");
		addComponentToFormInputPanel(tfSwiftKod, "SWIFT kod banke", false);

		JPanel panelRacun = new JPanel(new FlowLayout(FlowLayout.LEFT));

		tfObracunskiRacun = new JDigitsTextField(14, 18, true, true);
		tfObracunskiRacun.setName("BANK_OBR_RACUN");
		btnGenerisiKontrolniBroj = new JButton("Generisanje kontrolnog broja");
		btnGenerisiKontrolniBroj
				.addActionListener(new GenerateControlNumberAction(
						tfObracunskiRacun, BankaDialog.this));
		btnGenerisiKontrolniBroj.setEnabled(false);
		tfObracunskiRacun.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				kontrolniBrojGenerisan = false;
				if (tfObracunskiRacun.getText().trim().length() >= 16)
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

		panelRacun.add(tfObracunskiRacun);
		panelRacun.add(btnGenerisiKontrolniBroj);
		addComponentToFormInputPanel(panelRacun, "Obračunski račun", true);

		tfNaziv = new JTextField(20);
		tfNaziv.setName("BANK_NAZIV");
		addComponentToFormInputPanel(tfNaziv, "Naziv", true);

		tfAdresa = new JTextField(20);
		tfAdresa.setName("BANK_ADRESA");
		addComponentToFormInputPanel(tfAdresa, "Adresa", false);

		tfEmail = new JTextField(20);
		tfEmail.setName("BANK_EMAIL");
		addComponentToFormInputPanel(tfEmail, "E-mail", false);

		tfWeb = new JTextField(20);
		tfWeb.setName("BANK_WEB");
		addComponentToFormInputPanel(tfWeb, "Web adresa", false);

		tfTelefon = new JDigitsTextField(14, 20, true, true);
		tfTelefon.setName("BANK_TEL");
		addComponentToFormInputPanel(tfTelefon, "Telefon", false);

		tfFax = new JLimitTextField(14, 20);
		tfFax.setName("BANK_FAX");
		addComponentToFormInputPanel(tfFax, "Fax", false);

	}

	@Override
	public void deleteSelectedRow() {
		if (tableGrid.getSelectedRow() != -1
				&& ((String) tableGrid
						.getValueAt(tableGrid.getSelectedRow(), 0))
						.equals(CurrentBank.getId()))
			JOptionPane.showMessageDialog(this,
					"Ne možete izbrisati banku koja pripada ovom programu.",
					"Greška", JOptionPane.ERROR_MESSAGE);
		else
			super.deleteSelectedRow();
	}

	@Override
	protected void validateInputs() throws InvalidInputException {

		String idBanke = tfSifra.getText();
		String obracnskiRacun = tfObracunskiRacun.getText();
		String SwiftKod = tfSwiftKod.getText();
		String nazivBanke = tfNaziv.getText();

		if ((idBanke.trim().length() == 0) || (nazivBanke.trim().length() == 0))
			throw new InvalidInputException("Obavezna polja nisu popunjena");
		if (idBanke.length() != 3)
			throw new InvalidInputException(
					"Oznaka banke mora imati tačno 3 cifre");

		if (obracnskiRacun.length() != 18)
			throw new InvalidInputException(
					"Obračunski račun banke mora imati tačno 18 cifara");

		if (SwiftKod.length() != 8)
			throw new InvalidInputException("SWIFT kod mora sadržati 8 cifara");

		String prvihSestZnakovaSviftKoda = SwiftKod.substring(0, 6);
		if ((!isAllLetters(prvihSestZnakovaSviftKoda))
				|| (!isUpper(prvihSestZnakovaSviftKoda)))
			throw new InvalidInputException(
					"Prvih 6 znakova SWIFT koda moraju biti velika slova");

		String zadnjihDvaZnakaSviftKoda = SwiftKod.substring(6, 7);
		if (!isAllNumbersOrBigLetters(zadnjihDvaZnakaSviftKoda))
			throw new InvalidInputException(
					"Zadnja 2 znakova SWIFT koda moraju biti velika slova ili brojevi");

		if (!kontrolniBrojGenerisan)
			throw new InvalidInputException(
					"Kontrolni broj računa nije generisan");

	}

}
