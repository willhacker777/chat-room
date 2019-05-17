function debug(str) {
    $("#debug").append("<li>" + str + "</li>");
}

function messageHandler(evt) {
    debug("onmessage: " + evt.data);
    var json = JSON.parse(evt.data);
    if (json.cmd == "welcome") {

        ws.send(JSON.stringify({"cmd": "checkLoginState"}));
        debug("check login state");

        var hb = setInterval(heartBeat, 3 * 1000);
        function heartBeat() {
            if (ws.readyState != ws.OPEN) {
                clearInterval(hb);
                return;
            }
            ws.send(JSON.stringify({"cmd": "heartbeat"}));
            debug("send heartbeat");
        }
    } else if (json.cmd == "checkLoginStateReturn") {
        if (json.loginState == false) {
            renderLoginPage();
        } else {
            renderChatPage();
        }
    } else if (json.cmd == "heartbeatReturn") {
        if (json.data != "ok") {
            debug("heartbeatReturn is not ok");
        }
    }
}

function renderLoginPage() {
    $("#main").text("login page");
}

function renderChatPage() {
    $("#main").text("chat page");
}

var wsRemoteEndpoint = "ws://" + location.host + "/ws/";
var ws = new WebSocket(wsRemoteEndpoint);

ws.onopen = function() {
    debug("onopen");
};

ws.onmessage = messageHandler;

ws.onclose = function() {
    debug("onclose");
};

ws.onerror = function(err) {
    debug("onerror: err.code = " + err.data);
};
