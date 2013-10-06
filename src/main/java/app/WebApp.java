package app;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebApp {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setDescriptor("../rxjavademo/src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("../rxjavademo/src/main/webapp");

        System.out.println("*** Descriptor: " + context.getDescriptor());
        System.out.println("*** ResourceBase: " + context.getResourceBase());
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);

        server.start();
        server.join();
    }
}
