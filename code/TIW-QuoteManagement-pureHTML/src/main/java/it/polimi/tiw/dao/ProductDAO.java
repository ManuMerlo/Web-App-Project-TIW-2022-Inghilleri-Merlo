package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Product;

public class ProductDAO {

	private Connection connection;

	public ProductDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Product> findAllProducts() throws SQLException {

		List<Product> products = new ArrayList<>();
		String performedAction = " finding products";
		String query = "SELECT * FROM quotemanagement.product";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Product product = new Product();
				product.setCode(resultSet.getInt("code"));
				product.setName(resultSet.getString("name"));
				product.setImage(resultSet.getString("image"));
				products.add(product);
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
		return products;
	}
	
	public Product findProductByCode(int code) throws SQLException {

		Product product = null;
		String performedAction = " finding products";
		String query = "SELECT * FROM quotemanagement.product WHERE code=?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				product = new Product();
				product.setCode(resultSet.getInt("code"));
				product.setName(resultSet.getString("name"));
				product.setImage(resultSet.getString("image"));
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
		return product;
	}
	

}
