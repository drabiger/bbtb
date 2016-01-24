package net.raebiger.bbtb.api.updaters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.raebiger.bbtb.api.domain.UserDomain;
import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.model.UserDao;

public class UserUpdater {
	private UserDomain	input;
	private UserDao		userDao;

	private static final Logger LOG = Logger.getLogger("BBTB");

	private static final String MSG_DISPLAYNAME_VALIDATION = "A user's display name must contain at least three "
			+ "characters and a maximum of 15 and must not conflict with an existing display name.";

	public UserUpdater(UserDomain input, UserDao userDao) {
		this.input = input;
		this.userDao = userDao;
	}

	public Response create() {
		if (!validateDisplayName()) {
			return createResponseBadRequest(MSG_DISPLAYNAME_VALIDATION);
		}

		User newUser = new User();
		input.attachModel(newUser);
		newUser.setEmail(input.getEmail());
		userDao.persist(newUser);

		return update();
	}

	public Response update() {
		if (!validateDisplayName()) {
			return createResponseBadRequest(MSG_DISPLAYNAME_VALIDATION);
		}

		User existingUser = userDao.find(input.getUUID());
		if (existingUser == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("There is no user for given uuid.").build();
		}

		if (input.getDisplayName() != null && !input.getDisplayName().equals(existingUser.getDisplayName())) {
			existingUser.setName(input.getDisplayName());
		}

		return createResponseWithUriForUser(existingUser);
	}

	private boolean validateDisplayName() {
		if (input.getDisplayName() == null) {
			return false;
		}

		if (input.getDisplayName().length() < 3 && input.getDisplayName().length() > 15) {
			return false;
		}

		User existingUser = userDao.findByDisplayNameOrNull(input.getDisplayName());
		if (existingUser != null) {
			if (existingUser.getUUID() != input.getUUID()) {
				return false;
			}
		}
		return true;
	}

	private Response createResponseBadRequest(String message) {
		return Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
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
