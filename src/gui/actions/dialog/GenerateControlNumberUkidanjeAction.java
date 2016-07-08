package gui.actions.dialog;

import gui.dialogs.PotvrdaUkidanjaDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import utils.AccountNumberISO7064Mod9710;

public class GenerateControlNumberUkidanjeAction extends AbstractAction {

	private JTextField tf;
	private PotvrdaUkidanjaDialog dialog;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GenerateControlNumberUkidanjeAction(JTextField tf,PotvrdaUkidanjaDialog dialog){
		putValue(NAME,"Generate control number");
		this.tf=tf;
		this.dialog=dialog;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String prvih16cifaraRacuna="";
		prvih16cifaraRacuna=tf.getText().trim().substring(0,16);
		
		String generisaniZadnjiBrojevi="";
		if (prvih16cifaraRacuna.trim().length()==16)
		generisaniZadnjiBrojevi=AccountNumberISO7064Mod9710.computeCheckAsString(prvih16cifaraRacuna);
		String ceoBroj=prvih16cifaraRacuna.concat(generisaniZadnjiBrojevi);
		tf.setText(ceoBroj);
		dialog.setKontrolniBrojGenerisan(true);
		dialog.getBtnGenerisiKontrolniBroj().setEnabled(false);
		

	}

}