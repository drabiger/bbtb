package net.raebiger.bbtb.api.updaters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import net.raebiger.bbtb.api.domain.UserDomain;
import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.model.UserDao;

public class UserUpdater {
	private UserDomain	input;
	private UserDao		userDao;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public UserUpdater(UserDomain input, UserDao userDao) {
		this.input = input;
		this.userDao = userDao;
	}

	public Response create() {
		User newUser = new User();
		input.attachModel(newUser);
		newUser.setEmail(input.getEmail());
		userDao.persist(newUser);

		return update();
	}

	public Response update() {
		User existingUser = userDao.find(input.getUUID());
		if (existingUser == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("There is no user for given uuid.").build();
		}

		if (input.getDisplayName() != null && !input.getDisplayName().equals(existingUser.getDisplayName())) {
			existingUser.setName(input.getDisplayName());
		}

		return createResponseWithUriForUser(existingUser);
	}

	private Response createResponseWithUriForUser(User user) {
		URI userLocation;
		Response response;
		try {
			UserDomain newUser = new UserDomain(user);
			userLocation = new URI(newUser.getHref());
			response = Response.created(userLocation).build();
		} catch (URISyntaxException e) {
			LOG.log(Level.SEVERE, "Can't build URI for user.", e);
			response = Response.serverError().build();
		}
		return response;
	}
}
