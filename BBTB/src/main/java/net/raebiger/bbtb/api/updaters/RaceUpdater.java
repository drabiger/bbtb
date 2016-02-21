package net.raebiger.bbtb.api.updaters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import net.raebiger.bbtb.api.domain.PositionDomain;
import net.raebiger.bbtb.api.domain.RaceDomain;
import net.raebiger.bbtb.model.AccessController;
import net.raebiger.bbtb.model.Position;
import net.raebiger.bbtb.model.Race;

public class RaceUpdater {
	private RaceDomain				input;
	private AccessController<Race>	raceController;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public RaceUpdater(RaceDomain input, AccessController<Race> raceController) {
		this.input = input;
		this.raceController = raceController;
	}

	public Response update() {
		Race existingRace = raceController.getByUuid(input.getUUID());
		if (existingRace == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("There is no race for given uuid.").build();
		}

		if (input.getName() != null && !input.getName().equals(existingRace.getName())) {
			existingRace.setName(input.getName());
		}

		List<PositionDomain> inputPositions = input.getPositions();
		if (inputPositions != null && inputPositions.size() > 0) {
			existingRace.getPositions().clear();
			for (PositionDomain inputPosition : inputPositions) {
				if (inputPosition.getModelOrNull() != null) {
					existingRace.addPosition(inputPosition.getModelOrNull());
				} else {
					Position newPosition = new Position();
					newPosition.setName(inputPosition.getName());
					newPosition.setQuantity(inputPosition.getQuantity());
					newPosition.setAbbreviation(inputPosition.getAbbreviation());
					existingRace.addPosition(newPosition);
				}
			}
		}
		raceController.persist(existingRace);

		URI raceLocation;
		Response response;
		try {
			RaceDomain newRace = new RaceDomain(existingRace);
			raceLocation = new URI(newRace.getHref());
			response = Response.created(raceLocation).build();
		} catch (URISyntaxException e) {
			LOG.log(Level.SEVERE, "Can't build URI for race.", e);
			response = Response.serverError().build();
		}
		return response;
	}
}
