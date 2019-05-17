package chat_room;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

public final class Main {
    public static void main(String[] args) {

        ResourceHandler resourceHandler= new ResourceHandler();
        resourceHandler.setResourceBase("static/");
        ContextHandler staticContext = new ContextHandler("/");
        staticContext.setHandler(resourceHandler);
        
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.getPolicy().setIdleTimeout(600 * 1000);
                factory.register(MyWebSocketHandler.class);
            }
        };
        ContextHandler wsContext = new ContextHandler("/ws");
        wsContext.setHandler(wsHandler);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { staticContext, wsContext });

        Server server = new Server(8080);
        server.setHandler(contexts);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
