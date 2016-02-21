package net.raebiger.bbtb.api.updaters;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import net.raebiger.bbtb.api.domain.PositionDomain;
import net.raebiger.bbtb.api.domain.RaceDomain;
import net.raebiger.bbtb.model.AccessController;
import net.raebiger.bbtb.model.Position;
import net.raebiger.bbtb.model.Race;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:WebContent/WEB-INF/applicationContext-test.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class RaceUpdaterTest {

	private RaceDomain inputDomain;

	@Resource
	private AccessController<Race> raceAccessController;

	private Race race;

	@Before
	public void setUp() throws Exception {

		race = new Race();
		race.setName("Dubu");
		raceAccessController.persist(race);

		inputDomain = new RaceDomain();
		inputDomain.setName("Dark Elves");

		// this makes sure we update race "Dubu" with new values
		inputDomain.setUUID(race.getUUID());

		PositionDomain inputPos1 = new PositionDomain();
		inputPos1.setName("Linemen");
		inputPos1.setQuantity(16);
		inputDomain.getPositions().add(inputPos1);
		PositionDomain inputPos2 = new PositionDomain();
		inputPos2.setName("Runners");
		inputPos2.setQuantity(4);
		inputDomain.getPositions().add(inputPos2);

	}

	@After
	public void tearDown() throws Exception {
		inputDomain = null;
		race = null;
	}

	@Test
	@Transactional
	public void testUpdate() {

		RaceUpdater updater = new RaceUpdater(inputDomain, raceAccessController);
		updater.update();

		assertEquals("Names must be equal", inputDomain.getName(), race.getName());
		assertEquals("Size of position list must be equal", inputDomain.getPositions().size(),
				race.getPositions().size());

		List<Position> positions = race.getPositions();
		for (PositionDomain posDomain : inputDomain.getPositions()) {
			boolean found = false;
			for (Position position : positions) {
				if (position.getName().equals(posDomain.getName())
						&& position.getQuantity() == posDomain.getQuantity()) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Could not find position " + posDomain.getName(), found);
		}
	}

}
