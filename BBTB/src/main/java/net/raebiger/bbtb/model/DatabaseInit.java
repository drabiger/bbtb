package net.raebiger.bbtb.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class DatabaseInit {

	@Autowired
	RaceDao raceDao;

	@Autowired
	PositionDao positionDao;

	@Autowired
	BoardDao boardDao;

	@Autowired
	UserDao userDao;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public void initialize() {

		LOG.log(Level.INFO, "Creating Wood Elves");
		Race raceWoodElves = new Race();
		Position posWoodElvesLinemen = new Position();
		{
			raceWoodElves.setName("Wood Elves");
			raceDao.persist(raceWoodElves);

			posWoodElvesLinemen.setName("Linemen");
			posWoodElvesLinemen.setQuantity(16);
			posWoodElvesLinemen.setAbbreviation("Lm");
			raceWoodElves.addPosition(posWoodElvesLinemen);
			positionDao.persist(posWoodElvesLinemen);

			Position pos2 = new Position();
			pos2.setName("Treeman");
			pos2.setQuantity(1);
			pos2.setAbbreviation("Tm");
			raceWoodElves.addPosition(pos2);
			positionDao.persist(pos2);

			Position pos3 = new Position();
			pos3.setName("Wardancers");
			pos3.setQuantity(2);
			pos3.setAbbreviation("Wa");
			raceWoodElves.addPosition(pos3);
			positionDao.persist(pos3);

			Position pos4 = new Position();
			pos4.setName("Catchers");
			pos4.setQuantity(4);
			pos4.setAbbreviation("Ca");
			raceWoodElves.addPosition(pos4);
			positionDao.persist(pos4);

			Position pos5 = new Position();
			pos5.setName("Throwers");
			pos5.setQuantity(2);
			pos5.setAbbreviation("Th");
			raceWoodElves.addPosition(pos5);
			positionDao.persist(pos5);
		}

		LOG.log(Level.INFO, "Creating Humans");
		Race raceHumans = new Race();
		{
			raceHumans.setName("Humans");
			raceDao.persist(raceHumans);

			Position pos1 = new Position();
			pos1.setName("Linemen");
			pos1.setQuantity(16);
			pos1.setAbbreviation("Lm");
			raceHumans.addPosition(pos1);
			positionDao.persist(pos1);

			Position pos2 = new Position();
			pos2.setName("Blitzers");
			pos2.setQuantity(4);
			pos2.setAbbreviation("Bl");
			raceHumans.addPosition(pos2);
			positionDao.persist(pos2);

			Position pos3 = new Position();
			pos3.setName("Ogre");
			pos3.setQuantity(1);
			pos3.setAbbreviation("Og");
			raceHumans.addPosition(pos3);
			positionDao.persist(pos3);

			Position pos4 = new Position();
			pos4.setName("Catchers");
			pos4.setQuantity(4);
			pos4.setAbbreviation("Ca");
			raceHumans.addPosition(pos4);
			positionDao.persist(pos4);

			Position pos5 = new Position();
			pos5.setName("Throwers");
			pos5.setQuantity(4);
			pos5.setAbbreviation("Th");
			raceHumans.addPosition(pos5);
			positionDao.persist(pos5);

		}

		LOG.log(Level.INFO, "Creating Dark Elves");
		Race raceDarkElves = new Race();
		{
			raceDarkElves.setName("Dark Elves");
			raceDao.persist(raceDarkElves);

			Position pos1 = new Position();
			pos1.setName("Linemen");
			pos1.setQuantity(16);
			pos1.setAbbreviation("Lm");
			raceDarkElves.addPosition(pos1);
			positionDao.persist(pos1);

			Position pos2 = new Position();
			pos2.setName("Assassins");
			pos2.setQuantity(2);
			pos2.setAbbreviation("As");
			raceDarkElves.addPosition(pos2);
			positionDao.persist(pos2);

			Position pos3 = new Position();
			pos3.setName("Witch Elves");
			pos3.setQuantity(2);
			pos3.setAbbreviation("Wi");
			raceDarkElves.addPosition(pos3);
			positionDao.persist(pos3);

			Position pos4 = new Position();
			pos4.setName("Runners");
			pos4.setQuantity(2);
			pos4.setAbbreviation("Ru");
			raceDarkElves.addPosition(pos4);
			positionDao.persist(pos4);

			Position pos5 = new Position();
			pos5.setName("Blitzers");
			pos5.setQuantity(4);
			pos5.setAbbreviation("Bl");
			raceDarkElves.addPosition(pos5);
			positionDao.persist(pos5);
		}

		{
			User userFlash = new User();
			userFlash.setEmail("drabiger@googlemail.com");
			userFlash.setDisplayName("Flash");
			userDao.persist(userFlash);

			Board board1 = new Board();
			board1.setCreator(userFlash);
			board1.setName("Aggressive Wood Elf Kickoff");
			board1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
					+ "Aliquam quis sollicitudin ante. Etiam sed accumsan nunc, ultricies efficitur tortor. "
					+ "In hac habitasse platea dictumst. Nam tempus odio a nulla ultricies dictum.");
			board1.setRace1(raceWoodElves);
			board1.setRace2(raceHumans);
			board1.addPlacement(5, 6, posWoodElvesLinemen);
			board1.addPlacement(6, 12, posWoodElvesLinemen);
			board1.addPlacement(6, 13, posWoodElvesLinemen);
			board1.setColorRace1("#A00000");
			board1.setColorRace2("#0099FF");
			boardDao.persist(board1);

		}
	}
}
