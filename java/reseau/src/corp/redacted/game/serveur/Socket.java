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

public class Socket extends WebSocketServer {
	public final static int PORT = 8081;
	public final static Boolean DEBUG = false;

	// Stockage des informations globales de déplacement et de tir
	public static int numberLeft = 0;
	public static int numberRight = 0;
	public static int numberLeftShot = 0;
	public static int numberRightShot = 0;

	public Socket() throws UnknownHostException{
		// On Instancie la WebSocket sur le port PORT
		super(new InetSocketAddress(PORT));
		// Pour éviter le problème d'addresse déjà prise si nouveau lancement
		this.setReuseAddr(true);
	}

  @Override
  public void onOpen(WebSocket session, ClientHandshake hs){
		// Un utilisateur vient de se connecter à la session
    session.send("Connexion réussie");
		if(DEBUG){
	    System.out.println(
				"Nouveau joueur" +
	    	session.getRemoteSocketAddress().getAddress().getHostAddress()
			);
		}
  }

  @Override
  public void onClose(WebSocket session, int code, String cause, boolean r){
		// Fermeture de la connexion d'un joueur avec la WebSocket
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
