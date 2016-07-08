package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class ItemDeleteAction extends AbstractAction {

	GenericDialog dialog;

	public ItemDeleteAction(GenericDialog dialog){
		
		putValue(LARGE_ICON_KEY, new ImageIcon("images/remove.gif"));
		putValue(NAME,"Delete Item");
		this.dialog = dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		dialog.deleteSelectedRow();

	}

}
