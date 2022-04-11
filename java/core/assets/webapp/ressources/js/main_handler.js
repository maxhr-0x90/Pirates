var IP = "localhost";
var webSocket = new WebSocket("ws://" + IP + ":8889");;
var x, y;

webSocket.onopen = function(event){
  webSocket.send("hub");
};

webSocket.onmessage = function(event) {
  if(event.data === "redirect"){
    document.location.href = "erreur.html";
  }
  else if(event.data === "start"){
    document.location.href = "manette.html";
  }
  else{
    updateOutput(event.data);
  }
};

webSocket.onclose = function(event) {
  updateOutput("Connexion terminee");
};

function repaint(){
  let canvas = document.getElementById('canvas');
  if(canvas.getContext){
    let ctx = canvas.getContext('2d');
    ctx.fillRect(0, 0, 600, 400);
    ctx.font = '16px sans-serif';
    ctx.fillStyle = "White";
    ctx.fillText('ECRAN DU CINEMA', 110, 20);
    ctx.fillText('G', 10, 110);
    ctx.fillText('A', 10, 126);
    ctx.fillText('U', 10, 142);
    ctx.fillText('C', 10, 158);
    ctx.fillText('H', 10, 174);
    ctx.fillText('E', 10, 190);
    ctx.fillText('D', 335, 110);
    ctx.fillText('R', 335, 126);
    ctx.fillText('O', 335, 142);
    ctx.fillText('I', 335, 158);
    ctx.fillText('T', 335, 174);
    ctx.fillText('E', 335, 190);
    ctx.fillStyle = "Black";
  }
}

function init_canvas(){
  let canvas = document.getElementById('canvas');
  repaint();

  canvas.addEventListener("mousedown", function(event){
    let bounding = event.target.getBoundingClientRect();
    x = event.clientX - bounding.left;
    y = event.clientY - bounding.top;
    console.log(x,y);

    if(canvas.getContext){
      let ctx = canvas.getContext('2d');
      repaint();
      ctx.fillStyle = "Red";
      ctx.fillRect(x, y, 10, 10);
      ctx.fillStyle = "Black";
      updateOutput("clic : " + x);
      sendPosition(x);
    }
  });
}

function updateOutput(text) {
  sens.innerHTML = text;
}

function sendPosition(x){
  webSocket.send("position:"+parseInt(x, 10));
}
