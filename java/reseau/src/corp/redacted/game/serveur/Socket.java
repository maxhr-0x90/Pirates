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
	public final static int PORT = 8081;
	public final static Boolean DEBUG = false;

	// Stockage des informations globales de déplacement et de tir
	public static int numberLeft = 0;
	public static int numberRight = 0;
	public static int numberLeftShot = 0;
	public static int numberRightShot = 0;

	public static Boolean whiteListed = false;

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

		whiteListInBleue = new Hashtable<String, WebSocket>();
		whiteListInRouge = new Hashtable<String, WebSocket>();
		whiteListOut = new Hashtable<WebSocket, String>();
	}

  @Override
  public void onOpen(WebSocket session, ClientHandshake hs){
		// Un utilisateur vient de se connecter à la session
		WebSocket wsb, wsr;
		String ip = session.getRemoteSocketAddress().getAddress().getHostAddress();

		if(!whiteListed){	// Connexion ouverte à tous
			if(ip.charAt(ip.length()-1) % 2 == 0){
				whiteListInBleue.put(ip, session);
			}
			else{
				whiteListInRouge.put(ip, session);
			}
			whiteListOut.put(session, ip);
			System.out.println("bleue : " + whiteListInBleue);
			System.out.println("rouge : " + whiteListInRouge);
			System.out.println(whiteListOut);
		}
		else{	// Connexion fermée
			wsb = whiteListInBleue.get(ip);
			wsr = whiteListInRouge.get(ip);
			if(wsb == null && wsr == null){	// Personne non présente dans la white list
				session.send("redirect");
			}
			else{
				if(wsb == null){
					whiteListInRouge.put(ip, session);
				}
				else{
					whiteListInBleue.put(ip, session);
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
			if(whiteListInBleue.get(ip) != null){
				whiteListInBleue.remove(ip);
			}
			else{
				whiteListInRouge.remove(ip);
			}
		}

		if(DEBUG){
			System.out.println(session + " quitte le jeu à cause de : " + cause);
		}
  }

  @Override
  public void onMessage(WebSocket session, String message){
		if(message.equals("0")){	// L'utilisateur rame à gauche
			session.send("Gauche !");
			numberLeft++;
		}
		if(message.equals("1")){	// L'utilisateur rame à droite
			session.send("Droite !");
			numberRight++;
		}
		if(message.equals("2")){	// L'utilisateur tire à gauche
			session.send("Tir gauche !");
			numberLeftShot++;
		}
		if(message.equals("3")){	// L'utilisateur tire à droite
			session.send("Tir droit !");
			numberRightShot++;
		}
		if(message.equals("iswhitelisted")){
			if(!whiteListed){
				session.send("redirect");
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
}
