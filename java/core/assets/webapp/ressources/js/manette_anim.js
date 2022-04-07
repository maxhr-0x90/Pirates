function load(){
  let buttons = document.querySelectorAll("button.image");
  for(i = 0; i < buttons.length; i++){
    if(false){
      if(i % 2 == 0){
        buttons[i].style.backgroundColor = "rgba(120, 0, 0, 0.9)";
      }
      else{
        buttons[i].style.backgroundColor = "rgba(0, 0, 120, 0.9)";
      }
    }
    else{
      buttons[i].style.backgroundColor = "rgba(120, 120, 120, 0.9)";
    }
  }
}
