package gui.listeners;

import gui.MainFrame;
import gui.dialogs.LoginDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class LoginListener implements ActionListener{
private LoginDialog log;
	
	public LoginListener(LoginDialog logParent) {
		
		log = logParent;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if (MainFrame.getInstance().getUsers().checkUsernameAndPassword(log.getTfKorisnickoIme().getText(), log.getTfLozinka().getText())){
		
			
			MainFrame.getInstance().getMenu().getMnuKorisnik().setText(log.getTfKorisnickoIme().getText());
			log.dispose();
		}
		 else {
			
			JOptionPane.showMessageDialog(log, "Pogresno korisnicko ime ili lozinka",
					"Error Message", JOptionPane.ERROR_MESSAGE);
		}

	}
}
