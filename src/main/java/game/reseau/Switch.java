package game.reseau;

import java.util.ArrayList;
import javax.websocket.Session;
import java.io.IOException;

public class Switch {
	public static ArrayList<Session> SessionList= new ArrayList<Session>();
	public static int numberLeft = 0;
	public static int numberRight = 0;

	public static void sendAll(String text){
		for(Session s : SessionList){
			try{
				s.getBasicRemote().sendText(text);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
