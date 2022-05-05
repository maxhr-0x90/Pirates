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
  webSocket.send("merch");
};

webSocket.onmessage = function(event){
  console.log(event.data);
  let splitted = event.data.split(":");
  let buttons = document.querySelectorAll("button.image");
  let i;

  if(event.data === "redirect"){  // Redirection sur la page d'erreur
    document.location.href = "erreur.html";
  }
  else if(event.data === "finjeu"){ // Redirection de fin de jeu vers le hub
    document.location.href = "index.html";
  }
  else{
    // Le joueur fait partie d'une équipe
    if(splitted[0] === "rouge" || splitted[0] === "bleu"){
      for(i = 0; i < 4; i++){
        if(splitted[1][i] === "0"){
          buttons[i].disabled = true;
        }
      }
      couleur_equipe(splitted[0]);
    }
    else if(splitted[0] === "points"){  // actualisation du nombre de points
      points.innerHTML = "Points : " + splitted[1];
    }
    else if(splitted[0] === "merch"){ // Changement ou demande de marchandise
      if(splitted[1] === "P"){
        merch.innerHTML = "Taille de la marchandise : petite";
      }
      else if(splitted[1] === "M"){
        merch.innerHTML = "Taille de la marchandise : moyenne";
      }
      else if(splitted[1] === "G"){
        merch.innerHTML = "Taille de la marchandise : grosse";
      }
      else {
        merch.innerHTML = "Taille de la marchandise : type inconnu";
      }
    }
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

// Fonction de différencition visuelle de la manette par rapport à son équipe
function couleur_equipe(color){
  let buttons = document.querySelectorAll("button.image");
  for(i = 0; i < buttons.length; i++){
    if(!buttons[i].disabled){
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
}
