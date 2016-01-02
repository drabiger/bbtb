package net.raebiger.bbtb.servlet;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import net.raebiger.bbtb.api.filter.AuthorizationRequestFilter;
import net.raebiger.bbtb.api.providers.ConstraintViolationExceptionMapper;
import net.raebiger.bbtb.api.providers.GeneralExceptionMapper;
import net.raebiger.bbtb.api.services.RaceResource;
import net.raebiger.bbtb.logging.LogfileFormatter;

@ApplicationPath("/")
public class BBTBApplication extends ResourceConfig {
	private static final Logger LOG = Logger.getLogger("BBTB");

	public BBTBApplication() {
		configureLogging();
		LOG.info("Starting BBTB application.");

		// Resources.
		packages(RaceResource.class.getPackage().getName(), AuthorizationRequestFilter.class.getPackage().getName());

		register(LoggingFilter.class);
		register(ConstraintViolationExceptionMapper.class);
		register(GeneralExceptionMapper.class);
		register(AuthorizationRequestFilter.class);

		// allow annotations from package javax.annotation.security, see
		// http://www.nextinstruction.com/custom-jersey-security-filter.html
		register(RolesAllowedDynamicFeature.class);

	}

	private void configureLogging() {
		try {
			LOG.setUseParentHandlers(false);

			LogfileFormatter logfileFormatter = new LogfileFormatter();
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(logfileFormatter);
			consoleHandler.setLevel(Level.FINE);
			LOG.addHandler(consoleHandler);

			FileHandler fileHandler = new FileHandler("BBTB_log.%u.%g.txt", 0, 10, false);
			fileHandler.setFormatter(logfileFormatter);
			fileHandler.setLevel(Level.FINE);
			LOG.addHandler(fileHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
