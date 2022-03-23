package corp.redacted.game;

public interface IConfig{
  //Unité de mesure := mètre

  int HAUTEUR_CARTE = 250;
  int LARGEUR_CARTE = 250;

  int LARGEUR_BATEAU = 10;
  int LONGUEUR_BATEAU = 25;

  int DELAIS_TIR = 50; //ms

  int DENSITE_BATEAU = 10;
  int FRICTION_BATEAU = 10;

  int DEGAT_B_B = 10; //Nb de dégat infliger dans une collision bateau-bateau

  float DENSITE_MARCHANDISE = 100f;
  int FRICTION_MARCHANDISE = 1000;


}
