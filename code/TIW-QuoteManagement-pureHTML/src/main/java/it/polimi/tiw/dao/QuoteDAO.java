package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polimi.tiw.beans.Quote;

public class QuoteDAO {

	private Connection connection;

	public QuoteDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Quote> findQuotesByUserId(int userId, String role) throws SQLException {
		List<Quote> quotes = new ArrayList<>();
		String performedAction = " finding quotes by UserID";
		String query;
		if(role.equalsIgnoreCase("client"))
			query = "SELECT * FROM quotemanagement.quote WHERE clientId=? ";
		else query = "SELECT * FROM quotemanagement.quote WHERE workerId=? ";

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1,userId);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
					Quote quote = new Quote();
					quote.setId(resultSet.getInt("id"));
					quote.setClientId(resultSet.getInt("clientId"));
					quote.setProductCode(resultSet.getInt("productCode"));
					quote.setWorkerId(resultSet.getInt("workerId"));
					quote.setPrice(resultSet.getInt("price"));
					quotes.add(quote);
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
		return quotes;
	}
	public Quote findQuoteById(int quoteId) throws SQLException {
		Quote quote = null;
		String performedAction = " finding quote by id";
		String query = "SELECT * FROM quotemanagement.quote WHERE id=? ";

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1,quoteId);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				quote = new Quote();
				quote.setId(resultSet.getInt("id"));
				quote.setClientId(resultSet.getInt("clientId"));
				quote.setProductCode(resultSet.getInt("productCode"));
				quote.setWorkerId(resultSet.getInt("workerId"));
				quote.setPrice(resultSet.getInt("price"));
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
		return quote;
	}
	public List<Quote> findUnmanagedQuotes() throws SQLException {
		List<Quote> quotes = new ArrayList<>();
		String performedAction = " finding unmanaged quotes";
		String query = "SELECT * FROM quotemanagement.quote WHERE workerId is null";
		

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Quote quote = new Quote();
				quote.setId(resultSet.getInt("id"));
				quote.setClientId(resultSet.getInt("clientId"));
				quote.setProductCode(resultSet.getInt("productCode"));
				quote.setWorkerId(resultSet.getInt("workerId"));
				quote.setPrice(resultSet.getInt("price"));
				quotes.add(quote);
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
		return quotes;
	}

	public int insertQuote(int clientId, int productCode) throws SQLException {
		String performedAction = " inserting quote";
		String insert_quote_query = "INSERT INTO quotemanagement.quote (clientId,productCode) VALUES (?,?)";
		String id_quote_query = "SELECT COUNT(*) AS quoteId FROM quotemanagement.quote";
		// String insert_option_query="INSERT INTO quotemanagement.quoteoptions (quoteId,optionCode) VALUES (?,?)";
		PreparedStatement preparedStatement1 = null;
		PreparedStatement preparedStatement2 = null;
		// PreparedStatement preparedStatement3 = null;
		connection.setAutoCommit(false);
		ResultSet resultSet = null;
		int quoteId = -1;
		try {
			preparedStatement1 = connection.prepareStatement(insert_quote_query);
			preparedStatement1.setInt(1, clientId);
			preparedStatement1.setInt(2, productCode);
			preparedStatement1.executeUpdate();
			preparedStatement2 = connection.prepareStatement(id_quote_query);
			resultSet = preparedStatement2.executeQuery();
			while(resultSet.next()) quoteId = resultSet.getInt("quoteId");
			/*
			 * preparedStatement3 = connection.prepareStatement(insert_option_query);
			 * 
			 * for(String s : chosenOptions) { preparedStatement3.setInt(1,quoteId);
			 * preparedStatement3.setInt(2,Integer.parseInt(s));
			 * preparedStatement3.executeUpdate(); }
			 */
			connection.commit();
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction);
		} finally {
			connection.setAutoCommit(true);
			try {
				resultSet.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the result set when" + performedAction);
			}
			try {
				preparedStatement1.close();
				preparedStatement2.close();
				// preparedStatement3.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction);
			}
		}

		return quoteId;
	}
	
	public void updateQuote(int quoteId, int workerId, int price) throws SQLException {
		String performedAction = " updating quote";
		String insert_quote_query = "UPDATE quotemanagement.quote SET workerId=? , price=? WHERE id=?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(insert_quote_query);
			preparedStatement.setInt(1, workerId);
			preparedStatement.setInt(2, price);
			preparedStatement.setInt(3, quoteId);
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
