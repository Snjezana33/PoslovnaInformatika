package gui.dialogs.generic;

import gui.custom.JLimitTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import components.JDigitsTextField;

@SuppressWarnings("serial")
public class PoslovnoLiceDialog extends GenericDialog {
	
	private JDigitsTextField tfIdBroj;
	private JTextField tfAdresa;
	private JTextField tfEmail;
	private JTextField tfWeb;
	private JDigitsTextField tfTelefon;
	private JLimitTextField tfFax;
	private JCheckBox cbPravnoLice;
	private JDigitsTextField tfJMBGFizickogLica;
	private JTextField tfImeFizickogLica;
	private JTextField tfPrezimeFizickogLica;
	private JDigitsTextField tfPIBPravnogLica;
	private JTextField tfNazivPravnogLica;

	public PoslovnoLiceDialog(JFrame parent) {
		super(parent, "POSLOVNA LICA", "POSLOVNO_LICE", false);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeFormInputPanel() {
		tableGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tfIdBroj =new JDigitsTextField(8, 10, true, false);
		tfIdBroj.setName("L_ID");
		addComponentToFormInputPanel(tfIdBroj, "ID broj",true);
		
		tfAdresa = new JTextField(20);
		tfAdresa.setName("L_ADRESA");
		addComponentToFormInputPanel(tfAdresa, "Adresa",false);
		
		tfEmail=new JTextField(20);
		tfEmail.setName("L_EMAIL");
		addComponentToFormInputPanel(tfEmail, "E-mail",false);
		
		tfWeb=new JTextField(20);
		tfWeb.setName("L_WEB");
		addComponentToFormInputPanel(tfWeb, "Web adresa",false);
		
		tfTelefon=new JDigitsTextField(15,15,true,true);
		tfTelefon.setName("L_TELEFON");
		addComponentToFormInputPanel(tfTelefon, "Telefon",false);
		
		tfFax=new JLimitTextField(15, 20);
		tfFax.setName("L_FAX");
		addComponentToFormInputPanel(tfFax, "Fax",false);
		
		cbPravnoLice=new JCheckBox();
		cbPravnoLice.setName("L_PRAVNO");
		addComponentToFormInputPanel(cbPravnoLice, "Pravno lice",false);
		
		tfJMBGFizickogLica=new JDigitsTextField(10,13,false,true);
		tfJMBGFizickogLica.setName("L_JMBG");
		addComponentToFormInputPanel(tfJMBGFizickogLica, "JMBG fizičkog lica",false);
		
		tfImeFizickogLica=new JTextField(10);
		tfImeFizickogLica.setName("L_IME");
		addComponentToFormInputPanel(tfImeFizickogLica, "Ime fizičkog lica",false);
		
		tfPrezimeFizickogLica=new JTextField(10);
		tfPrezimeFizickogLica.setName("L_PREZIME");
		addComponentToFormInputPanel(tfPrezimeFizickogLica, "Prezime fizičkog lica",false);
		
		tfPIBPravnogLica=new JDigitsTextField(8, 10, false, true);
		tfPIBPravnogLica.setName("L_PIB");
		addComponentToFormInputPanel(tfPIBPravnogLica, "PIB pravnog lica",false);
		
		tfNazivPravnogLica=new JTextField(10);
		tfNazivPravnogLica.setName("L_NAZIV");
		addComponentToFormInputPanel(tfNazivPravnogLica, "Naziv pravnog lica",false);
		
		
		
		tfNazivPravnogLica.setEnabled(false);
		tfPIBPravnogLica.setEnabled(false);
		
		
		cbPravnoLice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeTextFieldsFromOrder();
				if (cbPravnoLice.isSelected()){
					tfImeFizickogLica.setText("");
					tfPrezimeFizickogLica.setText("");
					tfJMBGFizickogLica.setText("");
					
					tfImeFizickogLica.setEnabled(false);
					tfPrezimeFizickogLica.setEnabled(false);
					tfJMBGFizickogLica.setEnabled(false);
					
					tfNazivPravnogLica.setEnabled(true);
					tfPIBPravnogLica.setEnabled(true);
					getComponentsFromInputPanel(getFormInputPanel());
					
					
				}
				else {
					tfNazivPravnogLica.setText("");
					tfPIBPravnogLica.setText("");
					
					tfNazivPravnogLica.setEnabled(false);
					tfPIBPravnogLica.setEnabled(false);
					
					tfImeFizickogLica.setEnabled(true);
					tfPrezimeFizickogLica.setEnabled(true);
					tfJMBGFizickogLica.setEnabled(true);
					getComponentsFromInputPanel(getFormInputPanel());
				}
			}
		});
		
	}

	@Override
	protected void validateInputs() throws InvalidInputException {
		String idLica=tfIdBroj.getText();
		String jmbg=tfJMBGFizickogLica.getText();
		if (idLica.trim().length()==0)
			throw new InvalidInputException("Obavezno polje nije popunjeno");
		
		if (jmbg.trim().length()>0)
			if (jmbg.length()!=13)
			throw new InvalidInputException("JMBG mora imati tačno 13 cifara");
		
	}

}
