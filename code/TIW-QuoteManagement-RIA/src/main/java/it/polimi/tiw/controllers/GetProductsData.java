package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.lang.String;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.packets.ProductOptions;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetProductsData")
@MultipartConfig
public class GetProductsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetProductsData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		ProductDAO productDAO = new ProductDAO(connection);
		OptionDAO optionDAO = new OptionDAO(connection);
		List<Product> products = new ArrayList<Product>();
		List<Option> options = new ArrayList<Option>();
		List<ProductOptions> productsOptions= new ArrayList<ProductOptions>();
		try {
			products = productDAO.findAllProducts();
			for(Product p : products) {
				options = optionDAO.findOptionsByProductCode(p.getCode());
				productsOptions.add(new ProductOptions(p,options));
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover products and options");
			return;
		}
	
		String json = new Gson().toJson(productsOptions);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
