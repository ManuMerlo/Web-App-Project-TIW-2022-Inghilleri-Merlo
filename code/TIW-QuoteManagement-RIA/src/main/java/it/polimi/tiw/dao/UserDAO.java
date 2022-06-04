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

	public User findUser(String email, String password, String role) throws SQLException {
		User user = null;
		String performedAction = "finding a user by email, password and role";
		String query = "SELECT * FROM quotemanagement.user WHERE email = ? AND password = ? AND role = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, role);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User();
				user.setId(resultSet.getInt("id"));
				user.setEmail(email);
				user.setRole(role);
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

	public User findUserToRegister(String username, String email, String role) throws SQLException {
		User user = null;
		String performedAction = "finding a user by username, email and role";
		String query = "SELECT * FROM quotemanagement.user WHERE (username=? AND role = ?) OR email = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, role);
			preparedStatement.setString(3, email);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User();
				user.setId(resultSet.getInt("id"));
				user.setEmail(resultSet.getString("email"));
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
				user.setEmail(resultSet.getString("email"));
				user.setRole("client");
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

	public void registerUser(String username, String email, String password, String role) throws SQLException {

		String performedAction = " registering a new user in the database";
		String query = "INSERT INTO quotemanagement.user (username,email,password,role) VALUES(?,?,?,?)";
		
		PreparedStatement preparedStatementAddUser = null;

		try {
			preparedStatementAddUser = connection.prepareStatement(query);
			preparedStatementAddUser.setString(1, username);
			preparedStatementAddUser.setString(2, email);
			preparedStatementAddUser.setString(3, password);
			preparedStatementAddUser.setString(4, role);
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
