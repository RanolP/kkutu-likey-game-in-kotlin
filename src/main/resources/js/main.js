var socket = null;

function connect() {
    console.log("Begin connect");
    socket = new WebSocket("ws://" + window.location.host + "/ws");

    socket.onerror = () => {
        console.log("socket error");
    };

    socket.onclose = () => {
        socket.send("Disconnected");
        setTimeout(connect, 5000);
    };

    socket.onmessage = (event) => {
        received(event.data.toString());
    };
}

function received(message) {
    let parsed = JSON.parse(message);
    let data = parsed["data"];
    switch (parsed["id"]) {
        case 0:
            chatReceived(data["from"]["display_name"], data["message"], data["time"]);
            break;
        case 2:
            playerReceived(data["player"], data["modify"], data["users"])
        default:
            console.log(parsed);
            break;
    }
}

function playerReceived(player, modify, users) {
    document.querySelector("#user-list-wrap .title span").textContent = "(" + users.toString() + "명)";
    const userList = document.getElementById("user-list");
    if (modify === "quit") {
        userList.removeChild(document.getElementById(player["id"]));
    } else {
        const icon = document.createElement("img");
        icon.src = player["icon"];
        icon.className = "user-list-icon";
        const display = document.createElement("div");
        display.textContent = player["display_name"];
        display.className = "user-list-name";
        const user = document.createElement("div");
        user.className = ".user-list-item";
        user.id = player["id"];
        user.appendChild(icon);
        user.appendChild(display);
        userList.appendChild(user);
    }
}

function chatReceived(from, message, stamp) {
    let chatFrom = document.createElement("div");
    chatFrom.className = "chat-from";
    chatFrom.textContent = from;
    let chatMessage = document.createElement("div");
    chatMessage.className = "chat-message";
    chatMessage.textContent = message;
    let chatStamp = document.createElement("div");
    chatStamp.className = "chat-stamp";
    chatStamp.textContent = stamp;
    let chatItem = document.createElement("div");
    chatItem.className = "chat-item";
    chatItem.appendChild(chatFrom);
    chatItem.appendChild(chatMessage);
    chatItem.appendChild(chatStamp);

    let chat = document.getElementById("chat");
    chat.appendChild(chatItem);
    chat.scrollTop = chatItem.offsetTop;
}

function start() {
    connect();

    document.getElementById("chat-send").onclick = () => {
        let input = document.getElementById("chat-input");
        if (input) {
            let text = input.value;
            if (text && socket) {
                socket.send(JSON.stringify({
                    "id": 1,
                    "data": {
                        "message": text
                    }
                }));
                input.value = "";
            }
        }
    };
    document.getElementById("chat-input").onkeydown = (e) => {
        if (e.keyCode === 13) {
            document.getElementById('chat-send').click()
        }
    };
}

function initLoop() {
    if (document.getElementById("chat-send")) {
        start();
    } else {
        setTimeout(initLoop, 300);
    }
}

initLoop();