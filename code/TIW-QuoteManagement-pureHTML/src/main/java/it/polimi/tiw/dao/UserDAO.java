package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User findUser(String username, String password, String role) throws SQLException {
		User user = null;
		String performedAction = "finding a user by username, password and role";
		String query = "SELECT * FROM quotemanagement.user WHERE username = ? AND password = ? AND role = ?";
		/*
		 * if (role.equalsIgnoreCase("client")) { query =
		 * "SELECT * FROM quotemanagement.client WHERE username = ? AND password = ?"; }
		 * else { query =
		 * "SELECT * FROM quotemanagement.worker WHERE username = ? AND password = ?"; }
		 */
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, role);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User();
				user.setId(resultSet.getInt("id"));
				user.setUsername(username);
				user.setRole(role);
			}

		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction);
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the result set when" + performedAction);
			}
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction);
			}
		}
		return user;
	}

	public User findUser(String username, String role) throws SQLException {
		User user = null;
		String performedAction = "finding a user by username and role";
		String query = "SELECT * FROM quotemanagement.user WHERE username = ? AND role = ?";
		/*
		 * if (role.equalsIgnoreCase("client")) { query =
		 * "SELECT * FROM quotemanagement.client WHERE username = ?"; } else { query =
		 * "SELECT * FROM quotemanagement.worker WHERE username = ? "; }
		 */
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, role);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User();
				user.setId(resultSet.getInt("id"));
				user.setUsername(resultSet.getString("username"));
			}

		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction);
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the result set when" + performedAction);
			}
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction);
			}
		}
		return user;
	}

	public User findClientById(int clientId) throws SQLException {
		User user = null;
		String performedAction = "finding a client by Id";
		String query = "SELECT * FROM quotemanagement.user WHERE id = ? AND role = 'client'";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, clientId);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User();
				user.setId(clientId);
				user.setUsername(resultSet.getString("username"));
				user.setRole("client");
			}

		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction);
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the result set when" + performedAction);
			}
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction);
			}
		}
		return user;
	}

	public void registerUser(String username, String password, String role) throws SQLException {

		String performedAction = " registering a new user in the database";
		String query = "INSERT INTO quotemanagement.user (username,password,role) VALUES(?,?,?)";
		/*
		 * if (role.equalsIgnoreCase("client")) query =
		 * "INSERT INTO quotemanagement.client (username,password) VALUES(?,?)"; else if
		 * (role.equalsIgnoreCase("worker")) query =
		 * "INSERT INTO quotemanagement.worker (username,password) VALUES(?,?)";
		 */
		PreparedStatement preparedStatementAddUser = null;

		try {
			preparedStatementAddUser = connection.prepareStatement(query);
			preparedStatementAddUser.setString(1, username);
			preparedStatementAddUser.setString(2, password);
			preparedStatementAddUser.setString(2, role);
			preparedStatementAddUser.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction);
		} finally {
			try {
				preparedStatementAddUser.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction);
			}
		}
	}
}
