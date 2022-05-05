package corp.redacted.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import corp.redacted.game.Game;
import corp.redacted.game.serveur.Socket;
import corp.redacted.game.serveur.GestionHttp;
import corp.redacted.game.serveur.Metronome;

public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config;
		Socket server;
		try{
			// DEMARRAGE DU SERVEUR HTTP
			if(!GestionHttp.start()){
				System.out.println(
					"Connexion impossible, vérifiez votre connexion ou votre matériel"
				);
				System.exit(-1);
			}
			System.out.print("Serveur HTTP démarré sur le port ");
			System.out.println(GestionHttp.PORT);

			// DEMARRAGE DE LA WEBSOCKET
			server = new Socket();
			server.start();
			System.out.println("WebSocket démarrée sur le port " + Socket.PORT);

			// DEMARRAGE DU TIMER
			Metronome.launchTimer(150);

			// DEMARRAGE DU JEU
			config = new Lwjgl3ApplicationConfiguration();
			new Lwjgl3Application(new Game(), config);
			System.out.println("Jeu terminé");

			// ARRET DU SERVEUR HTTP
			GestionHttp.stop();

			// ARRET DE LA WEBSOCKET
			server.stop();
			System.out.println("Websocket éteinte");

			// ARRET DU TIMER
			Metronome.timer.cancel();
			Metronome.timer.purge();
		}
		catch(Exception e){
			System.out.println("Quelque chose s'est visiblement mal passé");
			e.printStackTrace();
		}
	}
}
