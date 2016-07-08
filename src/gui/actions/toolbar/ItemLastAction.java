package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class ItemLastAction extends AbstractAction {

	GenericDialog dialog;

	public ItemLastAction(GenericDialog dialog){
		
		putValue(LARGE_ICON_KEY, new ImageIcon("images/last.gif"));
		putValue(NAME,"Last Item");
		this.dialog = dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable table = dialog.getTableGrid();
		int rowCount = table.getModel().getRowCount(); 
	    if (rowCount > 0)
	    	table.setRowSelectionInterval(rowCount - 1, rowCount - 1);

	}
	
}
