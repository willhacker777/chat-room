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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebSocket
public class MyWebSocketHandler {
    private static int nextId = 0;
    private static Map<Session, Integer> sessionToId = new HashMap<Session, Integer>();
    private static Set<Integer> occupiedId = new HashSet<Integer>();
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    @OnWebSocketConnect
    public void onConnect(Session session) {
        MyWebSocketHandler.readWriteLock.readLock().lock();
        if (MyWebSocketHandler.sessionToId.containsKey(session)) {
            MyWebSocketHandler.readWriteLock.readLock().unlock();

            System.out.println("Connect: session id exists");
            session.close();
        } else {
            MyWebSocketHandler.readWriteLock.readLock().unlock();

            MyWebSocketHandler.readWriteLock.writeLock().lock();
            while (MyWebSocketHandler.occupiedId.contains(MyWebSocketHandler.nextId)) {
                MyWebSocketHandler.nextId++;
            }
            MyWebSocketHandler.sessionToId.put(session, MyWebSocketHandler.nextId);
            occupiedId.add(MyWebSocketHandler.nextId);
            Integer id = MyWebSocketHandler.sessionToId.get(session);
            MyWebSocketHandler.readWriteLock.writeLock().unlock();

            System.out.println("Connect: session id = " + id);
        }
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        MyWebSocketHandler.readWriteLock.readLock().lock();
        if (MyWebSocketHandler.sessionToId.containsKey(session)) {
            Integer id = MyWebSocketHandler.sessionToId.get(session);
            MyWebSocketHandler.readWriteLock.readLock().unlock();

            System.out.println("Message: session id = " + id);
            System.out.println("Message: " + message);
        } else {
            MyWebSocketHandler.readWriteLock.readLock().unlock();

            System.out.println("Message: invalid session");
            session.close();
        }
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);

        MyWebSocketHandler.readWriteLock.readLock().lock();
        if (MyWebSocketHandler.sessionToId.containsKey(session)) {
            Integer id = MyWebSocketHandler.sessionToId.get(session);
            MyWebSocketHandler.readWriteLock.readLock().unlock();

            System.out.println("Close: session id = " + id);

            MyWebSocketHandler.readWriteLock.writeLock().lock();
            sessionToId.remove(session);
            occupiedId.remove(id);
            MyWebSocketHandler.readWriteLock.writeLock().unlock();
        } else {
            MyWebSocketHandler.readWriteLock.readLock().unlock();

            System.out.println("Close: invalid session");
        }
    }
    @OnWebSocketError
    public void onError(Session session, Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }
}
