package gui.actions.dialog;

import gui.state.State;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class CommitAction extends AbstractAction {
	
	private State state;
	
	public CommitAction(State state) {
		this.state = state;
		putValue(SMALL_ICON, new ImageIcon("images/commit.gif"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		state.commit();
	}

}
