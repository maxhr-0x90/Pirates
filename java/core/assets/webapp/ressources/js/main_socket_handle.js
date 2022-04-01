var IP = "localhost";
var webSocket = new WebSocket("ws://" + IP + ":8081");;

webSocket.onopen = function(event) {
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
  webSocket.send("5");
}
