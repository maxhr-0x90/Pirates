package corp.redacted.game;

import com.badlogic.gdx.Screen;
import corp.redacted.game.views.MainScreen;

import java.util.HashMap;

/**
 * Classe principale du jeu
 */
public class Game extends com.badlogic.gdx.Game {
	// Liste des identifiant des differents écrans de jeu
	private static final String MAIN = "main";

	private HashMap<String, Screen> screens;

	@Override
	public void create () {
		screens = new HashMap<>();
		screens.put(MAIN, new MainScreen(this));

		setScreen(screens.get(MAIN));
	}

	/**
	 * Change l'écran de jeu
	 *
	 * @param id identifiant de l'écran
	 */
	public void switchScreen(String id){
		Screen screen = screens.get(id);
		if (screen == null){ System.err.println("Écran de jeu inconnu: Changement impossible."); }
		else { setScreen(screen); }
	}
}
