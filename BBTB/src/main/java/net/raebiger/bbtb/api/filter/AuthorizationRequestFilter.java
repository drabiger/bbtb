package net.raebiger.bbtb.api.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.model.UserSpecificsAccessController;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationRequestFilter implements ContainerRequestFilter {
	@Context
	HttpServletRequest webRequest;

	@Resource
	private UserSpecificsAccessController userAccessController;

	@Autowired
	SessionInfo sessionInfo;

	private static final Logger LOG = Logger.getLogger("BBTB");

	private static final String RECAPTCHA_CLIENT_SECRET = "6Ldk_BMTAAAAAMS8EmBQhsvDewUCPluyFKfI_7Od";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		try {
			boolean authorized = false;

			if ("/users".equals(webRequest.getPathInfo()) && "post".equals(webRequest.getMethod().toLowerCase())) {
				LOG.log(Level.INFO, "User creation request.");
				String recaptchaResponseToken = webRequest.getParameter("recaptchaResponseToken");

				if (recaptchaResponseToken != null) {
					Client client = ClientBuilder.newClient();
					WebTarget target = client.target("https://www.google.com/recaptcha/api/siteverify")
							.queryParam("secret", RECAPTCHA_CLIENT_SECRET)
							.queryParam("response", recaptchaResponseToken);
					Response response = target.request(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
							.post(Entity.json(""));
					JsonElement jelement = new JsonParser().parse(response.readEntity(String.class));
					JsonObject jobject = jelement.getAsJsonObject();
					boolean success = jobject.get("success").getAsBoolean();
					LOG.info("Google recaptcha token verification response success: " + success);
					if (success) {
						authorized = true;
					}
				}
			} else {
				if (sessionInfo.getCurrentUser() != null) {
					authorized = true;
				} else {
					// TODO do we need the following code? is it all managed by
					// sessionInfo?
					String idTokenString = (String) webRequest.getSession().getAttribute("bbtbUserId");
					if (idTokenString != null && userAccessController.findByEmailOrNull(idTokenString) != null) {
						User userOrNull = userAccessController.findByEmailOrNull(idTokenString);
						if (userOrNull != null) {
							LOG.info("Request by user: " + idTokenString);
							authorized = true;
							sessionInfo.setCurrentUser(userOrNull);
						}
					}
				}
			}

			if (!authorized) {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.entity("User cannot access the resource.").build());
				LOG.warning("Unauthorized request attempt.");
			}
		} catch (Exception e) {
			requestContext.abortWith(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error.").build());
			LOG.log(Level.SEVERE, "Unexpected exception!", e);
		}
	}

}
