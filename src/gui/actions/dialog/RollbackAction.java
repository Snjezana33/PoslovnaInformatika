package gui.actions.dialog;

import gui.state.State;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class RollbackAction extends AbstractAction {

private State state;
	
	public RollbackAction(State state) {
		this.state = state;
		putValue(SMALL_ICON, new ImageIcon("images/remove.gif"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		state.rollback();
	}

}
