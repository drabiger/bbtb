package net.raebiger.bbtb.api.providers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = Logger.getLogger("BBTB");

	@Override
	public Response toResponse(Throwable exception) {
		if (exception instanceof WebApplicationException) {
			return ((WebApplicationException) exception).getResponse();
		} else {
			LOG.log(Level.SEVERE, exception.getMessage(), exception);
			return Response.serverError().entity("").build();
		}
	}

}
