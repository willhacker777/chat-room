package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class OnlineManager {
    private final Map<Session, String> sessionToUserName;
    private final Map<String, Session> userNameToSession;
    private final ReadWriteLock readWriteLock;
    
    public OnlineManager() {
        sessionToUserName = new HashMap<Session, String>();
        userNameToSession = new HashMap<String, Session>();
        readWriteLock = new ReentrantReadWriteLock();
    }
    public boolean isValidSession(Session session) {
        boolean ret = false;
        readWriteLock.readLock().lock();
        if (sessionToUserName.containsKey(session)) {
            ret = true;
        }
        readWriteLock.readLock().unlock();
        return ret;
    }
    public boolean isValidUserName(String userName) {
        boolean ret = false;
        readWriteLock.readLock().lock();
        if (userNameToSession.containsKey(userName)) {
            ret = true;
        }
        readWriteLock.readLock().unlock();
        return ret;
    }
    public String getUserNameBySession(Session session) throws Exception {
        String ret = "";
        readWriteLock.readLock().lock();
        if (!sessionToUserName.containsKey(session)) {
            readWriteLock.readLock().unlock();
            throw new Exception("OnlineManager::getUserNameBySession: invalid session");
        }
        ret = sessionToUserName.get(session);
        readWriteLock.readLock().unlock();
        return ret;
    }
    public Session getSessionByUserName(String userName) throws Exception {
        Session ret = null;
        readWriteLock.readLock().lock();
        if (!userNameToSession.containsKey(userName)) {
            readWriteLock.readLock().unlock();
            throw new Exception("OnlineManager::getSessionByUserName: invalid userName");
        }
        ret = userNameToSession.get(userName);
        readWriteLock.readLock().unlock();
        return ret;
    }
    public void add(Session session, String userName) throws Exception {
        if (session == null || userName == null) {
            throw new Exception("OnlineManager::add: session or userName is null");
        }
        readWriteLock.writeLock().lock();
        if (sessionToUserName.containsKey(session)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("OnlineManager::add: session exists");
        }
        if (userNameToSession.containsKey(userName)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("OnlineManager::add: userName exists");
        }
        sessionToUserName.put(session, userName);
        userNameToSession.put(userName, session);
        readWriteLock.writeLock().unlock();
    }
    public void remove(Session session) throws Exception {
        if (session == null) {
            throw new Exception("OnlineManager::remove: session is null");
        }
        readWriteLock.writeLock().lock();
        if (!sessionToUserName.containsKey(session)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("OnlineManager::remove: invalid session");
        }
        String userName = sessionToUserName.get(session);
        sessionToUserName.remove(session);
        userNameToSession.remove(userName);
        readWriteLock.writeLock().unlock();
    }
    public void broadcast(JSONObject json) throws Exception {
        readWriteLock.readLock().lock();
        for (Session session : sessionToUserName.keySet()) {
            session.getRemote().sendString(json.toString());
        }
        readWriteLock.readLock().unlock();
    }
}
