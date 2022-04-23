package corp.redacted.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sun.tools.javac.util.StringUtils;
import corp.redacted.game.Game;
import corp.redacted.game.loader.Assets;

/**
 * Écran présent au démarrage du jeu
 */
public class StartScreen implements Screen {
    private final Game PARENT;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;

    private Stage stage;
    private Table table;

    private float timeBuff = 0f;
    private int ellipses = 0;
    private Label loading;

    public StartScreen(Game parent){
        PARENT = parent;

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 64;
        fontParameter.borderWidth = 5;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        stage = new Stage(new FitViewport(2560, 1440));
        table = new Table();

        setupTable();

        stage.addActor(table);
    }

    /**
     * Place les éléments sur l'écran
     */
    private void setupTable(){
        table.setFillParent(true);
        //table.setDebug(true);

        Label invit = new Label("Patience moussaillon la bataille ne devrait pas tarder !", new Label.LabelStyle(font, Color.WHITE));
        loading = new Label("Creation des equipages      ", new Label.LabelStyle(font, Color.WHITE));

        table.add(invit).fillX().uniformX();
        table.row().pad(0, 0, 25, 0);
        table.add(loading);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        updateEllipses(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        checkCtrl();
    }

    /**
     * Vérifie si une commande a été entrée pour passer au jeu
     */
    private void checkCtrl(){
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)){
                PARENT.switchScreen(Game.MAIN);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
                PARENT.switchScreen(Game.OPT);
            }
        }
    }

    /**
     * Met à jour l'animation de ellipse
     *
     * @param delta Temps écoulé depuis la dernière execution de la fonction
     */
    private void updateEllipses(float delta){
        timeBuff += delta;

        if (timeBuff < .5f) { return; }

        ellipses = (ellipses + 1) % 4;

        String dots = new String(new char[ellipses * 2]).replace("\0\0", " .");
        String spaces = new String(new char[(3 - ellipses) * 2]).replace('\0', ' ');
        loading.setText("Creation des equipages" + dots + spaces);

        timeBuff = 0f;
    }

    @Override
    public void resize(int x, int y) {
        stage.getViewport().update(x, y, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
