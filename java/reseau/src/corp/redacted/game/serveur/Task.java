package corp.redacted.game.serveur;

//Classe Task 

//BIBLIOTHEQUES
import java.util.TimerTask;


//CLASSE
public class Task extends TimerTask{
    //VARIABLES 
    public static int nbLeft = 0;
    public static int nbRight = 0;
    public static int nbShotLeft = 0;
    public static int nbShotRight = 0;

    //FONCTIONS
    //Fonction run qui va executer les taches du thread (recuperation des nombre de clic gauche et droite)
    public void run(){
        //Recuperation des nombres envoyer par la socket
        nbLeft = Socket.numberLeft;
        nbRight = Socket.numberRight;
        nbShotLeft = Socket.numberLeftShot;
        nbShotRight = Socket.numberRightShot;

        //DEBUG
        if(Socket.DEBUG){
            System.out.println("NbLeft = " + nbLeft + " NbRight = " + nbRight + " NbLeftShot = " + nbShotLeft + " NbRightShot = " + nbShotRight );
        }

        //Remise a 0 des nombres du Switch avant prochaine utilisation
        Socket.numberLeft = 0;
        Socket.numberRight = 0;
        Socket.numberLeftShot = 0;
        Socket.numberRightShot = 0;
    }
}