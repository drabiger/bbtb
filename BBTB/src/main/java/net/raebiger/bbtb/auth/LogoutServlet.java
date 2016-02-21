package net.raebiger.bbtb.auth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.model.UserSpecificsAccessController;

@Component
@Transactional
public class LogoutServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;
	private static final Logger	LOG					= Logger.getLogger("BBTB");

	@Resource
	UserSpecificsAccessController userAccessController;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		HttpSession session = request.getSession();
		if (session != null) {
			String idTokenString = (String) session.getAttribute("bbtbUserId");
			if (idTokenString != null) {
				User user = userAccessController.findByEmailOrNull(idTokenString);
				LOG.log(Level.INFO, "Logout: {0}", user);
			}
			session.invalidate();
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}
}
