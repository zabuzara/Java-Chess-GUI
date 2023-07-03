package www.zabuzara.com.tools;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade providing JDBC based operations.
 */
public class RelationalDatabases {

	/**
	 * Prevents external instantiation.
	 */
	private RelationalDatabases () {}
	

	/**
	 * Returns the column labels defined within the given JDBC metadata.
	 * @param jdbcTableMetaData the JDBC table metadata
	 * @return the column labels
	 * @throws NullPointerException if the given argument is {@code null}
	 * @throws SQLException if there is a JDBC related problem
	 */
	static public List<String> columnLabels (final ResultSetMetaData jdbcTableMetaData) throws NullPointerException, SQLException {
		final List<String> columnLabels = new ArrayList<>();
		for (int index = 1, columnCount = jdbcTableMetaData.getColumnCount(); index <= columnCount; ++index)
			columnLabels.add(jdbcTableMetaData.getColumnLabel(index));

		return columnLabels;
	}
	
	/**
	 * Returns Column names
	 * @param jdbcConnection JDBC connection
	 * @param sql SQL-Statement
	 * @return column names
	 * @throws NullPointerException if the given argument is {@code null}
	 * @throws SQLException if there is an SQL related problem
	 */
	static public List<String> getColumnNames (final Connection jdbcConnection, final String sql) throws NullPointerException, SQLException {
		final List<String> columnLabels = new ArrayList<>();
		try (PreparedStatement jdbcStatement = jdbcConnection.prepareStatement(sql)) {
			try (ResultSet jdbcTable = jdbcStatement.executeQuery()) {
				final ResultSetMetaData jdbcTableMetaData = jdbcTable.getMetaData();
				for (int index = 1, columnCount = jdbcTableMetaData.getColumnCount(); index <= columnCount; ++index)
					columnLabels.add(jdbcTableMetaData.getColumnLabel(index));
			}
		}
		return columnLabels;
	}


	/**
	 * Returns the remaining rows of the given result set as a list of row maps.
	 * @param jdbcResultSet the JDBC result set
	 * @param sortedColumns whether or not the columns shall be sorted 
	 * @return the list of row maps
	 * @throws NullPointerException if the given argument is {@code null}
	 * @throws SQLException if there is an SQL related problem
	 */
	static public List<Map<String,Object>> toRowMaps (final ResultSet jdbcTable, final boolean sortedColumns) throws NullPointerException, SQLException {
		final List<String> columnLabels = columnLabels(jdbcTable.getMetaData());

		final List<Map<String,Object>> rows = new ArrayList<>();
		while (jdbcTable.next()) {
			final Map<String,Object> row = sortedColumns ? new LinkedHashMap<>() : new HashMap<>();
			rows.add(row);

			for (final String columnLabel : columnLabels)
				row.put(columnLabel, jdbcTable.getObject(columnLabel));
		}

		return rows;
	}


	/**
	 * Executes any kind of SQL query statement (select, info, ...), returning the
	 * query result as a list of row maps.
	 * @param jdbcConnection the JDBC connection
	 * @param sql the SQL statement
	 * @param arguments the statement arguments, optionally including {@code null} values
	 * @return the list of row maps
	 * @throws NullPointerException if the given JDBC connection or SQL statement is {@code null}
	 * @throws SQLException if there is an SQL related problem
	 */
	static public List<Map<String,Object>> executeQuery (final Connection jdbcConnection, final String sql, final Object... arguments) throws NullPointerException, SQLException {
		try (PreparedStatement jdbcStatement = jdbcConnection.prepareStatement(sql)) {
			for (int index = 0; index < arguments.length; ++index)
				jdbcStatement.setObject(index + 1, arguments[index]);

			try (ResultSet jdbcTable = jdbcStatement.executeQuery()) {
				return RelationalDatabases.toRowMaps(jdbcTable, true);
			}
		}
	}


	/**
	 * Executes any kind of SQL insert statement, optionally returning an
	 * auto-generated key.
	 * @param jdbcConnection the JDBC connection
	 * @param sql the SQL statement
	 * @param arguments the statement arguments, optionally including {@code null} values
	 * @return the auto-generated key, or {@code null} for none
	 * @throws NullPointerException if the given JDBC connection or SQL statement is {@code null}
	 * @throws IllegalStateException if the insert statement created more or less than one row,
	 * 									or if more than one column was auto-generated 
	 * @throws SQLException if there is an SQL related problem
	 */
	static public Object executeInsert (final Connection jdbcConnection, final String sql, final Object... arguments) throws NullPointerException, IllegalStateException, SQLException {
		try (PreparedStatement jdbcStatement = jdbcConnection.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
			for (int index = 0; index < arguments.length; ++index)
				jdbcStatement.setObject(index + 1, arguments[index]);

			final int modifiedRowCount = jdbcStatement.executeUpdate();
			if (modifiedRowCount != 1) throw new IllegalStateException();

			try (ResultSet jdbcTable = jdbcStatement.getGeneratedKeys()) {
				final int columnCount = jdbcTable.getMetaData().getColumnCount();
				if (columnCount > 1) throw new IllegalStateException();
				if (columnCount == 0 || !jdbcTable.next()) return null;
				return jdbcTable.getObject(1);
			}
		}
	}


	/**
	 * Executes any kind of SQL update statement, returning the number of modified rows.
	 * @param jdbcConnection the JDBC connection
	 * @param sql the SQL statement
	 * @param arguments the statement arguments, optionally including {@code null} values
	 * @return the number of modified rows
	 * @throws NullPointerException if the given JDBC connection or SQL statement is {@code null}
	 * @throws SQLException if there is an SQL related problem
	 */
	static public long executeUpdate (final Connection jdbcConnection, final String sql, final Object... arguments) throws NullPointerException, SQLException {
		try (PreparedStatement jdbcStatement = jdbcConnection.prepareStatement(sql)) {
			for (int index = 0; index < arguments.length; ++index)
				jdbcStatement.setObject(index + 1, arguments[index]);

			jdbcStatement.executeUpdate();
			return jdbcStatement.getLargeUpdateCount();
		}
	}


	/**
	 * Executes any kind of SQL delete statement, returning the number of deleted rows.
	 * @param jdbcConnection the JDBC connection
	 * @param sql the SQL statement
	 * @param arguments the statement arguments, optionally including {@code null} values
	 * @return the number of deleted rows
	 * @throws NullPointerException if the given JDBC connection or SQL statement is {@code null}
	 * @throws SQLException if there is an SQL related problem
	 */
	static public long executeDelete (final Connection jdbcConnection, final String sql, final Object... arguments) throws NullPointerException, SQLException {
		return executeUpdate(jdbcConnection, sql, arguments);
	}
}