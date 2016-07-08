package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;
import gui.state.AddState;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class AddModeAction extends AbstractAction {

	GenericDialog dialog;

	public AddModeAction(GenericDialog dialog){
		
		putValue(LARGE_ICON_KEY, new ImageIcon("images/add.gif"));
		putValue(NAME,"Add Mode");
		this.dialog = dialog;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		dialog.setState(new AddState(dialog));
		dialog.getCurrentState().setMode();
		dialog.getStatusBar().setStatusPaneText(dialog.getCurrentState().toString());

	}

}
