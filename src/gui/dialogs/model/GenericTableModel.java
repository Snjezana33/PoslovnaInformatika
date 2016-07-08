package gui.dialogs.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import table.property.ColumnProperties;
import table.property.LookUpElementProperties;
import table.property.PropertiesContainer;
import table.property.TableProperties;
import table.property.ZoomElementProperties;
import table.property.ZoomProperties;
import utils.SortUtils;
import db.DBConnection;

@SuppressWarnings("serial")
public class GenericTableModel extends DefaultTableModel {

	private String tableName;

	private static final int CUSTOM_ERROR_CODE = 50000;
	private static final String ERROR_RECORD_WAS_CHANGED = "Slog je promenjen od strane drugog korisnika. Molim vas, pogledajte njegovu trenutnu vrednost";
	private static final String ERROR_RECORD_WAS_DELETED = "Slog je obrisan od strane drugog korisnika";

	private TableProperties tableProperties;

	public GenericTableModel(TableProperties tableProperties) {
		super(tableProperties.getColumnsLabel(), 0);
		this.tableName = tableProperties.getTableName();
		this.tableProperties = tableProperties;

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void open() throws SQLException {
		fillData(getSQLForOpenWithOrderBy());
	}

	/**
	 * Metoda koja popunjava tabelu forme na osnovu prosledjenog sql upita.
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	public void fillData(String sql) throws SQLException {
		setRowCount(0);
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);

		while (rset.next()) {
			Vector<Object> rowData = new Vector<Object>();
			
			for (String name : tableProperties.getColumnsNameForForm()) {
				ColumnProperties cp = tableProperties.getColumnProperties(name);
				if (cp != null) {
					if (cp.getType().equals("java.sql.Date")) {
						rowData.add(rset.getDate(name));
					} else
						rowData.add(rset.getObject(name));
				} else
					rowData.add(rset.getObject(name));
			}

			addRow(rowData);

		}
		rset.close();
		stmt.close();
		fireTableDataChanged();
	}

	private void checkRow(int index, Vector<Object> primKeys)
			throws SQLException {

		DBConnection.getConnection().setTransactionIsolation(
				Connection.TRANSACTION_REPEATABLE_READ);
		PreparedStatement selectStmt = DBConnection.getConnection()
				.prepareStatement(getSQLForCheckRow());

		for (int i = 0; i < tableProperties.getPrimaryKeysFromTable().size(); i++) {
			selectStmt.setObject(i + 1, primKeys.get(i));
		}

		ResultSet rset = selectStmt.executeQuery();

		Vector<Object> values = new Vector<Object>();
		Boolean postoji = false;
		String errorMsg = "";
		while (rset.next()) {
			for (String name : tableProperties.getColumnsNameForForm()) {
				ColumnProperties cp = tableProperties.getColumnProperties(name);
				if (cp != null) {
					if (cp.getType().equals("java.sql.Date")) {
						values.add(rset.getDate(cp.getName()));
					} else
						values.add(rset.getObject(name));
				} else
					values.add(rset.getObject(name));
			}
			postoji = true;
		}
		boolean flag = false;
		for (int i = 0; i < values.size(); i++) {
			if (SortUtils.getLatCyrCollator().compare(
					values.get(i).toString().trim(),
					getValueAt(index, i).toString().trim()) != 0) {
				flag = true;
				break;
			}
		}
		if (!postoji) {
			removeRow(index);
			fireTableDataChanged();
			errorMsg = ERROR_RECORD_WAS_DELETED;
		} else if (flag) {

			for (int i = 0; i < values.size(); i++) {
				setValueAt(values.get(i), index, i);
			}
			fireTableDataChanged();
			errorMsg = ERROR_RECORD_WAS_CHANGED;
		}
		rset.close();
		selectStmt.close();
		DBConnection.getConnection().setTransactionIsolation(
				Connection.TRANSACTION_READ_COMMITTED);
		if (errorMsg != "") {
			DBConnection.getConnection().commit();
			throw new SQLException(errorMsg, "", CUSTOM_ERROR_CODE);
		}
	}

	/**
	 * Metoda koja brise selektovan red iz tabele.
	 * 
	 * @param primaryKeys
	 * @param index
	 * @throws SQLException
	 */
	public void deleteRow(Vector<Object> primaryKeys, int index)
			throws SQLException {
		checkRow(index, primaryKeys);
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				getSQLForDelete());

		for (int i = 0; i < primaryKeys.size(); i++) {
			stmt.setObject(i + 1, primaryKeys.get(i));
		}

		// Brisanje iz baze
		int rowsAffected = stmt.executeUpdate();
		stmt.close();
		DBConnection.getConnection().commit();
		if (rowsAffected > 0) {
			// i brisanje iz TableModel-a
			removeRow(index);
			fireTableDataChanged();
		}
	}

	/**
	 * Metoda koja dodaje novi red u tabelu.
	 * 
	 * @param rowData
	 * @return
	 * @throws SQLException
	 */
	public int insertRow(Vector<Object> rowData) throws SQLException {
		int retVal = 0;
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				getSQLForInsert());
		
		for (int i = 0; i < rowData.size(); i++) {
			stmt.setObject(i + 1, rowData.get(i));
		}
		int rowsAffected = stmt.executeUpdate();
		stmt.close();
		DBConnection.getConnection().commit();

		if (rowsAffected > 0) {
			// i unos u TableModel

			// addRow(rowData);
			// retVal = sortedInsert(rowData);

			open();
			// fireTableDataChanged();
		}

		return retVal;
	}

	/**
	 * Metoda za izmenu selektovanog reda u tabeli.
	 * 
	 * @param index
	 * @param rowData
	 * @param kljucevi
	 * @throws SQLException
	 */
	public void updateRow(int index, Vector<Object> rowData,
			Vector<Object> kljucevi) throws SQLException {
		checkRow(index, kljucevi);
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				getSQLForUpdate());

		for (int i = 0; i < rowData.size(); i++) {
			stmt.setObject(i + 1, rowData.get(i));
		}

		int index2 = rowData.size();
		for (Object obj : kljucevi) {
			stmt.setObject(index2 + 1, obj);
			index2++;
		}

		int rowsAffected = stmt.executeUpdate();
		stmt.close();
		DBConnection.getConnection().commit();
		if (rowsAffected > 0) {

			open();
		}

	}

	/**
	 * Metoda za pretrazivanje.
	 * 
	 * @param rowData
	 * @throws SQLException
	 */
	public void searchRow(Vector<Object> rowData) throws SQLException {
		setRowCount(0);
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				getSQLForSearch(rowData));

		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			Vector<Object> rowData1 = new Vector<Object>();

			for (String name : tableProperties.getColumnsNameForForm()) {
				ColumnProperties cp = tableProperties.getColumnProperties(name);
				if (cp != null) {
					if (cp.getType().equals("java.sql.Date"))
						rowData1.add(rset.getDate(name));
					else
						rowData1.add(rset.getObject(name));
				} else
					rowData1.add(rset.getObject(name));
			}

			addRow(rowData1);
		}
		rset.close();
		stmt.close();
		fireTableDataChanged();

	}

	/**
	 * Metoda za sortiranje prilikom unosa novog reda u tabelu.
	 * 
	 * @param rowData
	 * @param key
	 * @return
	 */
	private int sortedInsert(Vector<Object> rowData) {
		int left = 0;
		int right = getRowCount() - 1;
		int mid = (left + right) / 2;

		for (int i = 0; i < getColumnCount(); i++) {
			String name = getColumnName(i);
			if (tableProperties.getPrimaryKeysLabelName().contains(name)) {
				while (left <= right) {
					mid = (left + right) / 2;
					Object aSifra = getValueAt(mid, i);
					Object data = rowData.get(i);
					;

					if (SortUtils.getLatCyrCollator().compare(
							data.toString().trim(), aSifra.toString().trim()) > 0)
						left = mid + 1;
					else if (SortUtils.getLatCyrCollator().compare(
							data.toString().trim(), aSifra.toString().trim()) < 0)
						right = mid - 1;
					else
						// ako su jednaki: to ne moze da se desi ako tabela ima
						// primarni
						// kljuc
						break;

				}
			}
		}
		insertRow(left, rowData);

		return left;
	}

	/**
	 * Metoda koja popunjava tabelu po odredjenom kriterijumu. Koristi se za
	 * next mehanizam.
	 * 
	 * @param rowData
	 * @param tName
	 * @throws SQLException
	 */
	public void fillNext(Vector<Object> rowData, String tName)
			throws SQLException {
		setRowCount(0);
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				getSQLForNext(tName));

		for (int i = 0; i < rowData.size(); i++) {
			stmt.setObject(i + 1, rowData.get(i));
		}
		ResultSet rset = stmt.executeQuery();

		while (rset.next()) {
			Vector<Object> rowData1 = new Vector<Object>();
			for (String name : tableProperties.getColumnsNameForForm()) {
				ColumnProperties cp = tableProperties.getColumnProperties(name);
				if (cp != null) {
					if (cp.getType().equals("java.sql.Date")) {
						rowData1.add(rset.getDate(cp.getName()));
					} else
						rowData1.add(rset.getObject(name));
				} else
					rowData1.add(rset.getObject(name));
			}
			addRow(rowData1);
		}

		rset.close();
		stmt.close();
		fireTableDataChanged();
	}

	/**
	 * Metoda koja generise sql upit za popunjavanje tabele prilikom otvaranja
	 * forme.
	 * 
	 * @return
	 */
	private String getSQLForOpen() {
		String retVal = "";
		// provera da li radimo spajanje tabela ili ne
		if (tableProperties.getZoom().isEmpty()) {
			retVal = "SELECT DISTINCT ";
			int i = 0;
			for (ColumnProperties cp : tableProperties.getColumns()) {
				i++;
				if (i != tableProperties.getColumns().size()) {
					retVal = retVal + cp.getName() + ", ";
				} else if (i == tableProperties.getColumns().size()) {
					retVal = retVal + cp.getName();
				}
			}
			retVal = retVal + " FROM " + tableName;
		} else if (!tableProperties.getZoom().isEmpty()) {
			retVal = "SELECT DISTINCT ";
			Vector<String> lookUpColumns = new Vector<String>();
			Vector<String> zoomTables = new Vector<String>();
			for (ZoomProperties zp : tableProperties.getZoom()) {
				zoomTables.add(zp.getTable());
				for (LookUpElementProperties lookUp : zp.getLookUpElements()) {
					lookUpColumns.add(lookUp.getName());
				}

			}

			for (int i = 0; i < tableProperties.getColumns().size(); i++) {
				if (i != tableProperties.getColumns().size() - 1) {
					retVal = retVal + tableName + "."
							+ tableProperties.getColumns().get(i).getName()
							+ ", ";
				} else if (i == tableProperties.getColumns().size() - 1) {
					retVal = retVal + tableName + "."
							+ tableProperties.getColumns().get(i).getName();
				}
			}
			int i = 0;
			for (String t : zoomTables) {
				for (String c : lookUpColumns) {
					i++;
					if (i != lookUpColumns.size() && lookUpColumns.size() > 0) {
						retVal = retVal + ", " + t + "." + c + ", ";
					} else if (i == lookUpColumns.size()) {
						retVal = retVal + ", " + t + "." + c;
					}
				}

			}
			retVal = retVal + " FROM " + tableName;

			for (String t : zoomTables) {
				int j = 0;
				ZoomProperties zp = tableProperties.getZoomItemByTableName(t);
				String tName = "";
				if (tableProperties.checkZoomElements(zp) > zp
						.getZoomElements().size()) {
					int index = 0;
					tName = t;
					for (ZoomElementProperties zep : zp.getZoomElements()) {
						index++;
						String tableNameWithPrefix = t + index;
						retVal = retVal + " JOIN " + t + " "
								+ tableNameWithPrefix + " ON " + tableName
								+ "." + zep.getTo() + " = "
								+ tableNameWithPrefix + "." + zep.getFrom();
					}
				} else if (!t.equals(tName)) {
					retVal = retVal + " JOIN " + t + " ON ";
					for (ZoomElementProperties zep : zp.getZoomElements()) {
						j++;
						if (j != zp.getZoomElements().size()) {
							retVal = retVal + tableName + "." + zep.getTo()
									+ " = " + t + "." + zep.getFrom() + " AND ";
						} else if (j == zp.getZoomElements().size()) {
							retVal = retVal + tableName + "." + zep.getTo()
									+ " = " + t + "." + zep.getFrom();
						}
					}
				}
			}

		}
		System.out.println(retVal);
		return retVal;
	}

	private String getSQLForOpenWithOrderBy() {
		String retVal = "";
		// provera da li radimo spajanje tabela ili ne
		if (tableProperties.getZoom().isEmpty()) {
			retVal = "SELECT DISTINCT ";
			int i = 0;
			for (ColumnProperties cp : tableProperties.getColumns()) {
				i++;
				if (i != tableProperties.getColumns().size()) {
					retVal = retVal + cp.getName() + ", ";
				} else if (i == tableProperties.getColumns().size()) {
					retVal = retVal + cp.getName();
				}
			}
			retVal = retVal + " FROM " + tableName + " ORDER BY "
					+ tableProperties.getColumns().get(0).getName();
		} else if (!tableProperties.getZoom().isEmpty()) {
			retVal = "SELECT DISTINCT ";
			Vector<String> lookUpColumns = new Vector<String>();
			Vector<String> zoomTables = new Vector<String>();
			for (ZoomProperties zp : tableProperties.getZoom()) {
				zoomTables.add(zp.getTable());
				for (LookUpElementProperties lookUp : zp.getLookUpElements()) {
					lookUpColumns.add(lookUp.getName());
				}

			}

			for (int i = 0; i < tableProperties.getColumns().size(); i++) {
				if (i != tableProperties.getColumns().size() - 1) {
					retVal = retVal + tableName + "."
							+ tableProperties.getColumns().get(i).getName()
							+ ", ";
				} else if (i == tableProperties.getColumns().size() - 1) {
					retVal = retVal + tableName + "."
							+ tableProperties.getColumns().get(i).getName();
				}
			}
			int i = 0;
			for (String t : zoomTables) {
				for (String c : lookUpColumns) {
					i++;
					if (i != lookUpColumns.size() && lookUpColumns.size() > 0) {
						retVal = retVal + ", " + t + "." + c + ", ";
					} else if (i == lookUpColumns.size()) {
						retVal = retVal + ", " + t + "." + c;
					}
				}

			}
			retVal = retVal + " FROM " + tableName;
			for (String t : zoomTables) {
				int j = 0;
				ZoomProperties zp = tableProperties.getZoomItemByTableName(t);
				String tName = "";
				if (tableProperties.checkZoomElements(zp) > zp
						.getZoomElements().size()) {
					int index = 0;
					tName = t;
					for (ZoomElementProperties zep : zp.getZoomElements()) {
						index++;
						String tableNameWithPrefix = t + index;
						retVal = retVal + " JOIN " + t + " "
								+ tableNameWithPrefix + " ON " + tableName
								+ "." + zep.getTo() + " = "
								+ tableNameWithPrefix + "." + zep.getFrom();
					}
				} else if (!t.equals(tName)) {
					retVal = retVal + " JOIN " + t + " ON ";
					for (ZoomElementProperties zep : zp.getZoomElements()) {
						j++;
						if (j != zp.getZoomElements().size()) {
							retVal = retVal + tableName + "." + zep.getTo()
									+ " = " + t + "." + zep.getFrom() + " AND ";
						} else if (j == zp.getZoomElements().size()) {
							retVal = retVal + tableName + "." + zep.getTo()
									+ " = " + t + "." + zep.getFrom();
						}
					}
				}

			}

			retVal = retVal + " ORDER BY "
					+ tableProperties.getColumns().get(0).getName();

		}
		System.out.println(retVal);
		return retVal;
	}

	/**
	 * Metoda koja generise sql upit za pretrazivanje podataka.
	 * 
	 * @return
	 */
	private String getSQLForSearch(Vector<Object> rowData) {
		String retVal = "";
		if (tableProperties.getZoom().isEmpty()) {
			int i = 0;
			for (ColumnProperties cp : tableProperties.getColumns()) {
				i++;
				if (i != tableProperties.getColumns().size()) {
					retVal = retVal + cp.getName() + ", ";
				} else if (i == tableProperties.getColumns().size()) {
					retVal = retVal + cp.getName();
				}
			}
			retVal = getSQLForOpen() + " WHERE ";
			int index = 0;
			for (ColumnProperties cp : tableProperties.getColumns()) {
				if (rowData.isEmpty()) {
					retVal = getSQLForOpen();
					break;
				}
				if(index < rowData.size())
				if (rowData.get(index).toString().trim().length() > 0) {
					if (index >= 1) {
						retVal = retVal + " AND " + cp.getName() + " LIKE '%"
								+ rowData.get(index).toString() + "%'";
					} else
						retVal = retVal + cp.getName() + " LIKE '%"
								+ rowData.get(index).toString() + "%'";
				}
				index++;
			}

		} else if (!tableProperties.getZoom().isEmpty()) {
			retVal = getSQLForOpen();
			int index = 0;
			for (ColumnProperties cp : tableProperties.getColumns()) {
				if (!rowData.isEmpty())
					if(index < rowData.size())
					if (rowData.get(index) != null)
						if (rowData.get(index).toString().trim().length() > 0) {
							if (cp.getType().equals("java.sql.Date"))
								retVal = retVal + " AND " + tableName + "."
										+ cp.getName() + " = '"
										+ rowData.get(index).toString() + "'";
							else if (cp.getType().equals("java.lang.Boolean"))
								retVal = retVal + " AND " + tableName + "."
										+ cp.getName() + " = '"
										+ rowData.get(index).toString() + "'";
							else
								retVal = retVal + " AND " + tableName + "."
										+ cp.getName() + " LIKE '%"
										+ rowData.get(index).toString() + "%'";
						}
				index++;
			}
		}
		retVal = retVal + " ORDER BY "
				+ tableProperties.getColumns().get(0).getName();
		System.out.println(retVal);
		return retVal;
	}

	/**
	 * Metoda koja generise sql upit za next mehanizam.
	 * 
	 * @param tName
	 * @return
	 */
	private String getSQLForNext(String tName) {
		String retVal = getSQLForOpen();
		TableProperties tp = PropertiesContainer.getInstance().getTablesMap()
				.get(tName);
		for (String c : tp.getColumnsForNext()) {
			retVal = retVal + " AND " + tableName + "." + c + " = ?";
		}

		System.out.println(retVal);
		return retVal;
	}

	/**
	 * Metoda koja generise sql upit za dodavanje podataka u tabelu.
	 * 
	 * @return
	 */
	private String getSQLForInsert() {

		String retVal = "INSERT INTO " + tableName + " (";
		int i = 0;
		for (ColumnProperties cp : tableProperties.getColumns()) {
			i++;
			if (i != tableProperties.getColumns().size()) {
				retVal = retVal + cp.getName() + ", ";
			} else if (i == tableProperties.getColumns().size()) {
				retVal = retVal + cp.getName();
			}
		}

		retVal = retVal + ") VALUES (";
		int j = 0;
		for (ColumnProperties cp : tableProperties.getColumns()) {
			j++;
			if (j != tableProperties.getColumns().size())
				retVal = retVal + "?, ";
			else if (j == tableProperties.getColumns().size())
				retVal = retVal + "?";
		}

		retVal = retVal + ")";
		System.out.println(retVal);
		return retVal;

	}

	/**
	 * Metoda koja generise sql upit za brisanje podataka iz tabele.
	 * 
	 * @return
	 */
	private String getSQLForDelete() {

		String retVal = "DELETE FROM " + tableName + " WHERE ";
		int i = 0;
		for (String cp : tableProperties.getPrimaryKeysFromTable()) {
			i++;
			if (i != tableProperties.getPrimaryKeysFromTable().size()) {
				retVal = retVal + cp + " = ? " + "AND ";
			} else if (i == tableProperties.getPrimaryKeysFromTable().size())
				retVal = retVal + cp + " = ?";
		}
		System.out.println(retVal);
		return retVal;
	}

	/**
	 * Metoda koja generise sql upit za izmenu podataka u tabeli.
	 * 
	 * @return
	 */
	private String getSQLForUpdate() {

		String retVal = "UPDATE " + tableName + " SET ";
		int i = 0;
		for (ColumnProperties cp : tableProperties.getColumns()) {
			i++;
			if (i != tableProperties.getColumns().size()) {
				retVal = retVal + cp.getName() + " = ?,";
			} else if (i == tableProperties.getColumns().size())
				retVal = retVal + cp.getName() + " = ?";
		}

		retVal = retVal + " WHERE ";
		int j = 0;
		for (String cp : tableProperties.getPrimaryKeysFromTable()) {
			j++;
			if (j != tableProperties.getPrimaryKeysFromTable().size()) {
				retVal = retVal + cp + " = ? " + "AND ";
			} else if (j == tableProperties.getPrimaryKeysFromTable().size())
				retVal = retVal + cp + " = ?";
		}
		System.out.println(retVal);
		return retVal;
	}

	/**
	 * Metoda koja generise sql upit za checkRow metodu.
	 * 
	 * @return
	 */
	private String getSQLForCheckRow() {
		String retVal = "";
		if (tableProperties.getZoom().isEmpty()) {
			retVal = "SELECT * FROM " + tableName + " WHERE ";
			int i = 0;
			int size = tableProperties.getPrimaryKeysFromTable().size();
			for (String pKey : tableProperties.getPrimaryKeysFromTable()) {
				i++;
				if (i != size) {
					retVal = retVal + pKey + " = ? AND ";
				} else if (i == size) {
					retVal = retVal + pKey + " = ?";
				}
			}
		} else if (!tableProperties.getZoom().isEmpty()) {
			retVal = getSQLForOpen();
			for (int i = 0; i < tableProperties.getPrimaryKeysFromTable()
					.size(); i++) {
				String columnName = tableProperties.getPrimaryKeysFromTable()
						.get(i);
				retVal = retVal + " AND " + tableName + "." + columnName
						+ " = ?";
			}
		}
		System.out.println(retVal);
		return retVal;
	}

	/**
	 * Metoda koja vraca vektor indeksa kolona koje se ne trebaju prikazati.
	 * 
	 * @return
	 */
	public Vector<Integer> getExcludedIndexes() {
		return tableProperties.getIndexesOfExcludedColumns();
	}

	/**
	 * Metoda koja vraca table properties za formu.
	 * 
	 * @return
	 */
	public TableProperties getTableProperties() {
		return tableProperties;

	}

}
