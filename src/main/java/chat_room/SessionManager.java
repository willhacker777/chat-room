package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class SessionManager {
    private final Set<Session> sessions;
    private final ReadWriteLock readWriteLock;
    
    public SessionManager() {
        sessions = new HashSet<Session>();
        readWriteLock = new ReentrantReadWriteLock();
    }
    public boolean isValidSession(Session session) {
        boolean ret = false;
        readWriteLock.readLock().lock();
        ret = sessions.contains(session);
        readWriteLock.readLock().unlock();
        return ret;
    }
    public void add(Session session) throws Exception {
        readWriteLock.writeLock().lock();
        if (sessions.contains(session)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("SessionManager::add: session exists");
        }
        sessions.add(session);
        readWriteLock.writeLock().unlock();
    }
    public void remove(Session session) throws Exception {
        readWriteLock.writeLock().lock();
        if (!sessions.contains(session)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("SessionManager::remove: invalid session");
        }
        sessions.remove(session);
        readWriteLock.writeLock().unlock();
    }
}
