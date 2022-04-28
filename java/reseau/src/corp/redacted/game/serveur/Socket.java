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
	public final static Boolean DEBUG = false;

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

	// Booleen indiquant si la socket est whitelistée
	public static Boolean whiteListed = false;

	// Elements de la moitié gauche des équipes
	public static int rameGaucheG = 1;
	public static int rameDroiteG = 1;
	public static int tirGaucheG = 1;
	public static int tirDroiteG = 1;

	// Elements de la moitié droite des équipes
	public static int rameGaucheD = 1;
	public static int rameDroiteD = 1;
	public static int tirGaucheD = 1;
	public static int tirDroiteD = 1;

	/* HASHTABLES UTILISEE AVANT LA SEPARATION*/
	// White list avant séparation
	public static Hashtable<String, WebSocket> whiteListIn;
	// Position dans le cinéma des joueurs avant la séparation
	public static Hashtable<String, Integer> positionWhiteList;

	/* HASHTABLES UTILISEE APRES LA SEPARATION*/
	// Moitie gauche de la liste bateau bleu
	public static Hashtable<String, WebSocket> whiteListBleueG;
	// Moitie droite de la liste bateau bleu
	public static Hashtable<String, WebSocket> whiteListBleueD;
	// Moitie gauche de la liste bateau rouge
	public static Hashtable<String, WebSocket> whiteListRougeG;
	// Moitie droite de la liste bateau rouge
	public static Hashtable<String, WebSocket> whiteListRougeD;


	// Sert uniquement à enlever des personnes de la white list quand la connexion
	// est ouverte
	public static Hashtable<WebSocket, String> whiteListOut;

	public Socket() throws UnknownHostException{
		// On Instancie la WebSocket sur le port PORT
		super(new InetSocketAddress(PORT));

		// Pour éviter le problème d'addresse déjà prise si nouveau lancement
		this.setReuseAddr(true);

		whiteListIn = new Hashtable<String, WebSocket>();
		whiteListRougeG = new Hashtable<String, WebSocket>();
		whiteListRougeD = new Hashtable<String, WebSocket>();
		whiteListBleueG = new Hashtable<String, WebSocket>();
		whiteListBleueD = new Hashtable<String, WebSocket>();
		positionWhiteList = new Hashtable<String, Integer>();
		whiteListOut = new Hashtable<WebSocket, String>();
	}

  @Override
  public void onOpen(WebSocket session, ClientHandshake hs){
		// Un utilisateur vient de se connecter à la session
		WebSocket wsrg, wsrd, wsbg, wsbd;
		String ip = session.getRemoteSocketAddress().getAddress().getHostAddress();
		String messG = "" + rameGaucheG + rameDroiteG + tirGaucheG + tirDroiteG;
		String messD = "" + rameGaucheD + rameDroiteD + tirGaucheD + tirDroiteD;

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
			wsrg = whiteListRougeG.get(ip);
			wsrd = whiteListRougeD.get(ip);
			wsbg = whiteListBleueG.get(ip);
			wsbd = whiteListBleueD.get(ip);

			if(wsrg != null){	// Moitié gauche de l'équipe rouge
				whiteListRougeG.put(ip, session);
				session.send("rouge:" + messG);
			}
			else if(wsrd != null){	// Moitié droite de l'équipe rouge
				whiteListRougeD.put(ip, session);
				session.send("rouge:" + messD);
			}
			else if(wsbg != null){	// Moitié gauche de l'équipe bleue
				whiteListBleueG.put(ip, session);
				session.send("bleu:" + messG);
			}
			else if(wsbd != null){	// Moitié droite de l'équipe bleue
				whiteListBleueD.put(ip, session);
				session.send("bleu:" + messD);
			}
			else{	// Personne inconnue des whitelists
				session.send("redirect");
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

		try{
			if(!whiteListed){
				whiteListOut.remove(session);
				if(whiteListIn.get(ip) != null){
					whiteListIn.remove(ip);
				}
				if(positionWhiteList.get(ip) != null){
					positionWhiteList.remove(ip);
				}
			}
		}
		catch(Exception e){
		}

		if(DEBUG){
			System.out.println(session + " quitte le jeu à cause de : " + cause);
		}
  }

  @Override
  public void onMessage(WebSocket session, String message){
		String ip = session.getRemoteSocketAddress().getAddress().getHostAddress();
		WebSocket wsrg, wsrd, wsbg, wsbd;

		wsrg = whiteListRougeG.get(ip);
		wsrd = whiteListRougeD.get(ip);
		wsbd = whiteListBleueG.get(ip);
		wsbg = whiteListBleueD.get(ip);

		if(message.equals("gauche")){	// L'utilisateur rame à gauche
			session.send("Gauche !");
			if(wsrg != null || wsrd != null){
				numberLeftR++;
			}
			else if(wsbg != null || wsbd != null){
				numberLeftB++;
			}
		}
		else if(message.equals("droite")){	// L'utilisateur rame à droite
			session.send("Droite !");
			if(wsrg != null || wsrd != null){
				numberRightR++;
			}
			else if(wsbg != null || wsbd != null){
				numberRightB++;
			}
		}
		else if(message.equals("tgauche")){	// L'utilisateur tire à gauche
			session.send("Tir gauche !");
			if(wsrg != null || wsrd != null){
				numberLeftShotR++;
			}
			else if(wsbg != null || wsbd != null){
				numberLeftShotB++;
			}
		}
		else if(message.equals("tdroit")){	// L'utilisateur tire à droite
			session.send("Tir droit !");
			if(wsrg != null || wsrd != null){
				numberRightShotR++;
			}
			else if(wsbg != null || wsbd != null){
				numberRightShotB++;
			}
		}
		else if(message.equals("manette")){	// L'utilisateur provient de la manette
			if(!whiteListed){
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
				if(message.split(":")[0].equals("position") && !whiteListed){
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
				System.out.println(e.getMessage());
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
    int i, quart, moitie, troisQuarts;

		// On crée une Hashtable similaire avec les clés en valeur et réciproquement
    for (Map.Entry entry : (Set<Map.Entry<String, Integer>>)positionWhiteList.entrySet()){
      key = (int)entry.getValue();
      ip = (String)entry.getKey();
      reverseWL.put(key, ip);
    }

    // On met les positions dans une liste que l'on trie
    listePos = new ArrayList<Integer>(reverseWL.keySet());
    Collections.sort(listePos);

		quart = listePos.size()/4;	// Premier quart de la liste
    moitie = listePos.size()/2;	// Milieu de la liste
		troisQuarts = moitie+quart;	// Troisième quart de la liste

    for(i = 0; i < listePos.size(); i++){

      ip = reverseWL.get(listePos.get(i));	// On récupère l'IP
      if(ip != null){
        if(i < quart){	// Nouveau membre chez les rouges de gauche
					if(DEBUG){
	          System.out.println("RougeG : " + ip);
					}
          whiteListRougeG.put(ip, whiteListIn.get(ip));
        }
				else if(i <  moitie){	// Nouveau membre chez les rouges de droite
					if(DEBUG){
	          System.out.println("RougeG : " + ip);
					}
          whiteListRougeD.put(ip, whiteListIn.get(ip));
				}
				else if(i < troisQuarts){	// Nouveau membre chez les bleus de gauche
					if(DEBUG){
	          System.out.println("BleuG : " + ip);
					}
          whiteListBleueG.put(ip, whiteListIn.get(ip));
				}
        else{	// Nouveau membre chez les bleus de droite
					if(DEBUG){
	          System.out.println("BleuD : " + ip);
					}
          whiteListBleueD.put(ip, whiteListIn.get(ip));
        }
				whiteListIn.get(ip).send("start");
      }
    }
		if(DEBUG){
			System.out.println("RougesG : " + whiteListRougeG);
			System.out.println("RougesD : " + whiteListRougeD);
			System.out.println("BleusG : " + whiteListBleueG);
			System.out.println("BleusD : " + whiteListBleueD);
		}

		// Redirection des personnes n'ayant pas renseigné leur localisation
		for(Map.Entry entry : (Set<Map.Entry<String, WebSocket>>)whiteListIn.entrySet()){
      ip = (String)entry.getKey();
      if(whiteListRougeG.get(ip) == null
			&& whiteListRougeD.get(ip) == null
			&& whiteListBleueG.get(ip) == null
			&& whiteListBleueD.get(ip) == null){
				((WebSocket)entry.getValue()).send("redirect");
			}
    }

		// La partie est désormais whitelistée
		whiteListed = true;
	}

	/**
		* Permet d'envoyer à tous les utilisateurs pendant le jeu
		* @param message Message à envoyer
	*/
	public static void envoyerWhiteLists(String message){
		// Envoi à la moitié gauche des rouges
		for(Map.Entry entry : (Set<Map.Entry<String, WebSocket>>)whiteListRougeG.entrySet()){
      ((WebSocket)entry.getValue()).send(message);
    }

		// Envoi à la moitié droite des rouges
		for(Map.Entry entry : (Set<Map.Entry<String, WebSocket>>)whiteListRougeD.entrySet()){
      ((WebSocket)entry.getValue()).send(message);
    }

		// Envoi à la moitié gauche des bleus
		for(Map.Entry entry : (Set<Map.Entry<String, WebSocket>>)whiteListBleueG.entrySet()){
      ((WebSocket)entry.getValue()).send(message);
    }

		// Envoi à la moitié droite des bleus
		for(Map.Entry entry : (Set<Map.Entry<String, WebSocket>>)whiteListBleueD.entrySet()){
      ((WebSocket)entry.getValue()).send(message);
    }
	}

	/**
		* Fonction de reinitialisation de fin de jeu
	*/
  public static void reinit(){
		// On remet le booleen whiteListed a False
    whiteListed = false;

		// On redirige tout le monde sur le hub de nouveau
		envoyerWhiteLists("finjeu");

    // On nettoie les whitelist
    whiteListIn.clear();
    positionWhiteList.clear();
    whiteListBleueG.clear();
    whiteListBleueD.clear();
    whiteListRougeG.clear();
    whiteListRougeD.clear();
    whiteListOut.clear();
  }
}
