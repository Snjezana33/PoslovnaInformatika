package gui.dialogs.generic;

import gui.StatusBar;
import gui.Toolbar;
import gui.dialogs.model.GenericTableModel;
import gui.state.EditState;
import gui.state.ReadOnlyState;
import gui.state.State;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import table.property.ColumnProperties;
import table.property.PropertiesContainer;
import table.property.TableProperties;
import table.property.LookUpElementProperties;
import table.property.ZoomElementProperties;
import table.property.ZoomProperties;

public abstract class GenericDialog extends JDialog {
	private static final long serialVersionUID = 9212332865510405498L;
	private static final String ERROR_DATA_TRUNCTUATION = "Jedne ili više vrednosti koje ste uneli su prevelike.";

	protected Toolbar toolbar;
	protected JTable tableGrid = new JTable();
	protected JPanel formPanel = new JPanel();
	protected JPanel formButtonsPanel = new JPanel();
	protected JPanel formInputPanel = new JPanel();
	protected JButton btnDelete = new JButton();
	protected GenericDialog thisDialog = this;

	protected String titleName;
	protected StatusBar statusBar;
	protected State state;
	protected Vector<Component> order;
	protected Vector<Component> orderActiveTextFields;

	protected Map<String, String> zoomMap = new HashMap<String, String>();
	protected Vector<String> columnsForZoom;
	protected String tableName;
	protected boolean isReadOnly = false;
	protected int brojKolona = 0;
	protected boolean kontrolniBrojGenerisan = false;
	protected JButton btnGenerisiKontrolniBroj;
	
	public GenericDialog(JFrame parent, String titleName, String tableName,
			boolean isReadOnly) {

		super(parent);
		this.titleName = titleName;
		this.tableName = tableName;
		this.isReadOnly = isReadOnly;
		setTitle(this.titleName);
		setSize(800, 600);
		setLocationRelativeTo(parent);
		setModal(true);
		order = new Vector<Component>();
		orderActiveTextFields = new Vector<Component>();

		toolbar = new Toolbar(this);
		add(toolbar, BorderLayout.NORTH);

		statusBar = new StatusBar();
		add(statusBar, BorderLayout.SOUTH);

		if (isReadOnly)
			state = new ReadOnlyState(this);
		else
			state = new EditState(this);

		final JScrollPane scrollPane = new JScrollPane(tableGrid);

		tableGrid.setAutoCreateRowSorter(true);
		tableGrid.setRowSelectionAllowed(true);
		tableGrid.setColumnSelectionAllowed(false);
		tableGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableGrid.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting())
							return;
						sync();
					}
				});
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tableGrid
				.getTableHeader().getDefaultRenderer();
		renderer.setHorizontalAlignment(SwingConstants.LEFT);

		initializeTableModel();
		hideExcludedColumns();
		if (getGenTableModel().getTableProperties().isNextEmpty())
			toolbar.getNextAction().setEnabled(false);

		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

		formInputPanel
				.setLayout(new BoxLayout(formInputPanel, BoxLayout.Y_AXIS));
		initializeFormInputPanel();

		getCurrentState().setMode();

		formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnCommit = new JButton();
		btnCommit.setIcon(new ImageIcon("images/commit.gif"));
		btnCommit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				state.commit();
			}
		});
		JButton btnRollback = new JButton();
		btnRollback.setIcon(new ImageIcon("images/remove.gif"));
		btnRollback.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				state.rollback();
			}
		});
		formButtonsPanel.add(btnCommit);
		formButtonsPanel.add(btnRollback);

		// potrebno dugme za ukidanje rauna poslovnih lica

		final JScrollPane jsp = new JScrollPane(formInputPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		if (brojKolona < 5)
			jsp.setPreferredSize(new Dimension(formInputPanel.getWidth(),
					brojKolona * 50));
		formPanel.add(Box.createGlue());
		formPanel.add(scrollPane);
		formPanel.add(Box.createHorizontalStrut(5));
		formPanel.add(jsp);
		formPanel.add(Box.createHorizontalStrut(5));
		formPanel.add(formButtonsPanel);

		add(formPanel);
		getComponentsFromInputPanel(formInputPanel);

		setActionStateForReadOnly();

		jsp.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		jsp.getViewport().setSize(
				new Dimension(formInputPanel.getBounds().width, formInputPanel
						.getBounds().height / 2));
		jsp.getViewport().setViewSize(
				new Dimension(formInputPanel.getBounds().width, formInputPanel
						.getBounds().height / 2));
		jsp.getViewport().setViewPosition(new Point(0, 0));

		getActiveTextFields();

		setFocusTraversalPolicy(new FocusTraversalPolicy() {

			@Override
			public Component getLastComponent(Container aContainer) {

				return order.lastElement();
			}

			@Override
			public Component getFirstComponent(Container aContainer) {

				return order.get(0);

			}

			@Override
			public Component getDefaultComponent(Container aContainer) {
				// TODO Auto-generated method stub

				return order.get(0);
			}

			@Override
			public Component getComponentBefore(Container aContainer,
					Component aComponent) {
				// TODO Auto-generated method stub
				int idx = order.indexOf(aComponent) - 1;
				if (idx < 0) {
					idx = order.size() - 1;
				}

				jsp.getViewport().setViewPosition(
						(new Point((int) formInputPanel.getBounds().getMinX(),
								(int) order.get(idx).getBounds().getMinY())));
				return order.get(idx);
			}

			@Override
			public Component getComponentAfter(Container aContainer,
					Component aComponent) {
				// TODO Auto-generated method stub
				int idx = (order.indexOf(aComponent) + 1) % order.size();

				if (!(order.get(idx) instanceof JButton))
					jsp.getViewport().setViewPosition(
							(new Point((int) formInputPanel.getBounds()
									.getMinX(), (int) order.get(idx)
									.getBounds().getMinY())));

				return order.get(idx);
			}

		});
	}
	
	public void setActionStateForReadOnly() {
		if (isReadOnly) {
			toolbar.getAddAction().setEnabled(false);
			toolbar.getEditAction().setEnabled(false);
			toolbar.getDeleteAction().setEnabled(false);
		}
	}
	
	protected void getComponentsFromInputPanel(JComponent component) {
		Vector<Component> components = new Vector<Component>();
		for (int i = 0; i < component.getComponents().length; i++) {
			if (component.getComponent(i) instanceof JTextField) {
				JTextField tf = (JTextField) component.getComponent(i);

				// Da ne uzima u obzir ne editujuca polja
				if ((tf.isEditable()) && (tf.isEnabled()))
					components.add(tf);
			} else if (component.getComponent(i) instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox) component.getComponent(i);

				// Da ne uzima u obzir ne editujuca polja
				if (cb.isEnabled())
					components.add(cb);
			}

			else if (component.getComponent(i) instanceof JButton) {
				JButton button = (JButton) component.getComponent(i);

				// Da ne uzima u obzir ne editujuca polja

				components.add(button);
			}

			else if (component.getComponent(i) instanceof JPanel) {
				JPanel pan = (JPanel) component.getComponent(i);
				getComponentsFromInputPanel(pan);

			}

		}

		order.addAll(components);
	}
	
	protected void hideExcludedColumns() {
		GenericTableModel tableModel = (GenericTableModel) tableGrid.getModel();
		for (Integer i : tableModel.getExcludedIndexes()) {
			tableGrid.removeColumn(tableGrid.getColumnModel().getColumn(i));
		}
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public int getBrojKolona() {
		return brojKolona;
	}
	
	protected void removeTextFieldsFromOrder() {
		if (!order.isEmpty())
			order.clear();
	}
	
	protected void initializeTableModel() {
		GenericTableModel tableModel = new GenericTableModel(
				PropertiesContainer.getInstance().getTablesMap().get(tableName));
		tableGrid.setModel(tableModel);

		try {
			tableModel.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JPanel getFormInputPanel() {
		return formInputPanel;
	}
	
	protected abstract void initializeFormInputPanel();
	
	private boolean getRecursiveInput(String columnName,
			Vector<String> resultHolder, JPanel currentPanel) {
		boolean foundInput = false;
		for (int i = 0; i < currentPanel.getComponentCount(); i++) {
			Component c = currentPanel.getComponent(i);
			if (c instanceof JPanel) {
				foundInput = getRecursiveInput(columnName, resultHolder,
						(JPanel) c);
				if (foundInput) {
					break;
				}
			} else if (c instanceof JTextField) {
				JTextField tf = (JTextField) c;
				if (tf.getName().equals(columnName)) {
					String result = tf.getText().trim();
					if (result.equals(""))
						result = null;
					resultHolder.add(result);
					foundInput = true;
					break;
				}
			}
		}
		return foundInput;
	}
	
	protected String getInput(String columnName) {
		Vector<String> resultHolder = new Vector<String>();
		getRecursiveInput(columnName, resultHolder, formInputPanel);
		if (resultHolder.isEmpty())
			return null;
		else
			return resultHolder.get(0);
	}
	
	public void fillZoomMap() {
		for (int i = 0; i < columnsForZoom.size(); i++) {
			zoomMap.put(columnsForZoom.get(i), getInput(columnsForZoom.get(i)));
		}
	}
	
	@SuppressWarnings("rawtypes")
	private int emptyTextFieldRecursive(JPanel currentPanel, int currentIndex) {
		for (int i = 0; i < currentPanel.getComponentCount(); i++) {
			Component c1 = currentPanel.getComponent(i);
			if (c1 instanceof JPanel) {
				currentIndex = emptyTextFieldRecursive((JPanel) c1,
						currentIndex);
			} else if (c1 instanceof JTextField) {
				currentIndex++;
				JTextField tf = (JTextField) c1;
				tf.setText("");
				if (currentIndex == 1)
					tf.requestFocus();

			} else if (c1 instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox) c1;
				if (cb.getName().equals("BAR_VAZI"))
					cb.setSelected(true);
				else
					cb.setSelected(false);

			} else if (c1 instanceof JComboBox) {
				JComboBox cb = (JComboBox) c1;
				cb.setSelectedIndex(0);
			}
		}
		return currentIndex;
	}

	public void fillNext(Vector<Object> rowData, String tName) {
		GenericTableModel gtm = (GenericTableModel) tableGrid.getModel();
		try {
			gtm.fillNext(rowData, tName);

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greska",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void emptyTextField() {
		int index = 0;
		index = emptyTextFieldRecursive(formInputPanel, index);

	}

	public void getActiveTextFields() {
		for (Component comp : order) {
			if (comp instanceof JTextField)
				if (((JTextField) comp).isEditable())
					orderActiveTextFields.add(comp);

		}

	}
	
	private int setFieldValuesRecursive(Vector<Object> values,
			JPanel currentPanel, int currentIndex) {
		for (int i = 0; i < currentPanel.getComponentCount(); i++) {
			Component c = currentPanel.getComponent(i);
			if (c instanceof JPanel)
				currentIndex = setFieldValuesRecursive(values, (JPanel) c,
						currentIndex);
			else if (c instanceof JTextField) {
				JTextField tf = (JTextField) c;
				tf.setText(values.get(currentIndex).toString());
				currentIndex++;
			} else if (c instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox) c;
				if (values.get(currentIndex).toString().equals("true"))
					cb.setSelected(true);
				else
					cb.setSelected(false);
				currentIndex++;
			}
		}

		return currentIndex;
	}
	
	private void setFieldValues(Vector<Object> values) {
		int index = 0;
		index = setFieldValuesRecursive(values, formInputPanel, index);
	}
	
	public void sync() {
		int index = tableGrid.getSelectedRow();
		if (index < 0) {
			emptyTextField();
			return;
		}
		Vector<Object> values = new Vector<Object>();
		for (int i = 0; i < tableGrid.getModel().getColumnCount(); i++) {
			values.add(tableGrid.getModel().getValueAt(index, i));
		}

		setFieldValues(values);
		if (tableName.equals("RACUN_POSLOVNIH_LICA")) {
			if ((boolean) tableGrid.getValueAt(tableGrid.getSelectedRow(), 5) == true)
				btnDelete.setVisible(true);
			else
				btnDelete.setVisible(false);
		}
	}
	
	public void searchRow() {
	}
	
	public GenericTableModel getGenTableModel() {
		return (GenericTableModel) tableGrid.getModel();
	}
	
	@SuppressWarnings("rawtypes")
	private void getVectorWithComponentValueRecursive(Vector<Object> values,
			JPanel currentPanel) {
		Vector<String> lookUpNames = getGenTableModel().getTableProperties()
				.getLookUpElementNames();
		for (int i = 0; i < currentPanel.getComponentCount(); i++) {
			Component c = currentPanel.getComponent(i);
			if (c instanceof JPanel)
				getVectorWithComponentValueRecursive(values, (JPanel) c);
			else if (c instanceof JFormattedTextField) {
				JFormattedTextField ftf = (JFormattedTextField) c;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date utilDate = null;
				if (!ftf.getText().trim().isEmpty()) {
					try {
						utilDate = df.parse(ftf.getText());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					values.add(new java.sql.Date(utilDate.getTime()));
				} else
					values.add(null);
			} else if (c instanceof JTextField) {
				JTextField tf = (JTextField) c;
				ColumnProperties cp = getGenTableModel().getTableProperties()
						.getColumnProperties(tf.getName());
				if (!lookUpNames.contains(tf.getName())) {
					if (!tf.getText().trim().isEmpty()) {
						if (cp.getType().equals("java.sql.Date")) {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd");
							Date utilDate = null;
							try {
								utilDate = sdf.parse(tf.getText().trim());
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							values.add(new java.sql.Date(utilDate.getTime()));
						} else
							values.add(tf.getText().trim());
					} else
						values.add(null);
				}
			} else if (c instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox) c;
				values.add(cb.isSelected());
			} else if (c instanceof JComboBox) {
				JComboBox cb = (JComboBox) c;
				values.add(cb.getSelectedItem());
			}
		}
	}
	
	protected Vector<Object> getVectorWithComponentValue() {
		Vector<Object> retVal = new Vector<Object>();
		getVectorWithComponentValueRecursive(retVal, formInputPanel);

		return retVal;
	}
	
	public void addRow() {
		try {
			validateInputs();
		} catch (InvalidInputException e1) {

			JOptionPane.showMessageDialog(this,
					"Desila se greška: \n" + e1.getMessage(), "Greska",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		GenericTableModel gtm = (GenericTableModel) tableGrid.getModel();
		int index;
		try {
			Vector<Object> rowData = getVectorWithComponentValue();
			gtm.insertRow(rowData);
			index = getTableRowIndex(rowData);
			tableGrid.setRowSelectionInterval(index, index);
			getCurrentState().setMode();
			// getStatusBar().setStatusPaneText(getCurrentState().toString());

		} catch (DataTruncation e) {
			JOptionPane.showMessageDialog(this, ERROR_DATA_TRUNCTUATION,
					"Greska", JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this,
					"Desila se greška: \n" + e.getMessage(), "Greska",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void deleteSelectedRow() {
		int index = tableGrid.getSelectedRow();
		if (index < 0)
			return;
		GenericTableModel gtm = (GenericTableModel) tableGrid.getModel();
		TableProperties cp = gtm.getTableProperties();
		Vector<Object> primKeys = new Vector<Object>();
		for (String i : cp.getPrimaryKeysFromTable()) {
			primKeys.add(getInput(i));
		}

		try {
			gtm.deleteRow(primKeys, index);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this,
					"Desila se greška: \n" + e.getMessage(), "Greska",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateRow() {
		int selectedIndex = tableGrid.getSelectedRow();
		if (selectedIndex < 0) {
			return;
		}

		try {
			validateInputs();
		} catch (InvalidInputException e1) {

			JOptionPane.showMessageDialog(this,
					"Desila se greška: \n" + e1.getMessage(), "Greska",
					JOptionPane.ERROR_MESSAGE);
			return;

		}

		GenericTableModel gtm = (GenericTableModel) tableGrid.getModel();
		Vector<Object> primKeys = new Vector<Object>();
		for (int i = 0; i < tableGrid.getColumnCount(); i++) {
			if (getPrimaryKeysNames().contains(tableGrid.getColumnName(i))) {
				primKeys.add(tableGrid.getValueAt(selectedIndex, i));
			}
		}
		Vector<Object> rowData = getVectorWithComponentValue();
		try {
			gtm.updateRow(selectedIndex, rowData, primKeys);
		} catch (DataTruncation e) {
			JOptionPane.showMessageDialog(this, ERROR_DATA_TRUNCTUATION,
					"Greska", JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this,
					"Desila se greška: \n" + e.getMessage(), "Greska",
					JOptionPane.ERROR_MESSAGE);

		}
		selectedIndex = getTableRowIndex(rowData);
		tableGrid.setRowSelectionInterval(selectedIndex, selectedIndex);
	}
	
	public Toolbar getToolbar() {
		return toolbar;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public JTable getTableGrid() {
		return tableGrid;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getCurrentState() {
		return state;
	}

	public Map<String, String> getZoomMap() {
		return zoomMap;
	}

	public void setColumnsForZoom(Vector<String> columnsForZoom) {
		this.columnsForZoom = columnsForZoom;
	}

	public Vector<String> getColumnsForZoom() {
		Vector<String> retVal = new Vector<String>();
		for (ZoomProperties zp : ((GenericTableModel) tableGrid.getModel())
				.getTableProperties().getZoom()) {
			for (ZoomElementProperties zep : zp.getZoomElements()) {
				retVal.add(zep.getFrom());
			}
			for (LookUpElementProperties lookUp : zp.getLookUpElements()) {
				retVal.add(lookUp.getName());
			}
		}
		return retVal;
	}

	/**
	 * Metoda koja vraca vektor naziva labela primarnih kljuceva.
	 * 
	 * @return
	 */
	public Vector<String> getPrimaryKeysNames() {
		GenericTableModel gtm = (GenericTableModel) tableGrid.getModel();
		return gtm.getTableProperties().getPrimaryKeysLabelName();
	}

	public String getTableName() {
		return tableName;
	}
	
	private int getTableRowIndex(Vector<Object> rowData) {
		int retVal = 0;
		Vector<Boolean> flags = new Vector<Boolean>();
		for (int i = 0; i < tableGrid.getRowCount(); i++) {
			int index = 0;
			for (int j = 0; j < tableGrid.getModel().getColumnCount(); j++) {

				String name = tableGrid.getModel().getColumnName(j);
				if (getGenTableModel().getTableProperties()
						.getPrimaryKeysLabelName().contains(name)) {
					Object obj = tableGrid.getModel().getValueAt(i, j);
					if (rowData.get(index).toString().equals(obj.toString()))
						flags.add(true);
					else {
						flags.add(false);
						break;
					}
					index++;

				}

			}
			if (!flags.contains(false)) {
				retVal = i;
				break;
			} else
				flags.clear();
		}

		return retVal;
	}

	protected abstract void validateInputs() throws InvalidInputException;
	
	public boolean isAllLetters(String s) {
		return s.matches("[a-zA-Z]+");
	}

	public boolean isAllNumbersOrBigLetters(String s) {
		return s.matches("[A-Z0-9]+");
	}

	public boolean isKontrolniBrojGenerisan() {
		return kontrolniBrojGenerisan;
	}

	public void setKontrolniBrojGenerisan(boolean kontrolniBrojGenerisan) {
		this.kontrolniBrojGenerisan = kontrolniBrojGenerisan;
	}

	public JButton getBtnGenerisiKontrolniBroj() {
		return btnGenerisiKontrolniBroj;
	}

	public static boolean isUpper(String s) {
		for (char c : s.toCharArray()) {
			if (!Character.isUpperCase(c))
				return false;
		}

		return true;
	}
	
	protected void addComponentToFormInputPanel(JComponent componentToAdd,
			String labelText, boolean mandatory) {
		JLabel label = new JLabel(labelText);
		label.setPreferredSize(new Dimension(180, 20));
		JLabel labelMandatory = new JLabel();
		if (mandatory)
			labelMandatory.setText("*");
		labelMandatory.setForeground(Color.RED);

		if (componentToAdd instanceof JPanel) {
			componentToAdd.add(label, 0);
			componentToAdd.add(labelMandatory,
					componentToAdd.getComponentCount());
			formInputPanel.add(componentToAdd);

		} else {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(label);
			panel.add(componentToAdd);
			panel.add(labelMandatory);
			formInputPanel.add(panel);
		}
		formInputPanel.add(Box.createHorizontalStrut(5));
		brojKolona++;
	}
}
