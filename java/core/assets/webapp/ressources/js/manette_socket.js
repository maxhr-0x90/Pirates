var ws_address;
var websocket;

// Acquisition l'adresse de la websocket à partir de l'adresse courante
ws_address = document.location.href;
ws_address = ws_address.replace("http://", "ws://");
ws_address = ws_address.replace(":8888/manette.html", ":8889");

// Connexion à la websocket
webSocket = new WebSocket(ws_address);

// Implémentation des méthodes de communication avec la websocket
webSocket.onopen = function(event){
  webSocket.send("manette");
};

webSocket.onmessage = function(event){
  if(event.data === "redirect"){  // Redirection sur la page d'erreur
    document.location.href = "erreur.html";
  }
  else if(event.data === "rouge"){  // Le joueur fait partie de l'équipe rouge
    couleur_equipe(event.data);
  }
  else if(event.data === "bleu"){ // Le joueur fait partie de l'équipe bleue
    couleur_equipe(event.data);
  }
  else{ // Sinon on affiche ce que la websocket à envoyé
    updateOutput(event.data);
  }
};

webSocket.onclose = function(event){
  updateOutput("Connexion terminee");
};

// Fonction permettant de ramer à gauche
function left(){
  webSocket.send("gauche");
}

// Fonction permettant de ramer à droite
function right(){
  webSocket.send("droite");
}

// Fonction permettant de tirer à gauche
function leftShot(){
  webSocket.send("tgauche");
}

// Fonction permettant de tirer à droite
function rightShot(){
  webSocket.send("tdroit");
}

// Fonction de mise à jour du contenu de la boite de message
function updateOutput(text) {
  document.querySelector("#msg").innerHTML = text;
}

// Fonction de différencition visuelle de la manette par rapport à son équipe
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
