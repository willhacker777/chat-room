package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SessionManager {
    public static SessionManager singleInstance = new SessionManager();

    private int nextId;
    private Map<Session, Integer> sessionToId;
    private Set<Integer> occupiedId;
    private ReadWriteLock readWriteLock;
    
    private SessionManager() {
        nextId = 0;
        sessionToId = new HashMap<Session, Integer>();
        occupiedId = new HashSet<Integer>();
        readWriteLock = new ReentrantReadWriteLock();
    }
    public boolean isValidSession(Session session) {
        boolean ret = false;
        readWriteLock.readLock().lock();
        ret = sessionToId.containsKey(session);
        readWriteLock.readLock().unlock();
        return ret;
    }
    public int getSessionId(Session session) throws Exception {
        int id = 0;
        readWriteLock.readLock().lock();
        if (!sessionToId.containsKey(session)) {
            readWriteLock.readLock().unlock();
            throw new Exception("getSessionId: invalid session");
        }
        id = sessionToId.get(session);
        readWriteLock.readLock().unlock();
        return id;
    }
    public void addSession(Session session) throws Exception {
        readWriteLock.writeLock().lock();
        if (sessionToId.containsKey(session)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("addSession: session exists");
        }
        while (occupiedId.contains(nextId)) {
            nextId++;
        }
        sessionToId.put(session, nextId);
        occupiedId.add(nextId);
        nextId++;
        readWriteLock.writeLock().unlock();
    }
    public void removeSession(Session session) throws Exception {
        readWriteLock.writeLock().lock();
        if (!sessionToId.containsKey(session)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("removeSession: invalid session");
        }
        int id = sessionToId.get(session);
        sessionToId.remove(session);
        occupiedId.remove(id);
        readWriteLock.writeLock().unlock();
    }
}
