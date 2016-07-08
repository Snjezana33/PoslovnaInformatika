package gui.dialogs.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DBConnection;

public class Lookup {

	public static String getLookupColumnValue(String tableName, String columnName, String primColumnName, String primKey) throws SQLException {
		String retVal = "";
		if(tableName.trim().isEmpty() || columnName.trim().isEmpty() || primKey.trim().isEmpty())
			return retVal;
		Statement stmt = DBConnection.getConnection().createStatement();
		String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE " + primColumnName + " = '"
				+ primKey + "'";
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			retVal= rset.getString(columnName);
		}
		rset.close();
		stmt.close();
		
		return retVal;
	}
}
