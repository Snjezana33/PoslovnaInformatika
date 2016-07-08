package gui.listeners;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import users.Users;
import db.DBConnection;

public class MyWindowListener extends WindowAdapter {
	
	private Users users;
	
	public MyWindowListener(Users users) {
		this.users = users;
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		DBConnection.close();
		System.out.println("Konekcija zatvorena!");

	}

	@Override
	public void windowOpened(WindowEvent arg0) {

		File file = new File("users.txt");
		if (file.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				users.load(reader);
				reader.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
}
