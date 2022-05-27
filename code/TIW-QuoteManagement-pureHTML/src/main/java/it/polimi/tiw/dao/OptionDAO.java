package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Option;

public class OptionDAO {

	private Connection connection;

	public OptionDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Option> findOptionsByProductCode(int productCode) throws SQLException {
		List<Option> options = new ArrayList<>();
		String performedAction = " finding options by product code";
		String query = "SELECT * FROM quotemanagement.productoptions JOIN quotemanagement.option ON option.code=productoptions.optionCode WHERE productCode=?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, productCode);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Option option = new Option();
				option.setCode(resultSet.getInt("code"));
				option.setName(resultSet.getString("name"));
				option.setType(resultSet.getString("type"));
				options.add(option);
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
		return options;
	}
	public List<Option> findOptionsByQuoteId(int productCode) throws SQLException {
		List<Option> options = new ArrayList<>();
		String performedAction = " finding options by quote id";
		String query = "SELECT * FROM quotemanagement.quoteoptions JOIN quotemanagement.option ON option.code=quoteoptions.optionCode WHERE quoteId=?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, productCode);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Option option = new Option();
				option.setCode(resultSet.getInt("code"));
				option.setName(resultSet.getString("name"));
				option.setType(resultSet.getString("type"));
				options.add(option);
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
		return options;
	}

	public void insertOption(int quoteId, int optionCode) throws SQLException {
		String performedAction = " inserting option";
		String insert_option_query = "INSERT INTO quotemanagement.quoteoptions (quoteId,optionCode) VALUES (?,?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(insert_option_query);
			preparedStatement.setInt(1, quoteId);
			preparedStatement.setInt(2, optionCode);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction);
		} finally {
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction);
			}
		}

	}

}
