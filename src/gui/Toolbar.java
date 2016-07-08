package gui;

import gui.actions.toolbar.AddModeAction;
import gui.actions.toolbar.EditAction;
import gui.actions.toolbar.HelpAction;
import gui.actions.toolbar.ItemDeleteAction;
import gui.actions.toolbar.ItemFirstAction;
import gui.actions.toolbar.ItemLastAction;
import gui.actions.toolbar.ItemNextAction;
import gui.actions.toolbar.ItemPreviousAction;
import gui.actions.toolbar.NextAction;
import gui.actions.toolbar.RefreshAction;
import gui.actions.toolbar.SearchAction;
import gui.actions.toolbar.ZoomAction;
import gui.dialogs.generic.GenericDialog;

import javax.swing.Action;
import javax.swing.JToolBar;

public class Toolbar extends JToolBar {
	
	private GenericDialog dialog;
	private Action btnAdd;
	private Action btnDelete;
	private Action btnEdit;
	private Action btnZoomPickUp;
	private Action btnNextForm;
	
	public Toolbar(GenericDialog dialog) {
		super();
		setFloatable(false);
		
		this.dialog = dialog;
		
		add(new SearchAction(dialog));
		add(new RefreshAction(dialog));
		
		btnZoomPickUp = new ZoomAction(dialog);
		btnZoomPickUp.setEnabled(false);
		add(btnZoomPickUp);
		
		add(new HelpAction(dialog));
		addSeparator();
		
		add(new ItemFirstAction(dialog));
		add(new ItemPreviousAction(dialog));
		add(new ItemNextAction(dialog));
		add(new ItemLastAction(dialog));
		addSeparator();
		
		
		btnAdd = new AddModeAction(dialog);
		add(btnAdd);
		
		btnEdit = new EditAction(dialog);
		add(btnEdit);
		
		btnDelete = new ItemDeleteAction(dialog);
		add(btnDelete);
		addSeparator();
		
		addSeparator();
		
		btnNextForm = new NextAction(dialog);
		add(btnNextForm);
		
		if(dialog.isReadOnly()) {
			btnAdd.setEnabled(false);
			btnDelete.setEnabled(false);
			btnEdit.setEnabled(false);
		}
	}
	
	public Action getZoomAction() {
		return btnZoomPickUp;
	}
	
	public Action getAddAction() {
		return btnAdd;
	}
	
	public Action getEditAction() {
		return btnEdit;
	}
	
	public Action getDeleteAction() {
		return btnDelete;
	}
	
	public Action getNextAction() {
		return btnNextForm;
	}
}
