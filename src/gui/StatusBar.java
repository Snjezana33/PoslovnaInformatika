package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {
	
	private StatusPane mode;
	
	public StatusBar() {
		setLayout(new GridLayout(1, 1, 1, 1));
		setBackground(Color.lightGray);
		setBorder(BorderFactory.createLineBorder(Color.darkGray));
		
		mode = new StatusPane("EDIT");
		add(mode);
	}
	
	public StatusPane getStatusPane() {
		return mode;
	}
	
	public void setStatusPaneText(String text) {
		mode.setText(text);
	}
	
}

@SuppressWarnings("serial")
class StatusPane extends JLabel {

	public StatusPane(String text) {
		super(text);
		setHorizontalAlignment(CENTER);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setPreferredSize(new Dimension(200, 25));
	}

}
