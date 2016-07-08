package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;
import gui.dialogs.model.GenericTableModel;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class RefreshAction extends AbstractAction {
	
	GenericDialog dialog;
	
	public RefreshAction(GenericDialog dialog) {
		putValue(SMALL_ICON, new ImageIcon("images/refresh.gif"));
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GenericTableModel gtm = (GenericTableModel)dialog.getTableGrid().getModel();
		try {
			gtm.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
