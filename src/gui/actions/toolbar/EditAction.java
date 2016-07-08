package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;
import gui.state.EditState;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;


@SuppressWarnings("serial")
public class EditAction extends AbstractAction {
	
	GenericDialog dialog;
	
	public EditAction(GenericDialog dialog) {
		putValue(SMALL_ICON, new ImageIcon("images/edit.gif"));
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		dialog.setState(new EditState(dialog));
		dialog.getCurrentState().setMode();
		dialog.getStatusBar().setStatusPaneText(dialog.getCurrentState().toString());

	}

}
