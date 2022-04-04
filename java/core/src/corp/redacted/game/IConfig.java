package corp.redacted.game;

public interface IConfig{
  //Unité de mesure := mètre

  /*-----Carte de jeu-----*/
  int HAUTEUR_CARTE = 250;
  int LARGEUR_CARTE = 250;

  /*-----Boulet de canon-----*/
  int TAILLE_CANNONBALL = 2;
  int DELAIS_TIR = 50; //ms
  float DENSITE_CANNONBALL = 100f;
  int FRICTION_CANNONBALL = 1000;

  /*-----Bateau-----*/
  int LARGEUR_BATEAU = 10;
  int LONGUEUR_BATEAU = 25;

  int DENSITE_BATEAU = 10;
  int MISE_A_NIVEAU = 1000000;
  int FRICTION_BATEAU = 1;
  float VITESSE = 9999f;

  /*-----Marchandise-----*/
  float DENSITE_MARCHANDISE = 2f;
  int FRICTION_MARCHANDISE = 20;

  /*-----Dégat-----*/
  int DEGAT_B_B = 5; //Nb de dégat infliger dans une collision bateau-bateau
  int DEGAT_CB_B = 20; //Nb de dégat ingliger lors de la réception d'un cannon

}
