var ws_address;
var websocket;

ws_address = document.location.href;
ws_address = ws_address.replace("http://", "ws://");
ws_address = ws_address.replace(":8888/manette.html", ":8889");

webSocket = new WebSocket(ws_address);

webSocket.onopen = function(event){
  webSocket.send("manette");
};

webSocket.onmessage = function(event){
  if(event.data === "redirect"){
    document.location.href = "erreur.html";
  }
  else if(event.data === "rouge"){
    couleur_equipe(event.data);
  }
  else if(event.data === "bleu"){
    couleur_equipe(event.data);
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

function couleur_equipe(color){
  let buttons = document.querySelectorAll("button.image");
  for(i = 0; i < buttons.length; i++){
    if(color === "rouge"){
      buttons[i].style.backgroundColor = "rgba(120, 0, 0, 0.9)";
    }
    else if(color == "bleu"){
      buttons[i].style.backgroundColor = "rgba(0, 0, 120, 0.9)";
    }
    else{
      buttons[i].style.backgroundColor = "rgba(120, 120, 120, 0.9)";
    }
  }
}
