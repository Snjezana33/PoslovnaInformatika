package gui.actions.toolbar;

import gui.MainFrame;
import gui.dialogs.generic.GenericDialog;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import table.property.NextElementProperties;
import table.property.NextProperties;
import table.property.PropertiesContainer;
import table.property.TableProperties;

@SuppressWarnings("serial")
public class NextAction extends AbstractAction {

	GenericDialog dialog;

	public NextAction(GenericDialog dialog) {
		putValue(SMALL_ICON, new ImageIcon("images/nextform.gif"));
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final int row = dialog.getTableGrid().getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(dialog,
					"Niste selektovali red u tabeli.", "Greška",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (dialog.getGenTableModel().getTableProperties().getNext().size() == 1) {
			GenericDialog nextDialog = createNextDialog(0);

			Vector<Object> nextColumnsData = new Vector<Object>();
			Vector<String> nextColumnsName = new Vector<String>();
			int index = 0;
			for (int i = 0; i < dialog.getTableGrid().getColumnCount(); i++) {
				if (dialog.getPrimaryKeysNames().contains(
						dialog.getTableGrid().getColumnName(i))) {
					TableProperties tp = dialog.getGenTableModel()
							.getTableProperties();
					NextProperties np = tp.getNextItemByTableName(nextDialog
							.getTableName());
					for (NextElementProperties nep : np.getNextElements()) {
						if (tp.getPrimaryKeysFromTable().get(index)
								.equals(nep.getFrom())) {
							nextColumnsData.add(dialog.getTableGrid()
									.getValueAt(row, i));
							nextColumnsName.add(nep.getTo());
						}
					}
					tp.setColumnsForNext(nextColumnsName);
					index++;
				}
			}

			if (nextDialog != null) {
				nextDialog.fillNext(nextColumnsData, dialog.getTableName());
				if (nextDialog.getTableGrid().getRowCount() > 0)
					nextDialog.getTableGrid().setRowSelectionInterval(0, 0);
				nextDialog.setVisible(true);
			}

		} else if (dialog.getGenTableModel().getTableProperties().getNext()
				.size() > 1) {
			JPopupMenu popupMenu = new JPopupMenu();
			for (int i = 0; i < dialog.getGenTableModel().getTableProperties()
					.getNext().size(); i++) {
				final int index = i;
				TableProperties tp = PropertiesContainer
						.getInstance()
						.getTablesMap()
						.get(dialog.getGenTableModel().getTableProperties()
								.getNext().get(i).getTable());
				String name = tp.getTableLabel();

				if (dialog
						.getGenTableModel()
						.getTableProperties()
						.checkNextElements(
								dialog.getGenTableModel().getTableProperties()
										.getNext().get(i)) > dialog
						.getGenTableModel().getTableProperties().getNext()
						.get(i).getNextElements().size()) {
					JMenu mnu = new JMenu(name);
					for (int j = 0; j < dialog.getGenTableModel()
							.getTableProperties().getNext().get(i)
							.getNextElements().size(); j++) {
						final NextElementProperties nep = dialog
								.getGenTableModel().getTableProperties()
								.getNext().get(i).getNextElements().get(j);
						final GenericDialog nextDialog = createNextDialog(index);
						String menuItemName = PropertiesContainer.getInstance()
								.getTablesMap().get(nextDialog.getTableName())
								.getColumnLabel(nep.getTo());
						mnu.add(new JMenuItem(new AbstractAction(menuItemName) {

							@Override
							public void actionPerformed(ActionEvent e) {
								// GenericDialog nextDialog =
								// createNextDialog(index);

								Vector<Object> nextColumnsData = new Vector<Object>();
								// Vektor sa nazivima kolona za next
								Vector<String> nextColumnsName = new Vector<String>();
								nextColumnsName.add(nep.getTo());
								int index = 0;
								for (int i = 0; i < dialog.getTableGrid()
										.getColumnCount(); i++) {
									if (dialog.getPrimaryKeysNames().contains(
											dialog.getTableGrid()
													.getColumnName(i))) {
										TableProperties tp = dialog
												.getGenTableModel()
												.getTableProperties();
										if (tp.getPrimaryKeysFromTable()
												.get(index)
												.equals(nep.getFrom()))
											nextColumnsData.add(dialog
													.getTableGrid().getValueAt(
															row, i));

										tp.setColumnsForNext(nextColumnsName);
										index++;
									}
								}

								if (nextDialog != null) {
									nextDialog.fillNext(nextColumnsData,
											dialog.getTableName());
									if (nextDialog.getTableGrid().getRowCount() > 0)
										nextDialog.getTableGrid()
												.setRowSelectionInterval(0, 0);
									nextDialog.setVisible(true);
								}

							}
						}));
					}
					popupMenu.add(mnu);
				} else {

					popupMenu.add(new JMenuItem(new AbstractAction(name) {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							GenericDialog nextDialog = createNextDialog(index);

							Vector<Object> nextColumnsData = new Vector<Object>();
							// Vektor sa nazivima kolona za next
							Vector<String> nextColumnsName = new Vector<String>();
							int index = 0;
							for (int i = 0; i < dialog.getTableGrid()
									.getColumnCount(); i++) {
								if (dialog.getPrimaryKeysNames().contains(
										dialog.getTableGrid().getColumnName(i))) {
									TableProperties tp = dialog
											.getGenTableModel()
											.getTableProperties();
									NextProperties np = tp
											.getNextItemByTableName(nextDialog
													.getTableName());
									for (NextElementProperties nep : np
											.getNextElements()) {
										if (tp.getPrimaryKeysFromTable()
												.get(index)
												.equals(nep.getFrom())) {
											nextColumnsData.add(dialog
													.getTableGrid().getValueAt(
															row, i));
											nextColumnsName.add(nep.getTo());
										}
									}
									tp.setColumnsForNext(nextColumnsName);
									index++;
								}
							}

							if (nextDialog != null) {
								nextDialog.fillNext(nextColumnsData,
										dialog.getTableName());
								if (nextDialog.getTableGrid().getRowCount() > 0)
									nextDialog.getTableGrid()
											.setRowSelectionInterval(0, 0);
								nextDialog.setVisible(true);
							}
						}
					}));
				}
			}

			popupMenu.show(dialog.getComponent(0), (int) dialog.getToolbar()
					.getMousePosition().getX(), (int) dialog.getToolbar()
					.getMousePosition().getY());

		}

	}

	private GenericDialog createNextDialog(int index) {
		GenericDialog nextDialog = null;
		NextProperties np = dialog.getGenTableModel().getTableProperties()
				.getNext().get(index);
		try {
			nextDialog = (GenericDialog) Class
					.forName("gui.dialogs.generic." + np.getClassName())
					.getConstructor(JFrame.class)
					.newInstance(MainFrame.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nextDialog;
	}

}
