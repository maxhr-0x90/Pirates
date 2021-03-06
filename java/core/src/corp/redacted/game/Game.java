package corp.redacted.game;

import com.badlogic.gdx.Screen;
import corp.redacted.game.loader.Assets;
import corp.redacted.game.views.EndScreen;
import corp.redacted.game.views.MainScreen;
import corp.redacted.game.views.StartScreen;
import corp.redacted.game.views.OptionScreen;

import java.util.HashMap;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;

/**
 * Classe principale du jeu
 */
public class Game extends com.badlogic.gdx.Game {
	// Liste des identifiant des differents écrans de jeu
	public static final String MAIN = "main";
	public static final String START = "start";
	public static final String END = "end";
	public static final String OPT = "opt";

	private HashMap<String, Screen> screens;

	public final static Assets assets = new Assets();
	public static WorldBuilder worldBuilder;

	@Override
	public void create () {
		screens = new HashMap<>();
		screens.put(MAIN, new MainScreen(this));
		screens.put(START, new StartScreen(this));
		screens.put(END, new EndScreen(this));
		screens.put(OPT, new OptionScreen(this));

		Music vagues = Gdx.audio.newMusic(Gdx.files.internal("sounds/waves.mp3"));
		vagues.setVolume(0.008f);
		vagues.setLooping(true);
		vagues.play();

		setScreen(screens.get(OPT));
		usage();
	}

	/**
	 * Affiche l'usage du jeu
	 */
	private void usage(){
		System.out.println("\n============================================================");
		System.out.println("Bienvenue sur Pirates!");
		System.out.println("Entrez Ctrl+X dans l'écran des options pour entrer sur l'écran de départ");
		System.out.println("Entrez Ctrl+X dans l'écran de départ pour commencer la partie");
		System.out.println("Entrez Ctrl+R dans l'écran de départ pour revenir à l'écran des options");
		System.out.println("Pendant que la partie est en cours vous pouvez:");
		System.out.println("\t - Entrer Ctrl+B pour entrer dans le mode debug");
		System.out.println("\t - Entrer Ctrl+F pour terminer la partie prématurément");
		System.out.println("\t - Entrer Ctrl+C pour activer la caméra libre");
		System.out.println("\t - Entrer Ctrl+T pour voir le timer");
		System.out.println("\t - Entrer Ctrl+I pour changer entre écran séparé et écran global");
		System.out.println("\t - Entrer Ctrl+P pour mettre en pause le jeu");
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
