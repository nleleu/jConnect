package com.jconnect.core.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract class, manages a SQLite Database
 * 
 */
public abstract class DbliteConnection {
	private static final String DATABASE_INFO = "DATABASE_INFO";
	private static final String INFO_FIELD = "INFO_FIELD";
	private static final String INFO_VALUE = "INFO_VALUE";

	private static final String FIELD_VERSION = "VERSION";
	private Connection m_connection;

	/**
	 * Executed by {@link #createBDD(int)}
	 * Contains tables' creation requests
	 * @throws SQLException
	 */
	protected abstract void onCreate() throws SQLException;

	/**
	 * Executed by {@link #updateBDD(int)}
	 * Contains tables' update requests
	 * @throws SQLException
	 */
	protected abstract void onUpdate() throws SQLException;


	protected abstract void onDelete() throws SQLException;

	/**
	 * Constructor
	 * @param path = Database's filePath 
	 * @param version = Database's version
	 */
	protected DbliteConnection(String path, int version) {
		try {
			openConnection(path, version);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (SQLException e) {

			e.printStackTrace();
			System.exit(-1);
		}

	}

	/**
	 * Connects to database
	 * @param dbPath = Database's filePath 
	 * @param version = Database's version
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected void openConnection(String dbPath, int version)
			throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");


		File f = new File(dbPath);
		boolean exist = f.exists();
		if (!exist)
			new File(f.getParent()).mkdirs();

		m_connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		update("PRAGMA foreign_keys = ON;");
		if (!exist) {

			createBDD(version);
		} else { // VERIFICATION DATABASE UPDATE

			ResultSet rs = query("Select " + INFO_VALUE + " from "
					+ DATABASE_INFO + " where " + INFO_FIELD + "='"
					+ FIELD_VERSION + "'");
			rs.next();
			if (Integer.parseInt(rs.getString(INFO_VALUE)) < version)
				updateBDD(version);
		}

	}

	private void updateBDD(int version) throws SQLException {
		onUpdate();
		update("Update " + DATABASE_INFO + " set " + INFO_VALUE + "='"
				+ version + "' where " + DATABASE_INFO + "='" + FIELD_VERSION
				+ "'");

	}

	private void createBDD(int version) throws SQLException {
		update("create table " + DATABASE_INFO + "(" + INFO_FIELD + " text, "
				+ INFO_VALUE + " text, PRIMARY KEY (" + INFO_FIELD + "))");
		update("insert into " + DATABASE_INFO + " values ('" + FIELD_VERSION
				+ "', '" + version + "')");
		onCreate();

	}

	public void finalize() {
		closeConnection();

	}

	/**
	 * Manages Select queries
	 * @param query = Query string
	 * @return Query's resultSet
	 * @throws SQLException
	 */
	protected ResultSet query(String query) throws SQLException {
		Statement statement = m_connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec
		return statement.executeQuery(query);
	}

	/**
	 * Manages Update or Delete queries
	 * @param update = Query string
	 * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing 
	 * @throws SQLException
	 */
	protected int update(String update) throws SQLException {
		Statement statementUpdate = m_connection.createStatement();
		statementUpdate.setQueryTimeout(30);
		return statementUpdate.executeUpdate(update);
	}

	/**
	 * Close SQLite Connection
	 */
	protected void closeConnection() {
		try {
			if (m_connection != null)
				m_connection.close();
		} catch (SQLException e) {
			System.err.println(e);
		}

	}

}
