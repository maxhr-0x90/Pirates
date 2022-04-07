package corp.redacted.game.serveur;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Collections;
import java.util.Hashtable;

public class Socket extends WebSocketServer {
	public final static int PORT = 8889;
	public final static Boolean DEBUG = true;

	// Stockage des informations globales de déplacement et de tir
	public static int numberLeft = 0;
	public static int numberRight = 0;
	public static int numberLeftShot = 0;
	public static int numberRightShot = 0;

	public static Boolean whiteListed = false;

	/* HASHTABLES UTILISEE AVANT LA SEPARATION*/
	// White list avant séparation
	public static Hashtable<String, WebSocket> whiteListIn;
	// Position dans le cinéma des joueurs avant la séparation
	public static Hashtable<String, Integer> positionWhiteList;

	/* HASHTABLES UTILISEE APRES LA SEPARATION*/
	// Liste bateau bleu
	public static Hashtable<String, WebSocket> whiteListInBleue;
	// Liste bateau rouge
	public static Hashtable<String, WebSocket> whiteListInRouge;

	// Sert uniquement à enlever des personnes de la white list quand la connexion
	// est ouverte
	public static Hashtable<WebSocket, String> whiteListOut;

	public Socket() throws UnknownHostException{
		// On Instancie la WebSocket sur le port PORT
		super(new InetSocketAddress(PORT));

		// Pour éviter le problème d'addresse déjà prise si nouveau lancement
		this.setReuseAddr(true);

		whiteListIn = new Hashtable<String, WebSocket>();
		whiteListInRouge = new Hashtable<String, WebSocket>();
		whiteListInBleue = new Hashtable<String, WebSocket>();
		positionWhiteList = new Hashtable<String, Integer>();
		whiteListOut = new Hashtable<WebSocket, String>();
	}

  @Override
  public void onOpen(WebSocket session, ClientHandshake hs){
		// Un utilisateur vient de se connecter à la session
		WebSocket ws;
		String ip = session.getRemoteSocketAddress().getAddress().getHostAddress();

		if(!whiteListed){	// Connexion ouverte à tous
			whiteListIn.put(ip, session);
			whiteListOut.put(session, ip);
			if(DEBUG){
				System.out.println(whiteListIn);
				System.out.println(whiteListOut);
				System.out.println(positionWhiteList);
			}
		}
		else{	// Connexion fermée
			ws = whiteListIn.get(ip);
			if(ws == null){	// Personne non présente dans la white list
				session.send("redirect");
			}
			else{
				whiteListIn.put(ip, session);
			}
		}

		if(DEBUG){
			System.out.println(
				"Nouveau joueur : IP " +
				session.getRemoteSocketAddress().getAddress().getHostAddress()
				+ " " + session
			);
		}
  }

  @Override
  public void onClose(WebSocket session, int code, String cause, boolean r){
		// Fermeture de la connexion d'un joueur avec la WebSocket

		String ip = whiteListOut.get(session);
		if(!whiteListed){
			whiteListOut.remove(session);
			if(whiteListIn.get(ip) != null){
				whiteListIn.remove(ip);
			}
			if(positionWhiteList.get(ip) != null){
				positionWhiteList.remove(ip);
			}
		}

		if(DEBUG){
			System.out.println(session + " quitte le jeu à cause de : " + cause);
		}
  }

  @Override
  public void onMessage(WebSocket session, String message){
		if(message.equals("gauche")){	// L'utilisateur rame à gauche
			session.send("Gauche !");
			numberLeft++;
		}
		else if(message.equals("droite")){	// L'utilisateur rame à droite
			session.send("Droite !");
			numberRight++;
		}
		else if(message.equals("tgauche")){	// L'utilisateur tire à gauche
			session.send("Tir gauche !");
			numberLeftShot++;
		}
		else if(message.equals("tdroit")){	// L'utilisateur tire à droite
			session.send("Tir droit !");
			numberRightShot++;
		}
		else if(message.equals("manette")){	// L'utilisateur provient de la manette
			if(!whiteListed && !DEBUG){
				session.send("redirect");
			}
		}
		else if(message.equals("hub")){	// L'utilisateur provient de l'accueil
			if(whiteListed){
				session.send("redirect");
			}
		}
		else{ // Autre message
			try{
				// Si c'est une position on la concerve
				if(message.split(":")[0].equals("position")){
					positionWhiteList.put(
						session.getRemoteSocketAddress().getAddress().getHostAddress(),
						Integer.parseInt(message.split(":")[1])
					);
					System.out.println(positionWhiteList);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		if(DEBUG){
			System.out.println("Nouveau message de " + session + " : " + message);
			System.out.println("gauche : " + numberLeft);
			System.out.println("droite : " + numberRight);
		}
  }

  @Override
  public void onError(WebSocket session, Exception e){
    e.printStackTrace();
  }

	@Override
  public void onStart(){
  }

	/**
		* Fonction de séparation des deux équipes
	*/
	private static void separation(){
		// Séparation
	}
}
