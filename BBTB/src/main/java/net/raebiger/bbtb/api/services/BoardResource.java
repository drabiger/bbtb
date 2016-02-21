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

import net.raebiger.bbtb.api.domain.BoardDomain;
import net.raebiger.bbtb.api.domain.BoardPlacementDomain;
import net.raebiger.bbtb.api.updaters.BoardPlacementUpdater;
import net.raebiger.bbtb.api.updaters.BoardUpdater;
import net.raebiger.bbtb.model.AccessController;
import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.BoardSpecificsController;
import net.raebiger.bbtb.model.Position;
import net.raebiger.bbtb.model.Race;

@Path("boards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
@Transactional
public class BoardResource {

	@Context
	private UriInfo uriInfo;

	@Resource
	BoardSpecificsController boardAccessController;

	@Resource
	AccessController<Position> positionController;

	@Resource
	AccessController<Race> raceController;

	private static final Logger LOG = Logger.getLogger("BBTB");

	@GET
	@Path("{uuid}")
	public BoardDomain getSingleBoard(@PathParam("uuid") @NotNull String uuid) {
		Board board = boardAccessController.getByUuid(uuid);
		BoardDomain boardDomain = new BoardDomain(board);
		return boardDomain;
	}

	@GET
	@Path("all")
	@Transactional(readOnly = true)
	public List<BoardDomain> getAllBoards() {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http
		LOG.log(Level.INFO, "getAll");
		List<BoardDomain> boardDomains = new ArrayList<BoardDomain>();
		List<Board> allBoards = boardAccessController.getAll();

		for (Board board : allBoards) {
			BoardDomain boardDomain = new BoardDomain(board);
			boardDomains.add(boardDomain);
		}
		return boardDomains;
	}

	@GET
	@Path("my")
	@Transactional(readOnly = true)
	public List<BoardDomain> getMyBoards() {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http
		LOG.log(Level.INFO, "getMyBoards");
		List<BoardDomain> boardDomains = new ArrayList<BoardDomain>();
		List<Board> allBoards = boardAccessController.getMyBoards();

		for (Board board : allBoards) {
			BoardDomain boardDomain = new BoardDomain(board);
			boardDomains.add(boardDomain);
		}
		return boardDomains;
	}

	@GET
	@Path("TEST")
	@Transactional(readOnly = true)
	public BoardDomain getTestBoard() {
		List<BoardDomain> allBoards = getAllBoards();
		return allBoards.get(0);
	}

	@DELETE
	@Path("{uuid}")
	public Response deleteBoard(@PathParam("uuid") @NotNull String uuid) {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http
		Board boardToDelete = boardAccessController.getByUuid(uuid);
		if (boardToDelete == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("UUID does not exist").build();
		}
		boardAccessController.delete(boardToDelete);
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@PUT
	@Path("{uuid}/placements/{placementUuid}")
	public Response updatePlacement(@PathParam("uuid") @NotNull String boardUUID,
			@PathParam("placementUuid") @NotNull String placementUUID, BoardPlacementDomain placementDomain) {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http

		BoardPlacementUpdater updater = new BoardPlacementUpdater(placementUUID, placementDomain, boardUUID,
				boardAccessController, positionController);
		return updater.update();

	}

	@POST
	@Path("{uuid}/placements")
	public Response createPlacement(@PathParam("uuid") @NotNull String boardUUID,
			BoardPlacementDomain placementDomain) {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http

		BoardPlacementUpdater updater = new BoardPlacementUpdater(null, placementDomain, boardUUID,
				boardAccessController, positionController);
		return updater.update();

	}

	@DELETE
	@Path("{uuid}/placements/{placementUuid}")
	public Response deletePlacement(@PathParam("uuid") @NotNull String boardUUID,
			@PathParam("placementUuid") @NotNull String placementUUID, BoardPlacementDomain placementDomain) {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http

		BoardPlacementUpdater updater = new BoardPlacementUpdater(placementUUID, placementDomain, boardUUID,
				boardAccessController, positionController);
		return updater.delete();

	}

	@PUT
	@Path("{uuid}")
	public Response updateBoard(@PathParam("uuid") @NotNull String uuid, BoardDomain board) {
		// TODO enable XSRF protection by sending X-XSRF-TOKENs, see
		// https://docs.angularjs.org/api/ng/service/$http
		if (!uuid.equals(board.getUUID())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("UUIDs do not match").build();
		}

		BoardUpdater updater = new BoardUpdater(board, boardAccessController, raceController);
		return updater.update();
	}

}
