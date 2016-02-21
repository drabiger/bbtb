package net.raebiger.bbtb.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.api.domain.RaceDomain;
import net.raebiger.bbtb.api.updaters.RaceUpdater;
import net.raebiger.bbtb.model.AccessController;
import net.raebiger.bbtb.model.Race;

@Path("races")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
@Transactional
public class RaceResource {

	@Context
	private UriInfo uriInfo;

	@Resource
	AccessController<Race> raceAccessController;

	private static final Logger LOG = Logger.getLogger("BBTB");

	@GET
	@Path("{uuid}")
	public RaceDomain getSingleRace(@PathParam("uuid") @NotNull String uuid) {
		Race race = raceAccessController.getByUuid(uuid);
		RaceDomain raceDomain = new RaceDomain(race);
		return raceDomain;
	}

	@GET
	@Path("all")
	@Transactional(readOnly = true)
	public List<RaceDomain> getAllRaces() {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http
		LOG.log(Level.INFO, "getAll");
		List<RaceDomain> raceDomains = new ArrayList<RaceDomain>();
		List<Race> allRaces = raceAccessController.getAll();

		for (Race race : allRaces) {
			RaceDomain raceDomain = new RaceDomain(race);
			raceDomains.add(raceDomain);
		}
		return raceDomains;
	}

	@POST
	public Response createRace(RaceDomain race) {
		Response response;

		if (race.getUUID() != null && race.getUUID().length() > 0) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("UUID submitted in payload. Are you sure you want to create a new entity?").build();
		}

		// set values of input RaceDomain to fresh model
		Race newModel = new Race();
		race.attachModel(newModel);
		raceAccessController.persist(newModel);
		RaceUpdater updater = new RaceUpdater(race, raceAccessController);
		response = updater.update();

		return response;
	}

	@PUT
	@Path("{uuid}")
	public Response updateRace(@PathParam("uuid") @NotNull String uuid, RaceDomain race) {
		if (!uuid.equals(race.getUUID())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("UUIDs do not match").build();
		}

		RaceUpdater updater = new RaceUpdater(race, raceAccessController);
		return updater.update();
	}

	@DELETE
	@Path("{uuid}")
	public Response deleteRace(@PathParam("uuid") @NotNull String uuid) {
		Race raceToDelete = raceAccessController.getByUuid(uuid);
		if (raceToDelete == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("UUID does not exist").build();
		}
		raceAccessController.delete(raceToDelete);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
