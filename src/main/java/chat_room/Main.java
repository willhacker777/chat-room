package chat_room;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);
        ResourceHandler resourceHandler= new ResourceHandler();
        resourceHandler.setResourceBase("static/index.html");
        ContextHandler contextHandler= new ContextHandler("/");
        contextHandler.setHandler(resourceHandler);
        server.setHandler(contextHandler);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
