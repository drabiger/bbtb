package net.raebiger.bbtb.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class OAuthServlet extends AbstractAuthorizationCodeServlet {

	private static final long	serialVersionUID	= -5369968763179761440L;
	static final String			CLIENT_ID			= "290685487945-bs2eir89lom13ffq89850jdth5ir2niv.apps.googleusercontent.com";
	static final String			CLIENT_SECRET		= "HHZ_jIhEwpxV9CBuRtaojaPS";
	// List the scopes your app requires:
	static List<String>			SCOPE				= Arrays.asList("https://www.googleapis.com/auth/plus.me");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// do stuff
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/bbtb/auth/oauth2callback");
		return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		// TODO obviously...
		return "1";
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		// TODO wrong tokens, ids etc.

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
				new JacksonFactory(), CLIENT_ID, // This comes from your
													// Developers Console
													// project
				CLIENT_SECRET, // This, as well
				SCOPE).setApprovalPrompt("force")
						// Set the access type to offline so that the token can
						// be refreshed.
						// By default, the library will automatically refresh
						// tokens when it
						// can, but this can be turned off by setting
						// dfp.api.refreshOAuth2Token=false in your
						// ads.properties file.
						.setAccessType("offline").build();
		return flow;
	}

}
