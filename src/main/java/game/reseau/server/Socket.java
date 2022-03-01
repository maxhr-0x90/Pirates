package game.reseau.server;

import game.reseau.Switch;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;

@ServerEndpoint("/socket")
public class Socket {

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Nouvel joueur : " + session.getId());
		if(Switch.SessionList.size() == 0){
			// Launch timer
		}
		Switch.SessionList.add(session);
	}
	@OnMessage
	public void onMessage(String txt, Session session) throws IOException {
		System.out.println("Message recu : " + session.getId() + " " + txt);
		if(txt.equals("0")){
			txt = "GAUCHE";
			Switch.numberLeft++;
		}
		else if(txt.equals("1")){
			txt = "DROITE";
			Switch.numberRight++;
		}
		Switch.sendAll(txt);
		System.out.println("g : " + Switch.numberLeft + ", d : " + Switch.numberRight);
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		System.out.println("Connexion interrompue, cause : " + reason.getReasonPhrase());
		Switch.SessionList.remove(session);
	}
}
