package corp.redacted.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import corp.redacted.game.Game;
import corp.redacted.game.loader.Assets;

public class EndScreen implements Screen {
    private final Game PARENT;

    public static String winningTeam = "???";
    public static int pts = 0;
    public static String victoryType = "par hasard ???";
    public static boolean draw = false;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;

    private Stage stage;
    private Table table;

    private Label winners, ptsIntro, points, vicType;

    public EndScreen(Game parent){
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
     * Met à jour les labels
     */
    public void updateLabels(){
        if (draw){
            winners.setText("Le pouvoir de la camaraderie l'emporte");
            ptsIntro.setText("Nos equipes repartent mains dans la main avec");
            points.setText(pts * 1000 + "$ en marchandises");
            vicType.setText("Egalite");
        } else {
            winners.setText("L'equipe " + winningTeam + " sort victorieuse de cet affrontrement !");
            ptsIntro.setText("Et ce avec en poche l'equivalent de");
            points.setText(pts * 1000 + " $ en marchandises");
            vicType.setText("Victoire " + victoryType);
        }
    }

    /**
     * Place les éléments sur l'écran
     */
    private void setupTable(){
        table.setFillParent(true);
        //table.setDebug(true);

        winners = new Label("", new Label.LabelStyle(font, Color.WHITE));
        ptsIntro = new Label("", new Label.LabelStyle(font, Color.WHITE));
        points = new Label("", new Label.LabelStyle(font, Color.WHITE));
        vicType = new Label("", new Label.LabelStyle(font, Color.WHITE));


        table.add(winners);
        table.row().pad(0, 0, 25, 0);
        table.add(ptsIntro);
        table.row().pad(0, 0, 10, 0);
        table.add(points);
        table.row().pad(0, 0, 25, 0);
        table.add(vicType);

        updateLabels();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        checkCtrl();
    }

    /**
     * Vérifie si une commande a été entrée pour revenir à l'écrand de départ
     */
    private void checkCtrl(){
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.R)){
            resetGame();
        }
    }

    /**
     * Fonction de reset de partie
     */
    public void resetGame(){
        if (Game.worldBuilder != null){
            Game.worldBuilder.reset();
        }
        PARENT.switchScreen(Game.START);
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
