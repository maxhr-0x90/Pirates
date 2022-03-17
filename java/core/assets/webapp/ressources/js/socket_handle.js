var IP = "161.3.34.43";
var webSocket = new WebSocket("ws://" + IP + ":8081");;

webSocket.onopen = function(event) {
  updateOutput("Connexion etablie");
};

webSocket.onmessage = function(event) {
  updateOutput(event.data);
};

webSocket.onclose = function(event) {
  updateOutput("Connexion terminee");
};

function left() {
  webSocket.send("0");
}

function right() {
  webSocket.send("1");
}

function right() {
  webSocket.send("1");
}

function leftShot() {
  webSocket.send("2");
}

function rightShot() {
  webSocket.send("3");
}

function closeSocket() {
  webSocket.close();
}

function updateOutput(text) {
  sens.innerHTML = text;
}
