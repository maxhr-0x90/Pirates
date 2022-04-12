package corp.redacted.game;

import com.badlogic.gdx.Screen;
import corp.redacted.game.loader.Assets;
import corp.redacted.game.views.EndScreen;
import corp.redacted.game.views.MainScreen;
import corp.redacted.game.views.StartScreen;

import java.util.HashMap;

/**
 * Classe principale du jeu
 */
public class Game extends com.badlogic.gdx.Game {
	// Liste des identifiant des differents écrans de jeu
	public static final String MAIN = "main";
	public static final String START = "start";
	public static final String END = "end";

	private HashMap<String, Screen> screens;

	public final static Assets assets = new Assets();

	@Override
	public void create () {
		screens = new HashMap<>();
		screens.put(MAIN, new MainScreen(this));
		screens.put(START, new StartScreen(this));
		screens.put(END, new EndScreen(this));

		setScreen(screens.get(START));
		usage();
	}

	/**
	 * Affiche l'usage du jeu
	 */
	private void usage(){
		System.out.println("\n============================================================");
		System.out.println("Bienvenue sur Pirates!");
		System.out.println("Entrez Ctrl+X dans l'écran de départ pour commencer la partie");
		System.out.println("Pendant que la partie est en cours vous pouvez:");
		System.out.println("\t - Entrer Ctrl+B pour entrer dans le mode debug");
		System.out.println("\t - Entrer Ctrl+F pour terminer la partie prématurément");
		System.out.println("Entrez Ctrl+R dans l'écran de fin pour revenir sur l'écran de départ");
		System.out.println("============================================================\n");
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

	/**
	 * @param id Identifiant de l'écran
	 * @return L'écran avec l'identifiant correspondant si celui-ci existe, null sinon
	 */
	public Screen getScreen(String id) {
		return screens.get(id);
	}
}
