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

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Map;

public class Socket extends WebSocketServer {
	public final static int PORT = 8889;
	public final static Boolean DEBUG = true;

	// Stockage des informations globales de déplacement et de tir des rouges
	public static int numberLeftR = 0;
	public static int numberRightR = 0;
	public static int numberLeftShotR = 0;
	public static int numberRightShotR = 0;

	// Stockage des informations globales de déplacement et de tir des bleus
	public static int numberLeftB = 0;
	public static int numberRightB = 0;
	public static int numberLeftShotB = 0;
	public static int numberRightShotB = 0;

	public static Boolean whiteListed = false;

	/* HASHTABLES UTILISEE AVANT LA SEPARATION*/
	// White list avant séparation
	public static Hashtable<String, WebSocket> whiteListIn;
	// Position dans le cinéma des joueurs avant la séparation
	public static Hashtable<String, Integer> positionWhiteList;

	/* HASHTABLES UTILISEE APRES LA SEPARATION*/
	// Liste bateau bleu
	public static Hashtable<String, WebSocket> whiteListBleue;
	// Liste bateau rouge
	public static Hashtable<String, WebSocket> whiteListRouge;

	// Sert uniquement à enlever des personnes de la white list quand la connexion
	// est ouverte
	public static Hashtable<WebSocket, String> whiteListOut;

	public Socket() throws UnknownHostException{
		// On Instancie la WebSocket sur le port PORT
		super(new InetSocketAddress(PORT));

		// Pour éviter le problème d'addresse déjà prise si nouveau lancement
		this.setReuseAddr(true);

		whiteListIn = new Hashtable<String, WebSocket>();
		whiteListRouge = new Hashtable<String, WebSocket>();
		whiteListBleue = new Hashtable<String, WebSocket>();
		positionWhiteList = new Hashtable<String, Integer>();
		whiteListOut = new Hashtable<WebSocket, String>();
	}

  @Override
  public void onOpen(WebSocket session, ClientHandshake hs){
		// Un utilisateur vient de se connecter à la session
		WebSocket wsr, wsb;
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
			wsr = whiteListRouge.get(ip);
			wsb = whiteListBleue.get(ip);
			if(wsr == null && wsb == null){	// Personne non présente dans les white lists
				session.send("redirect");
			}
			else{
				if(wsr == null){	// Membre de l'équipe rouge
					whiteListBleue.put(ip, session);
					session.send("bleu");
				}
				else{	// Membre de l'équipe
					whiteListRouge.put(ip, session);
					session.send("rouge");
				}
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
		String ip = session.getRemoteSocketAddress().getAddress().getHostAddress();
		if(message.equals("gauche")){	// L'utilisateur rame à gauche
			session.send("Gauche !");
			if(whiteListRouge.get(ip) != null){
				numberLeftR++;
			}
			else if(whiteListBleue.get(ip) != null){
				numberLeftB++;
			}
		}
		else if(message.equals("droite")){	// L'utilisateur rame à droite
			session.send("Droite !");
			if(whiteListRouge.get(ip) != null){
				numberRightR++;
			}
			else if(whiteListBleue.get(ip) != null){
				numberRightB++;
			}
		}
		else if(message.equals("tgauche")){	// L'utilisateur tire à gauche
			session.send("Tir gauche !");
			if(whiteListRouge.get(ip) != null){
				numberLeftShotR++;
			}
			else if(whiteListBleue.get(ip) != null){
				numberLeftShotB++;
			}
		}
		else if(message.equals("tdroit")){	// L'utilisateur tire à droite
			session.send("Tir droit !");
			if(whiteListRouge.get(ip) != null){
				numberRightShotR++;
			}
			else if(whiteListBleue.get(ip) != null){
				numberRightShotB++;
			}
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
					if(DEBUG){
						System.out.println(positionWhiteList);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		if(DEBUG){
			System.out.println("Nouveau message de " + session + " : " + message);
			System.out.println("gaucheR : " + numberLeftR);
			System.out.println("droiteR : " + numberRightR);
			System.out.println("gaucheB : " + numberLeftB);
			System.out.println("droiteB : " + numberRightB);
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
	public static void separation(){
		Hashtable<Integer, String> reverseWL = new Hashtable<Integer, String>();
		ArrayList<Integer> listePos;
		Integer key;
		String ip;
    int i, middle;

		// On crée une Hashtable similaire avec les clés en valeur et réciproquement
    for (Map.Entry entry : (Set<Map.Entry<String, Integer>>)positionWhiteList.entrySet()){
      key = (int)entry.getValue();
      ip = (String)entry.getKey();
      reverseWL.put(key, ip);
    }

    // On met les positions dans une liste que l'on trie
    listePos = new ArrayList<Integer>(reverseWL.keySet());
    Collections.sort(listePos);

    middle = listePos.size()/2;	// Milieu de la liste

    for(i = 0; i < listePos.size(); i++){

      ip = reverseWL.get(listePos.get(i));	// On récupère l'IP
      if(ip != null){
        if(i < middle){	// Nouveau membre chez les rouges
					if(DEBUG){
	          System.out.println("Rouge : " + ip);
					}
          whiteListRouge.put(ip, whiteListIn.get(ip));
        }
        else{	// Nouveau membre chez les bleus
					if(DEBUG){
	          System.out.println("Bleu : " + ip);
					}
          whiteListBleue.put(ip, whiteListIn.get(ip));
        }
				whiteListIn.get(ip).send("start");
      }
    }
		if(DEBUG){
			System.out.println("Rouges : " + whiteListRouge);
			System.out.println("Bleus : " + whiteListBleue);
		}

		// Redirection des personnes n'ayant pas renseigné leur localisation
		for (Map.Entry entry : (Set<Map.Entry<String, WebSocket>>)whiteListIn.entrySet()){
      ip = (String)entry.getKey();
      if(whiteListRouge.get(ip) == null && whiteListBleue.get(ip) == null){
				((WebSocket)entry.getValue()).send("redirect");
			}
    }

		// La partie est désormais whitelistée
		whiteListed = true;
	}
}
