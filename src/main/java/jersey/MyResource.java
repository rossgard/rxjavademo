package jersey;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    private static final Logger LOG = LoggerFactory.getLogger(MyResource.class);

    private WrapperService wrapperService = new WrapperService();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapperService.asynchronousServiceCall());
        } catch (Exception e) {
            LOG.error("Unable to produce JSON {}", e.getMessage());
            return "{}";
        }
    }

    @GET
    @Path("/service1")
    @Produces(MediaType.APPLICATION_JSON)
    public String service1() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapperService.service1());
        } catch (Exception e) {
            LOG.error("Unable to produce JSON {}", e.getMessage());
            return "{}";
        }
    }

    @GET
    @Path("/service2")
    @Produces(MediaType.APPLICATION_JSON)
    public String service2() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapperService.service2());
        } catch (Exception e) {
            LOG.error("Unable to produce JSON {}", e.getMessage());
            return "{}";
        }
    }

    @GET
    @Path("/service3")
    @Produces(MediaType.APPLICATION_JSON)
    public String service3() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapperService.service3());
        } catch (Exception e) {
            LOG.error("Unable to produce JSON {}", e.getMessage());
            return "{}";
        }
    }
}

