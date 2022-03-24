package corp.redacted.game.serveur;

//Classe Timer

//BIBLIOTHEQUES
import java.util.Timer;


//CLASSE
public class Metronome {
  public static Timer timer = new Timer();

  // Fonction qui lance le systeme de timer le parametre frequence represente la frequence de lancement du timer en ms
  public static void launchTimer(int frequence){
    timer.scheduleAtFixedRate(new Task(), 0, frequence);
  }
}
