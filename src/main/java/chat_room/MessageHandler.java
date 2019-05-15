package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.json.JSONObject;

public final class MessageHandler {
    private final AccountManager accountManager;
    private final OnlineManager onlineManager;
    
    public MessageHandler() {
        accountManager = new AccountManager();
        onlineManager = new OnlineManager();
    }
    public void handle(Session session, JSONObject json) throws Exception {
        String cmd = json.getString("cmd");
        if (cmd.equals("logIn")) {
            String userName = json.getString("userName");
            String password = json.getString("password");
            if (onlineManager.isValidSession(session)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logInReturn");
                retJson.put("data", "Session Already Logged In");
                session.getRemote().sendString(retJson.toString());
            } else if (!accountManager.isValidUserNameAndPassword(userName, password)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logInReturn");
                retJson.put("data", "Invalid User Name or Password");
                session.getRemote().sendString(retJson.toString());
            } else {
                onlineManager.add(session, userName);
            }
        } else if (cmd.equals("logOut")) {
            if (!onlineManager.isValidSession(session)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logOutReturn");
                retJson.put("data", "Invalid Session");
                session.getRemote().sendString(retJson.toString());
            } else {
                onlineManager.remove(session);
            }
        } else if (cmd.equals("logOutInternal")) {
            if (onlineManager.isValidSession(session)) {
                onlineManager.remove(session);
            }
        } else if (cmd.equals("sendMessage")) {
            if (!onlineManager.isValidSession(session)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "sendMessageReturn");
                retJson.put("data", "Invalid Session");
                session.getRemote().sendString(retJson.toString());
            } else {
                String userName = onlineManager.getUserNameBySession(session);
                String message = json.getString("message");
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "sendMessage");
                retJson.put("userName", userName);
                retJson.put("message", message);
                onlineManager.broadcast(retJson);
            }
        } else if (cmd.equals("heartbeat")) {
            JSONObject retJson = new JSONObject();
            retJson.put("cmd", "heartbeatReturn");
            retJson.put("data", "ok");
            session.getRemote().sendString(retJson.toString());
        }
    }
}
