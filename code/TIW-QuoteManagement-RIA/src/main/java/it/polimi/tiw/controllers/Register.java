package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Register")
@MultipartConfig
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Register() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String username = null;
		String email = null;
		String password = null;
		String role = null;
		username = request.getParameter("username");
		email = request.getParameter("email");
		password = request.getParameter("password");
		role = request.getParameter("role");
		if (username == null || email == null || role == null
				|| (!role.equalsIgnoreCase("client") && !role.equalsIgnoreCase("worker")) || password == null
				|| username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}

		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.findUserToRegister(username, email, role);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}

		if (user != null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("This username and this email are not available");
		} else {
			try {
				userDAO.registerUser(username, email, password, role);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Internal server error, retry later");
			}
			response.setStatus(HttpServletResponse.SC_OK);
			// response.setContentType("application/json");
			// response.setCharacterEncoding("UTF-8");
			// response.getWriter().println(usrn);
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
