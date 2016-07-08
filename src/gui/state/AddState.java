package gui.state;

import gui.dialogs.generic.GenericDialog;


public class AddState extends State {
	
GenericDialog dialog;
	
	public AddState(GenericDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	@Override
	public void setMode() {
		dialog.emptyTextField();

	}

	@Override
	public void commit() {
		dialog.addRow();
		
	}

	@Override
	public void rollback() {
		dialog.setState(new EditState(dialog));
		dialog.getCurrentState().setMode();
		dialog.getStatusBar().setStatusPaneText(dialog.getCurrentState().toString());
	}

	@Override
	public String toString() {
		return "ADD";
	}

}
