package japicmp.webapp;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/cmp")
public class CmpResource {

	private static final Logger LOGGER = Logger.getLogger(CmpResource.class);

	@GET
	@Produces("text/html")
	@Path("/by-maven-coords-html")
	public Response cmpByMavenCoordsHtml(@QueryParam("groupdIdOld") String groupdIdOld, @QueryParam("artifactIdOld") String artifactIdOld, @QueryParam("versionOld") String versionOld,
										 @QueryParam("groupIdNew") String groupIdNew, @QueryParam("artifactIdNew") String artifactIdNew, @QueryParam("versionNew") String versionNew) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Comparing: " + groupdIdOld + ":" + artifactIdOld + ":" + versionOld + " vs. " + groupIdNew + ":" + artifactIdNew + ":" + versionNew);
		}
		return Response.status(200).entity("OK").build();
	}
}
