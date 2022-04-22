var intervalle = 3; // Intervalle en seconde du cooldown

function remplissage_barre(barre){
  let remplissage = setInterval(frame, 30);
  let width = 0;
  function frame() {
    if(width >= 100){
      clearInterval(remplissage);
    }else{
      width++;
      barre.style.width = width + "%";
    }
  }
}

function drop(cote){
  // let canon = document.getElementById();

  let boutons = document.querySelectorAll("td");
  let canon = boutons[cote].children[0]
  let chargement = document.createElement('div');
  let progression = document.createElement('div');
  let barre = document.createElement('div');

  // Ajout des classes aux div de la barre de chargement
  chargement.classList.add("chargement");
  progression.classList.add("progression");

  if(cote === 2){
    chargement.id = "canon_gauche";
  }
  else if(cote === 3){
    chargement.id = "canon_droit";
  }

  chargement.classList.add("image");
  barre.classList.add("barre");

  // On enlève le bouton de la cellule du tableau
  boutons[cote].removeChild(canon);

  // On ajoute la barre de chargement à la cellule du tableau
  progression.appendChild(barre);
  chargement.appendChild(progression);
  chargement.style.backgroundColor = "rgba(0, 0, 0, 0.75)";
  boutons[cote].appendChild(chargement);

  // On fait progresser la barre de chargement
  let width = 0;
  let remplissage = setInterval(frame, intervalle*10);
  function frame() {
    if(width >= 100){
      clearInterval(remplissage);
      boutons[cote].removeChild(chargement);  // On enlève la barre
      boutons[cote].appendChild(canon);       // On remet le canon à la place
    }else{
      width++;
      barre.style.width = width + "%";        // On fait progresser la barre
    }
  }
}
