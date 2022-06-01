package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
	}

	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = null;
		String password = null;
		String role = null;
		username = request.getParameter("username");
		password = request.getParameter("password");
		role = request.getParameter("role");
		if (username == null || password == null || role == null
				|| (!role.equalsIgnoreCase("client") && !role.equalsIgnoreCase("worker")) || username.isEmpty()
				|| password.isEmpty()) {
			warning(request, response, "Null username, password or role. Please try again.");
			return;
		}
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.findUser(username, role);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (user != null) {
			warning(request, response, "This username is not available. Please try again.");
		} else {
			try {
				userDAO.registerUser(username, password, role);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			response.sendRedirect(getServletContext().getContextPath() + "/GotoLogin");
		}
	}

	private void warning(HttpServletRequest request, HttpServletResponse response, String warning)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("warning", warning);
		templateEngine.process("Register.html", ctx, response.getWriter());
	}

}
