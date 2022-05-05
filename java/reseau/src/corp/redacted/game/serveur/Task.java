package corp.redacted.game.serveur;

//Classe Task

//BIBLIOTHEQUES
import java.util.TimerTask;


//CLASSE
public class Task extends TimerTask{
    //VARIABLES
    public static int nbLeftR = 0;
    public static int nbRightR = 0;
    public static int nbShotLeftR = 0;
    public static int nbShotRightR = 0;
    public static int nbLeftB = 0;
    public static int nbRightB = 0;
    public static int nbShotLeftB = 0;
    public static int nbShotRightB = 0;
    public final static boolean DEBUG = false;

    //FONCTIONS
    //Fonction run qui va executer les taches du thread (recuperation des nombre de clic gauche et droite)
    public void run(){
        //Recuperation des nombres envoyer par la socket
        nbLeftR = Socket.numberLeftR;
        nbRightR = Socket.numberRightR;
        nbShotLeftR = Socket.numberLeftShotR;
        nbShotRightR = Socket.numberRightShotR;

        nbLeftB = Socket.numberLeftB;
        nbRightB = Socket.numberRightB;
        nbShotLeftB = Socket.numberLeftShotB;
        nbShotRightB = Socket.numberRightShotB;

        // DEBUG
        if(DEBUG){
            System.out.println("NbLeftR = " + nbLeftR + " NbRightR = " + nbRightR + " NbLeftShotR = " + nbShotLeftR + " NbRightShotR = " + nbShotRightR );
            System.out.println("NbLeftB = " + nbLeftB + " NbRightB = " + nbRightB + " NbLeftShotB = " + nbShotLeftB + " NbRightShotB = " + nbShotRightB );
        }

        //Remise a 0 des nombres du Switch avant prochaine utilisation
        Socket.numberLeftR = 0;
        Socket.numberRightR = 0;
        Socket.numberLeftShotR = 0;
        Socket.numberRightShotR = 0;
        Socket.numberLeftB = 0;
        Socket.numberRightB = 0;
        Socket.numberLeftShotB = 0;
        Socket.numberRightShotB = 0;
    }
}
