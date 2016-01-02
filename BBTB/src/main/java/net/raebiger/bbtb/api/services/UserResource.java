package net.raebiger.bbtb.api.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.api.domain.UserDomain;
import net.raebiger.bbtb.api.updaters.UserUpdater;
import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.model.UserDao;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
@Transactional
public class UserResource {

	@Context
	private UriInfo uriInfo;

	@Autowired
	UserDao userDao;

	private static final Logger LOG = Logger.getLogger("BBTB");

	@GET
	@Path("@me")
	public UserDomain getCurrentUser(@Context HttpServletRequest request) {
		String idTokenString = (String) request.getSession().getAttribute("bbtbUserId");
		if (idTokenString == null) {
			LOG.log(Level.WARNING, "No session found.");
			throw new NotFoundException();
		}

		User userOrNull = userDao.findByEmailOrNull(idTokenString);
		if (userOrNull == null) {
			return null;
		}
		UserDomain result = new UserDomain(userOrNull);

		return result;
	}

	@GET
	@Path("{uuid}")
	public UserDomain getUser(@PathParam("uuid") @NotNull String uuid) {
		User userOrNull = userDao.find(uuid);
		if (userOrNull == null) {
			return null;
		}
		UserDomain result = new UserDomain(userOrNull);

		return result;
	}

	@POST
	public Response createUserAccount(UserDomain userInput) {

		UserUpdater updater = new UserUpdater(userInput, userDao);
		LOG.log(Level.INFO, "Created user {0}.", userInput.getEmail());
		return updater.create();
	}

}
