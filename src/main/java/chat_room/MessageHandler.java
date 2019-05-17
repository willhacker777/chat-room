package chat_room;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.json.JSONObject;

public final class MessageHandler {
    private final AccountManager accountManager;
    private final OnlineManager onlineManager;
    
    public MessageHandler() {
        accountManager = new AccountManager();
        try {
            accountManager.add("root", "123");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        onlineManager = new OnlineManager();
    }
    public void handle(Session session, JSONObject json) throws Exception {
        String cmd = json.getString("cmd");
        if (cmd.equals("checkLoginState")) {
            System.out.println("MessageHandler::handle: cmd = checkLoginState");
            JSONObject retJson = new JSONObject();
            retJson.put("cmd", "checkLoginStateReturn");
            retJson.put("loginState", onlineManager.isValidSession(session));
            session.getRemote().sendString(retJson.toString());
        } else if (cmd.equals("heartbeat")) {
            System.out.println("MessageHandler::handle: cmd = heartbeat");
            JSONObject retJson = new JSONObject();
            retJson.put("cmd", "heartbeatReturn");
            retJson.put("status", "success");
            session.getRemote().sendString(retJson.toString());
        } else if (cmd.equals("logIn")) {
            System.out.println("MessageHandler::handle: cmd = logIn");
            String userName = json.getString("userName");
            String password = json.getString("password");
            if (onlineManager.isValidSession(session)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logInReturn");
                retJson.put("status", "fail");
                retJson.put("message", "Session Already Logged In");
                session.getRemote().sendString(retJson.toString());
            } else if (!accountManager.isValidUserNameAndPassword(userName, password)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logInReturn");
                retJson.put("status", "fail");
                retJson.put("message", "Invalid User Name or Password");
                session.getRemote().sendString(retJson.toString());
            } else {
                onlineManager.add(session, userName);
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logInReturn");
                retJson.put("status", "success");
                session.getRemote().sendString(retJson.toString());
            }
        } else if (cmd.equals("logOut")) {
            System.out.println("MessageHandler::handle: cmd = logOut");
            if (!onlineManager.isValidSession(session)) {
                JSONObject retJson = new JSONObject();
                retJson.put("cmd", "logOutReturn");
                retJson.put("data", "Invalid Session");
                session.getRemote().sendString(retJson.toString());
            } else {
                onlineManager.remove(session);
            }
        } else if (cmd.equals("logOutInternal")) {
            System.out.println("MessageHandler::handle: cmd = logOutInternal");
            if (onlineManager.isValidSession(session)) {
                onlineManager.remove(session);
            }
        } else if (cmd.equals("sendMessage")) {
            System.out.println("MessageHandler::handle: cmd = sendMessage");
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
        }
    }
}
