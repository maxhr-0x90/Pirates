var IP = "192.168.0.29";
var webSocket = new WebSocket("ws://" + IP + ":8080/socket");;

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

function closeSocket() {
  webSocket.close();
}

function updateOutput(text) {
  sens.innerHTML = text;
}
