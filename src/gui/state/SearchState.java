package gui.state;

import gui.dialogs.generic.GenericDialog;

public class SearchState extends State {

	GenericDialog dialog;
	
	public SearchState(GenericDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	@Override
	public void setMode() {
		dialog.emptyTextField();

	}

	@Override
	public void commit() {
		dialog.searchRow();
		
	}

	@Override
	public void rollback() {
		if(!dialog.isReadOnly())
			dialog.setState(new EditState(dialog));
		else
			dialog.setState(new ReadOnlyState(dialog));
		dialog.getCurrentState().setMode();
		dialog.getStatusBar().setStatusPaneText(dialog.getCurrentState().toString());
	}

	@Override
	public String toString() {
		return "SEARCH";
	}

}
