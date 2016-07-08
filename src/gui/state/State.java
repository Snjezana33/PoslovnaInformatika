package gui.state;

import gui.dialogs.generic.GenericDialog;

public abstract class State {
	
	protected GenericDialog dialog;
	
	public State(GenericDialog dialog) {
		this.dialog = dialog;
	}
	
	public abstract void setMode();
	
	public abstract void commit();
	public abstract void rollback();
	public abstract String toString();
	
}
