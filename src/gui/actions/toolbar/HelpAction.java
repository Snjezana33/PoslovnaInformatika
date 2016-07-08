package gui.actions.toolbar;

import gui.dialogs.generic.GenericDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class HelpAction extends AbstractAction{
	
	GenericDialog dialog;
	
	public HelpAction(GenericDialog dialog){
		putValue(LARGE_ICON_KEY, new ImageIcon("images/help.gif"));
		putValue(NAME,"Help");
		this.dialog = dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
	}

}
