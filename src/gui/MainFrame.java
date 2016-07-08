package gui;

import gui.listeners.MyWindowListener;

import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JFrame;

import users.User;
import users.Users;
import db.DBConnection;

public class MainFrame extends JFrame {
	private static MainFrame instance = null;
	private Menu menu;
	private Users users = new Users();
	private User currentUser=null;

	private MainFrame() {
		setTitle("Banka");
		setSize(700, 500);
		setLocationRelativeTo(null);
		addWindowListener(new MyWindowListener(users));

		menu = new Menu();
		setJMenuBar(menu);

		try {
			DBConnection.open();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		addWindowListener(new MyWindowListener(users));

		setBackground(Color.WHITE);
	}
	
	//nesto ne iscrtava dobro kada se koristi ova klasa
	/*class ImagePanel extends JPanel {

		private Image img;

		public ImagePanel(Image img) {
			this.img = img;
		}

		public void paintComponent(Graphics g) {
			g.drawImage(
					img,
					(int) (this.getSize().getWidth() - img.getWidth(null)) / 2,
					(int) (this.getSize().getHeight() - img.getHeight(null)) / 2,
					null);
		}
	}*/
	
	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	public Users getUsers() {
		return users;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Menu getMenu() {
		return menu;
	}
}
