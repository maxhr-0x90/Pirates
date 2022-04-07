var IP = "192.168.0.2";
var webSocket = new WebSocket("ws://" + IP + ":8889");;

webSocket.onopen = function(event){
  webSocket.send("manette");
};

webSocket.onmessage = function(event){
  if(event.data === "redirect"){
    document.location.href = "erreur.html";
  }
  else{
    updateOutput(event.data);
  }
};

webSocket.onclose = function(event){
  updateOutput("Connexion terminee");
};

function connect(){
}

function left(){
  webSocket.send("gauche");
}

function right(){
  webSocket.send("droite");
}

function leftShot(){
  webSocket.send("tgauche");
}

function rightShot(){
  webSocket.send("tdroit");
}

function closeSocket(){
  webSocket.close();
}

function updateOutput(text) {
  sens.innerHTML = text;
}
