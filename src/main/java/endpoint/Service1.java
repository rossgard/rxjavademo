package endpoint;

import domain.Request;
import domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service1 {

    private static final Logger LOG = LoggerFactory.getLogger(Service1.class);

    public Response call(Request request) {
        LOG.debug("jersey.WrapperService 1 called with request {}", request);
        try {
            Thread.sleep(100);
            return new Response("result from service 1");
        } catch (InterruptedException e) {
            throw new RuntimeException("Error occurred in service 1");
        }
    }

    public Response throwExceptionOnRequest(Request request) {
        LOG.debug("jersey.WrapperService 1 called with request {}", request);
        try {
            Thread.sleep(100);
            throw new RuntimeException("An error occurred in service 1");
        } catch (InterruptedException e) {
            throw new RuntimeException("Error occurred in service 1");
        }
    }
}
