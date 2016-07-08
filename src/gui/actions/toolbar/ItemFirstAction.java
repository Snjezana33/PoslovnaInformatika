package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class ItemFirstAction extends AbstractAction {

	GenericDialog dialog;

	public ItemFirstAction(GenericDialog dialog){
		
		putValue(LARGE_ICON_KEY, new ImageIcon("images/first.gif"));
		putValue(NAME,"First Item");
		this.dialog = dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable table = dialog.getTableGrid();
		int rowCount = table.getModel().getRowCount(); 
	    if (rowCount > 0)
	    	table.setRowSelectionInterval(0, 0);

	}

}
