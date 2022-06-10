package it.polimi.tiw.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;

/**
 * Servlet Filter implementation class AdminChecker
 */

public class ClientChecker implements Filter {

	/**
	 * Default constructor.
	 */
	public ClientChecker() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Client filter executing ...\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/GotoLogin";
		
		HttpSession session = req.getSession();
		User user = null;
		user = (User) session.getAttribute("currentUser");
		if (!user.getRole().equals("client")) {
			/*res.setStatus(403);
			res.setHeader("Location", loginpath);
			System.out.print("Client checker FAILED...\n");
			return;*/
			res.sendRedirect(loginpath);
			return;
		}

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
