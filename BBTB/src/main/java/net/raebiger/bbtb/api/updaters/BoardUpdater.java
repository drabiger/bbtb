package net.raebiger.bbtb.api.updaters;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import net.raebiger.bbtb.api.domain.BoardDomain;
import net.raebiger.bbtb.model.AccessController;
import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.BoardPlacement;
import net.raebiger.bbtb.model.BoardPlacement.Team;
import net.raebiger.bbtb.model.Race;
import net.raebiger.bbtb.util.StringUtil;

public class BoardUpdater {
	private BoardDomain input;

	private static final Logger		LOG	= Logger.getLogger("BBTB");
	private AccessController<Race>	raceController;
	private AccessController<Board>	boardAccManager;

	public BoardUpdater(BoardDomain input, AccessController<Board> boardAccManager,
			AccessController<Race> raceController) {
		this.input = input;
		this.boardAccManager = boardAccManager;
		this.raceController = raceController;
	}

	public Response update() {

		Board existingBoard = boardAccManager.getByUuid(input.getUUID());
		if (existingBoard == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("There is no board for given uuid.").build();
		}

		boolean dirty = false;
		String trimmedBoardName = input.getName().trim();
		if (trimmedBoardName != null && !trimmedBoardName.equals(existingBoard.getName())) {
			existingBoard.setName(trimmedBoardName);
			dirty = true;
		}

		String trimmedDescription = StringUtil.getTrimmedValueOrNull(input.getDescription());
		if (trimmedDescription != null) {
			String safeHtmlValue = trimmedDescription; // ESAPI.encoder().encodeForHTML(trimmedDescription);
			if (!safeHtmlValue.equals(existingBoard.getDescription())) {
				existingBoard.setDescription(safeHtmlValue);
				dirty = true;
			}
		}

		if (input.getRace1() != null && (existingBoard.getTeam1Race() == null
				|| !input.getRace1().getUUID().equals(existingBoard.getTeam1Race().getUUID()))) {
			Race newRace1 = raceController.getByUuid(input.getRace1().getUUID());
			if (newRace1 == null) {
				LOG.log(Level.INFO, "UUID of race1 '{0}' does not match any existing race", input.getRace1().getUUID());
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("UUID of race1 does not match any existing race").build();
			}
			existingBoard.setTeam1Race(newRace1);
			dirty = true;
		}

		if (input.getRace2() != null && (existingBoard.getTeam2Race() == null
				|| !input.getRace2().getUUID().equals(existingBoard.getTeam2Race().getUUID()))) {
			Race newRace2 = raceController.getByUuid(input.getRace2().getUUID());
			if (newRace2 == null) {
				LOG.log(Level.INFO, "UUID of race2 '{0}' does not match any existing race", input.getRace2().getUUID());
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("UUID of race2 does not match any existing race").build();
			}
			existingBoard.setTeam2Race(newRace2);
			dirty = true;
		}

		if (input.getColorRace1() != null && !input.getColorRace1().equals(existingBoard.getColorRace1())) {
			existingBoard.setColorRace1(input.getColorRace1());
			dirty = true;
		}

		if (input.getColorRace2() != null && !input.getColorRace2().equals(existingBoard.getColorRace2())) {
			existingBoard.setColorRace2(input.getColorRace2());
			dirty = true;
		}

		List<BoardPlacement> placementsToBeRemoved = existingBoard.getPlacements().stream()
				.filter(p -> (p.getTeam() == Team.TEAM1 && p.getPosition().getRace() != existingBoard.getTeam1Race())
						|| (p.getTeam() == Team.TEAM2 && p.getPosition().getRace() != existingBoard.getTeam2Race()))
				.collect(Collectors.toList());

		placementsToBeRemoved.forEach(p -> existingBoard.removePlacement(p));

		if (dirty) {
			boardAccManager.persist(existingBoard);
		}

		Response response = Response.ok().build();
		return response;
	}
}
