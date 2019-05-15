package chat_room;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class AccountManager {
    private final Map<String, String> userNameToPassword;
    private final ReadWriteLock readWriteLock;
    
    public AccountManager() {
        userNameToPassword = new HashMap<String, String>();
        readWriteLock = new ReentrantReadWriteLock();
    }
    public boolean isValidUserName(String userName) {
        boolean ret = false;
        readWriteLock.readLock().lock();
        if (userNameToPassword.containsKey(userName)) {
            ret = true;
        }
        readWriteLock.readLock().unlock();
        return ret;
    }
    public boolean isValidUserNameAndPassword(String userName, String password) {
        boolean ret = false;
        readWriteLock.readLock().lock();
        if (userNameToPassword.containsKey(userName)) {
            String realPassword = userNameToPassword.get(userName);
            if (realPassword.equals(password)) {
                ret = true;
            }
        }
        readWriteLock.readLock().unlock();
        return ret;
    }
    public void add(String userName, String password) throws Exception {
        if (userName == null || password == null) {
            throw new Exception("AccountManager::add: userName or password is null");
        }
        readWriteLock.writeLock().lock();
        if (userNameToPassword.containsKey(userName)) {
            readWriteLock.writeLock().unlock();
            throw new Exception("AccountManager::add: userName exists");
        }
        userNameToPassword.put(userName, password);
        readWriteLock.writeLock().unlock();
    }
}
