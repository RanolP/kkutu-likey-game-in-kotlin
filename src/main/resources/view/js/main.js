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
            break
        default:
            console.log(parsed)
            break
    }
}

function chatReceived(from, message, stamp) {
    let chatFrom = document.createElement("div");
    chatFrom.className = "chat_from";
    chatFrom.textContent = from;
    let chatMessage = document.createElement("div");
    chatMessage.className = "chat_message";
    chatMessage.textContent = message;
    let chatStamp = document.createElement("div");
    chatStamp.className = "chat_stamp";
    chatStamp.textContent = stamp;
    let chatItem = document.createElement("div");
    chatItem.className = "chat_item";
    chatItem.appendChild(chatFrom);
    chatItem.appendChild(chatMessage);
    chatItem.appendChild(chatStamp);

    let chat = document.getElementById("chat");
    chat.appendChild(chatItem);
    chat.scrollTop = chatItem.offsetTop;
}

function start() {
    connect();

    document.getElementById("chat_send").onclick = () => {
        let input = document.getElementById("chat_input");
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
    document.getElementById("chat_input").onkeydown = (e) => {
        if (e.keyCode === 13) {
            document.getElementById('chat_send').click()
        }
    };
}

function initLoop() {
    if (document.getElementById("chat_send")) {
        start();
    } else {
        setTimeout(initLoop, 300);
    }
}

initLoop();