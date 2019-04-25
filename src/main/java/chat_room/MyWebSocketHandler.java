package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

@WebSocket
public class MyWebSocketHandler {
    private static int nextId = 0;
    private static Map<Session, Integer> sessionToId = new HashMap<Session, Integer>();
    private static Set<Integer> occupiedId = new HashSet<Integer>();
    @OnWebSocketConnect
    public void onConnect(Session session) {
        if (MyWebSocketHandler.sessionToId.containsKey(session)) {
            System.out.println("Connect: session id exists");
            session.close();
        } else {
            while (MyWebSocketHandler.occupiedId.contains(MyWebSocketHandler.nextId)) {
                MyWebSocketHandler.nextId++;
            }
            MyWebSocketHandler.sessionToId.put(session, MyWebSocketHandler.nextId);
            occupiedId.add(MyWebSocketHandler.nextId);
            Integer id = MyWebSocketHandler.sessionToId.get(session);
            System.out.println("Connect: session id = " + id);
        }
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        if (MyWebSocketHandler.sessionToId.containsKey(session)) {
            Integer id = MyWebSocketHandler.sessionToId.get(session);
            System.out.println("Message: session id = " + id);
        System.out.println("Message: " + message);
        } else {
            System.out.println("Message: invalid session");
            session.close();
        }
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        if (MyWebSocketHandler.sessionToId.containsKey(session)) {
            Integer id = MyWebSocketHandler.sessionToId.get(session);
            System.out.println("Close: session id = " + id);
            sessionToId.remove(session);
            occupiedId.remove(id);
        } else {
            System.out.println("Close: invalid session");
        }
    }
    @OnWebSocketError
    public void onError(Session session, Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }
}
