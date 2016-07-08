package gui.state;

import gui.dialogs.generic.GenericDialog;

public class EditState extends State {

	GenericDialog dialog;
	
	public EditState(GenericDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	@Override
	public void setMode() {
		dialog.sync();

	}

	@Override
	public void commit() {
		dialog.updateRow();
		
	}

	@Override
	public void rollback() {
		dialog.setState(new EditState(dialog));
		dialog.getCurrentState().setMode();
		dialog.getStatusBar().setStatusPaneText(dialog.getCurrentState().toString());
	}

	@Override
	public String toString() {
		return "EDIT";
	}

}
