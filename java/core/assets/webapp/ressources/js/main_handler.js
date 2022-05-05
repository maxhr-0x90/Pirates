var ws_address;
var websocket;
var x = null, y = null;

// Acquisition l'adresse de la websocket à partir de l'adresse courante
ws_address = document.location.href;
ws_address = ws_address.replace("http://", "ws://");
ws_address = ws_address.replace(":8888/index.html", ":8889");

// Connexion à la websocket
webSocket = new WebSocket(ws_address);

// Implémentation des méthodes de communication avec la websocket
webSocket.onopen = function(event){
  webSocket.send("hub");
};

webSocket.onmessage = function(event) {
  if(event.data === "redirect"){  // Redirection sur la page d'erreur
    document.location.href = "erreur.html";
  }
  else if(event.data === "finjeu"){ // Redirection de fin de jeu vers le hub
    document.location.href = "index.html";
  }
  else if(event.data === "start"){  // Redirection sur la manette
    document.location.href = "manette.html";
  }
};

// Fonction de changement de position sur le canvas
function repaint(){
  let canvas = document.getElementById('canvas');
  let cw = canvas.width;
  let ch = canvas.height;

  if(canvas.getContext){  // Affichage des informations primaires du canvas
    let ctx = canvas.getContext('2d');
    ctx.fillRect(0, 0, cw, ch);
    ctx.font = cw/20+'px sans-serif';
    ctx.fillStyle = "White";
    ctx.fillText('ECRAN DU CINEMA', 4*cw/14, ch/12);
    ctx.fillText('G', ch/26, 6*ch/16);
    ctx.fillText('A', ch/26, 6*ch/16+ch/18);
    ctx.fillText('U', ch/26, 6*ch/16+2*ch/18);
    ctx.fillText('C', ch/26, 6*ch/16+3*ch/18);
    ctx.fillText('H', ch/26, 6*ch/16+4*ch/18);
    ctx.fillText('E', ch/26, 6*ch/16+5*ch/18);
    ctx.fillText('D', cw-2*ch/26, 6*ch/16);
    ctx.fillText('R', cw-2*ch/26, 6*ch/16+ch/18);
    ctx.fillText('O', cw-2*ch/26, 6*ch/16+2*ch/18);
    ctx.fillText('I', cw-2*ch/26, 6*ch/16+3*ch/18);
    ctx.fillText('T', cw-2*ch/26, 6*ch/16+4*ch/18);
    ctx.fillText('E', cw-2*ch/26, 6*ch/16+5*ch/18);
    ctx.fillStyle = "Black";
  }
}

// Fonction d'initialisation du canvas
function init_canvas(){
  let canvas = document.getElementById('canvas');
  let vp = window.innerWidth;

  // On paramètre la taille du canvas
  canvas.width = vp-vp/14;
  canvas.height = vp-vp/4;

  repaint();

  // Évènement de clic sur le canvas
  canvas.addEventListener("mousedown", function(event){
    let bounding;

    // On récupère la position du clic sur le canvas
    bounding = event.target.getBoundingClientRect();
    x = event.clientX - bounding.left;
    y = event.clientY - bounding.top;

    if(canvas.getContext){  // On affiche la position sur le canvas
      let ctx = canvas.getContext('2d');
      repaint();
      ctx.fillStyle = "Red";
      ctx.fillRect(x-vp/80, y-vp/80, vp/40, vp/40);
      ctx.fillStyle = "Black";

      // On autorise l'utilisateur à pouvoir confirmer sa position
      document.querySelector("#confirmation").disabled = false;
      document.querySelector("#confirmation").innerHTML = "Confirmer";
    }
  });

  // Le bouton de confirmation est désactivé par défaut
  document.querySelector("#confirmation").disabled = true;
  document.querySelector("#confirmation").innerHTML = "---------";

  // Évènement de clic sur le bouton de confirmation
  document.querySelector("#confirmation").addEventListener("click", function(){
    let body = document.querySelector("body");
    let msgbox = document.createElement('div');
    let text = document.createElement('div');
    let bouton = document.createElement('button');
    let i;
    let child;

    // On envoie la position choisie à la websocket
    webSocket.send("position:"+parseInt(x, 10));

    // On enlève le canvas et le bouton de confirmation
    while(document.querySelector("body").firstChild != null){
      child = document.querySelector("body").firstChild
      document.querySelector("body").removeChild(child);
    }

    // Ajout du message indiquant à l'utilisateur que sa position est confirmée
    msgbox.id = "msgbox";
    text.id = "text";
    bouton.id = "refresh";
    bouton.classList.add("bouton");

    msgbox.innerHTML = "<div id=\"opacity\"></div>";
    text.innerHTML = "<b>Position acquise</b><br/><br/>"
    text.innerHTML += "Veuillez attendre que la partie commence<br/><br/>";
    text.innerHTML += "Vous pouvez aussi rafraichir pour choisir une ";
    text.innerHTML += "nouvelle position<br/><br/>";
    bouton.innerHTML = "Nouvelle position";

    bouton.addEventListener("click", function(){
      document.location.href = "./index.html";
    })

    text.appendChild(bouton);
    msgbox.appendChild(text);
    body.appendChild(msgbox);
  })
}
