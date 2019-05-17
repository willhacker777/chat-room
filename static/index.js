function debug(str) {
    //$("#debug").append("<div class=\"alert alert-info\">" + new Date().toLocaleString() + ": " + str + "</div>");
}

function messageHandler(evt) {
    debug("onmessage: " + evt.data);
    var json = JSON.parse(evt.data);
    if (json.cmd == "welcome") {

        ws.send(JSON.stringify({"cmd": "checkLoginState"}));
        debug("check login state");

        var hb = setInterval(heartBeat, 300 * 1000);
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
        if (json.status != "success") {
            debug("heartbeatReturn failed");
        }
    } else if (json.cmd == "logInReturn") {
        if (json.status != "success") {
            debug("logInReturn fail, message = " + json.message);
            $("#loginInfo").text(json.message);
            $("#loginInfo").show();
        } else {
            debug("logInReturn success");
            renderChatPage();
        }
    }
}

function renderLoginPage() {
    $("#loginContainer").show();
}

function renderChatPage() {
    $("#loginContainer").hide();
}

var ws = null;

$(document).ready(function() {
    var wsRemoteEndpoint = "ws://" + location.host + "/ws/";
    ws = new WebSocket(wsRemoteEndpoint);

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

    $("#loginButton").click(function() {
        if (ws.readyState != ws.OPEN) {
            $("#loginInfo").text("Websocket error, please try again.");
            $("#loginInfo").show();
            return false;
        }
        ws.send(JSON.stringify({
            "cmd": "logIn",
            "userName": $("#inputUserName").val(),
            "password": $("#inputPassword").val()
        }));
    });
});
