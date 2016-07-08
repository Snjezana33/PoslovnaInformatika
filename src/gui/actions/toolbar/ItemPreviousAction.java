package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class ItemPreviousAction extends AbstractAction {

	GenericDialog dialog;

	public ItemPreviousAction(GenericDialog dialog){
		
		putValue(LARGE_ICON_KEY, new ImageIcon("images/prev.gif"));
		putValue(NAME,"Previous Item");
		this.dialog = dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable table = dialog.getTableGrid();
		int rowCount = table.getModel().getRowCount(); 
	    if (rowCount > 0){
	    	int selectedRow = table.getSelectedRow();
	    	if(selectedRow != -1){
	    		if(selectedRow != 0)
	    			table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
	    		else
	    			table.setRowSelectionInterval(rowCount - 1, rowCount - 1);
	    	}
	    	else
	    		table.setRowSelectionInterval(0, 0);
	    }

	}

}
