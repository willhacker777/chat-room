package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

@WebSocket
public final class MyWebSocketHandler {
    private final static SessionManager sessionManager;
    private final static MessageHandler messageHandler;
    static {
        sessionManager = new SessionManager();
        messageHandler = new MessageHandler();
    }
    @OnWebSocketConnect
    public void onConnect(Session session) {
        if (sessionManager.isValidSession(session)) {
            try {
                System.out.println("MyWebSocketHandler::onConnect: session exists");
                session.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return;
        }
        try {
            sessionManager.add(session);
            JSONObject json = new JSONObject();
            json.put("cmd", "welcome");
            session.getRemote().sendString(json.toString());
            System.out.println("MyWebSocketHandler::onConnect: session added");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        if (!sessionManager.isValidSession(session)) {
            System.out.println("MyWebSocketHandler::onMessage: invalid session");
            session.close();
            return;
        }
        try {
            System.out.println("MyWebSocketHandler::onMessage: Message = " + message);

            //handle message
            JSONObject json = new JSONObject(message);
            messageHandler.handle(session, json);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("MyWebSockethandler::onClose: statusCode=" + statusCode + ", reason=" + reason);
        if (!sessionManager.isValidSession(session)) {
            System.out.println("MyWebSockethandler::onClose: invalid session");
            return;
        }
        try {
            //handle logout message
            JSONObject json = new JSONObject();
            json.put("cmd", "logOutInternal");
            messageHandler.handle(session, json);

            sessionManager.remove(session);
            System.out.println("MyWebSockethandler::onClose: session closed");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @OnWebSocketError
    public void onError(Session session, Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }
}
