var IP = "192.168.0.29";
var webSocket = new WebSocket("ws://" + IP + ":8081");;

webSocket.onopen = function(event) {
  webSocket.send("iswhitelisted")
};

webSocket.onmessage = function(event) {
  if(event.data === "redirect"){
    document.location.href = "erreur.html";
  }
  else{
    updateOutput(event.data);
  }
};

webSocket.onclose = function(event) {
  updateOutput("Connexion terminee");
};

function connect(){
}

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
