package net.raebiger.bbtb.auth;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;

import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.model.UserDao;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Service
@Transactional
public class GoogleAuthenticator extends HttpServlet {

	private static final long	serialVersionUID	= -3650137529811975784L;
	private static final Gson	GSON				= new Gson();

	static final String	CLIENT_ID			= "290685487945-bs2eir89lom13ffq89850jdth5ir2niv.apps.googleusercontent.com";
	static final String	CLIENT_SECRET		= "HHZ_jIhEwpxV9CBuRtaojaPS";
	static final String	APPS_DOMAIN_NAME	= "localhost";
	// List the scopes your app requires:
	static List<String>	SCOPE				= Arrays.asList("https://www.googleapis.com/auth/plus.me");

	@Autowired
	SessionInfo sessionInfo;

	private static final Logger LOG = Logger.getLogger("BBTB");

	/*
	 * Default HTTP transport to use to make HTTP requests.
	 */
	private static final HttpTransport TRANSPORT = new NetHttpTransport();

	/*
	 * Default JSON factory to use to deserialize JSON.
	 */
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

	@Autowired
	UserDao userDao;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		// Only connect a user that is not already connected.
		String idTokenString = (String) request.getSession().getAttribute("bbtbUserId");
		if (idTokenString != null) {
			LOG.log(Level.INFO, "Identified session of user '" + idTokenString + "'.");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().print(GSON.toJson("Successfully connected user."));
		} else {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY)
					.setAudience(Arrays.asList(CLIENT_ID)).build();
			try {
				ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
				getContent(request.getInputStream(), resultStream);
				String idTokenFromClient = new String(resultStream.toByteArray(), "UTF-8");

				if (idTokenFromClient != null && idTokenFromClient.length() > 0) {
					GoogleIdToken idToken = verifier.verify(idTokenFromClient);
					if (idToken != null) {
						// Case: Google verified user
						Payload payload = idToken.getPayload();
						String email = payload.getEmail();
						// Long expirationTimeSeconds =
						// payload.getExpirationTimeSeconds();
						// TODO handle expirationtimeinseconds

						System.out.println("User ID: " + payload.getSubject());
						LOG.log(Level.INFO, "Verified Google user {0}", email);

						User userOrNull = userDao.findByEmailOrNull(email);
						if (userOrNull == null) {
							// Case: Google verified user,
							// but user has no BBTB account (LOGIN FAILED)
							LOG.log(Level.INFO, "Could not link Google email address with existing BBTB user.");
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.getWriter().print(GSON.toJson("Invalid ID token (null response)."));
						} else {
							// Case: Google verified user,
							// user has BBBT account. (LOGIN SUCCESSFUL!)
							sessionInfo.setCurrentUser(userOrNull);
							LOG.log(Level.INFO, "Login: {0}", email);
							request.getSession().setAttribute("bbtbUserId", userOrNull.getEmail());
							response.setStatus(HttpServletResponse.SC_OK);
							response.getWriter().print(GSON.toJson("Google user verified."));
						}
					} else {
						// Case: No verification by Google
						LOG.log(Level.INFO, "Invalid ID token.");
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().print(GSON.toJson("Invalid ID token (null response)."));
					}
				} else {
					// Case: No verification by Google
					LOG.warning("Empty or null token from client.");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().print(GSON.toJson("Empty or null token."));
				}
			} catch (GeneralSecurityException e) {
				LOG.log(Level.WARNING, "Error verifying token", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().print(GSON.toJson("Failed to read token data from Google. " + e.getMessage()));
			}

		}

		// Ensure that this is no request forgery going on, and that the user
		// sending us this connect request is the user that was supposed to.
		// TODO use state parameter as xsrf token
		// if (request.getParameter("state") == null
		// ||
		// !request.getParameter("state").equals(request.getSession().getAttribute("state")))
		// {
		// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// response.getWriter().print(GSON.toJson("Invalid state parameter."));
		// return;
		// }

	}

	static void getContent(InputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {
		// Read the response into a buffered stream
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		int readChar;
		while ((readChar = reader.read()) != -1) {
			outputStream.write(readChar);
		}
		reader.close();
	}

}
