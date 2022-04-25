package corp.redacted.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
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

    private Label winners, points, vicType;

    private ModelBatch modelBatch;
    private PerspectiveCamera camera;
    private Environment environment;
    public static ModelInstance MODEL_BAT_BLEU, MODEL_BAT_ROUGE, MODEL_EGAL;
    public static ModelInstance victoryModel;

    private float angle = 0;

    public EndScreen(Game parent){
        PARENT = parent;

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 64;
        fontParameter.borderWidth = 5;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        stage = new Stage(new FitViewport(768 * 3.5f, 144 * 3.5f));
        table = new Table();

        setupTable();

        stage.addActor(table);

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.85f, 0.85f, 0.85f, -1f, -0.8f, -0.5f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(20f, 0f, 20f);
        camera.lookAt(0,0,5);
        camera.up.set(0, 0, 1);
        camera.near = .1f;
        camera.far = 300f;
        camera.update();

        Game.assets.queueAdd3DModels();
        Game.assets.manager.finishLoading();
        MODEL_BAT_ROUGE = new ModelInstance(Game.assets.manager.get(Game.assets.boatRModel, Model.class));
        MODEL_BAT_BLEU = new ModelInstance(Game.assets.manager.get(Game.assets.boatBModel, Model.class));
        MODEL_EGAL = new ModelInstance(Game.assets.manager.get(Game.assets.shakeModel, Model.class));
        MODEL_EGAL.transform.scale(2, 2, 2);
        MODEL_EGAL.transform.translate(0, 0, 5);

        victoryModel = MODEL_EGAL;
    }

    /**
     * Met à jour les labels
     */
    public void updateLabels(){
        if (draw){
            winners.setText("Le pouvoir de la camaraderie l'emporte :)");
            points.setText("Nos equipes repartent avec " + pts * 1000 + "$ en marchandises");
            vicType.setText("Egalite");
        } else {
            winners.setText("L'equipe " + winningTeam + " sort victorieuse de cet affrontrement !");
            points.setText("Et ce avec en poche l'equivalent de " + pts * 1000 + " $ en marchandises");
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
        points = new Label("", new Label.LabelStyle(font, Color.WHITE));
        vicType = new Label("", new Label.LabelStyle(font, Color.WHITE));


        table.add(winners);
        table.row().pad(0, 0, 25, 0);
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
        angle = (angle + delta) % 360;
        float angluarSpeed = 20f;

        camera.position.set(
                22 * (float)Math.cos(Math.toRadians(angle * angluarSpeed)),
                22 * (float)Math.sin(Math.toRadians(angle * angluarSpeed)),
                22f
        );
        camera.lookAt(0,0,6);
        camera.up.set(0, 0, 1);

        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight() - stage.getViewport().getScreenHeight();
        camera.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - stage.getViewport().getScreenHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(victoryModel, environment);
        modelBatch.end();

        Gdx.gl.glViewport(
                stage.getViewport().getScreenX(), Gdx.graphics.getHeight() - stage.getViewport().getScreenHeight(),
                stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight()
        );
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
