package gui.state;

import gui.dialogs.generic.GenericDialog;

public class ReadOnlyState extends State {

	private GenericDialog dialog;
	
	public ReadOnlyState(GenericDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	@Override
	public void setMode() {
		dialog.sync();
		dialog.getStatusBar().setStatusPaneText(this.toString());
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Read Only";
	}

}
