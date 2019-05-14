package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class MyWebSocketHandler {
    private static SessionManager sessionManager = SessionManager.singleInstance;
    @OnWebSocketConnect
    public void onConnect(Session session) {
        if (MyWebSocketHandler.sessionManager.isValidSession(session)) {
            try {
                int id = MyWebSocketHandler.sessionManager.getSessionId(session);
                System.out.println("Connect: session exists, session id = " + id);
                session.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return;
        }
        try {
            MyWebSocketHandler.sessionManager.addSession(session);
            int id = MyWebSocketHandler.sessionManager.getSessionId(session);
            System.out.println("Connect: session id = " + id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        if (!MyWebSocketHandler.sessionManager.isValidSession(session)) {
            System.out.println("Message: invalid session");
            session.close();
            return;
        }
        try {
            int id = MyWebSocketHandler.sessionManager.getSessionId(session);
            System.out.println("Message: session id = " + id);
            System.out.println("Message: " + message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        if (!MyWebSocketHandler.sessionManager.isValidSession(session)) {
            System.out.println("Close: invalid session");
            return;
        }
        try {
            int id = MyWebSocketHandler.sessionManager.getSessionId(session);
            System.out.println("Close: session id = " + id);
            MyWebSocketHandler.sessionManager.removeSession(session);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @OnWebSocketError
    public void onError(Session session, Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }
}
