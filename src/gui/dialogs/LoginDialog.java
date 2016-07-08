package gui.dialogs;

import gui.listeners.LoginListener;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog {
	
	private JLabel lbKorisnickoIme;
	private JTextField tfKorisnickoIme;
	private JLabel lbLozinka;
	private JTextField tfLozinka;
	
	public LoginDialog() {
		
		setSize(350,170);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		setModal(true);
		
		Dimension dim2 = new Dimension(150,20);
		
		JPanel veliki = new JPanel();
		veliki.setLayout(new BoxLayout(veliki,BoxLayout.Y_AXIS));
		veliki.setBorder(new TitledBorder(new EtchedBorder(),"Prijava administratora"));
		
		JPanel panKorisnicko = new JPanel();
		panKorisnicko.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		lbKorisnickoIme = new JLabel("Korisnicko ime");
		lbKorisnickoIme.setPreferredSize(dim2);
		
		tfKorisnickoIme = new JTextField();
		tfKorisnickoIme.setPreferredSize(dim2);
		
		panKorisnicko.add(lbKorisnickoIme);
		panKorisnicko.add(tfKorisnickoIme);
		
		JPanel panLozinka = new JPanel();
		panLozinka.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		lbLozinka = new JLabel("Lozinka");
		lbLozinka.setPreferredSize(dim2);
		
		tfLozinka = new JPasswordField();
		
		tfLozinka.setPreferredSize(dim2);
		
		panLozinka.add(lbLozinka);
		panLozinka.add(tfLozinka);
		
		JPanel panD = new JPanel();
		panD.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton btnOk=new JButton("Potvrda");
		btnOk.addActionListener(new LoginListener(this));
		
		JButton btnCancel=new JButton("Otkazi");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				System.exit(0);
				
				
				
			}
		});
		
		panD.add(btnOk);
		panD.add(btnCancel);
		
		veliki.add(panKorisnicko);
		veliki.add(panLozinka);
		veliki.add(panD);
		
		add(veliki);
		
	}

	public JLabel getLbKorisnickoIme() {
		return lbKorisnickoIme;
	}

	public void setLbKorisnickoIme(JLabel lbKorisnickoIme) {
		this.lbKorisnickoIme = lbKorisnickoIme;
	}

	public JTextField getTfKorisnickoIme() {
		return tfKorisnickoIme;
	}

	public void setTfKorisnickoIme(JTextField tfKorisnickoIme) {
		this.tfKorisnickoIme = tfKorisnickoIme;
	}

	public JLabel getLbLozinka() {
		return lbLozinka;
	}

	public void setLbLozinka(JLabel lbLozinka) {
		this.lbLozinka = lbLozinka;
	}

	public JTextField getTfLozinka() {
		return tfLozinka;
	}

	public void setTfLozinka(JTextField tfLozinka) {
		this.tfLozinka = tfLozinka;
	}
	
	
	

}
