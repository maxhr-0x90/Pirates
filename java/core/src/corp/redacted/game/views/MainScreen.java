package corp.redacted.game.views;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import corp.redacted.game.Game;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.controller.KeyboardController;
import corp.redacted.game.entity.components.ModelComponent;
import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.systems.*;

import corp.redacted.game.loader.Assets;
import corp.redacted.game.serveur.Socket;

/**
 * Écran principal de jeu. Là où le jeu, à proprement parlé, se déroule
 */
public class MainScreen implements Screen {
    private final Game PARENT;

    private PooledEngine engine;
    private WorldBuilder worldBuilder;

    private KeyboardController clavier = new KeyboardController();
    private CameraInputController camCtrl;

    private RenderingSystem renderSys;
    private PhysicsDebugSystem physicsDebugSys;

    private boolean debugging = false;
    private boolean freeCam = false;

    private final float TIMER_INIT = 2 * 60f;
    public static float timer;
    private final float START_TIMER_INIT = 3f;
    private static float startTimer;

    private Label timerLabel;
    private Stage stage;

    private boolean start = true;
    private boolean paused = true;

    private Node couronneA, couronneB;

    public MainScreen(Game parent){
        PARENT = parent;
        engine = new PooledEngine();

        renderSys = new RenderingSystem();

        Game.worldBuilder = new WorldBuilder(engine, Game.assets, renderSys.particleBatch);
        worldBuilder = Game.worldBuilder;
        worldBuilder.generateWorld();

        renderSys.setDebugging(false);

        physicsDebugSys = new PhysicsDebugSystem(worldBuilder.getWorld(), renderSys.getCam());

        engine.addSystem(renderSys);
        engine.addSystem(new BoatSystem(clavier, worldBuilder));
        engine.addSystem(new CollisionSystem(worldBuilder));
        engine.addSystem(new ArrowSystem());

        camCtrl = new CameraInputController(renderSys.getCam());

        setupTimerHUD();
    }

    /**
     * Met en place l'affichage du timer
     */
    private void setupTimerHUD(){
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 168;
        fontParameter.borderWidth = 6;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        BitmapFont font = fontGenerator.generateFont(fontParameter);

        stage = new Stage(new FitViewport(1280, 720));
        Table table = new Table();

        table.setFillParent(true);

        timerLabel = new Label(Integer.toString((int)Math.ceil(startTimer)), new Label.LabelStyle(font, Color.WHITE));
        table.add(timerLabel).fillX().uniformX();

        stage.addActor(table);
    }

    @Override
    public void show() {
        Socket.separation();
        Gdx.input.setInputProcessor(clavier);
        clavier.reset();
        renderSys.setBoats(worldBuilder.bateauA, worldBuilder.bateauB);
        renderSys.resetHUD();
        timer = TIMER_INIT;
        startTimer = START_TIMER_INIT;
        paused = true;
        start = true;

        couronneA = worldBuilder.bateauA.getComponent(ModelComponent.class).model.getNode("Couronne");
        couronneB = worldBuilder.bateauB.getComponent(ModelComponent.class).model.getNode("Couronne");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(paused ? 0 : delta);
        worldBuilder.getWorld().step(paused ? 0 : delta, 6, 2);

        timer -= paused ? 0 : delta;
        checkCtrl();
        endGameCheck();

        startTimerUpdate(delta);
        updateCurrentWinner();
    }

    private void startTimerUpdate(float delta){
        if (!paused || !start){ return; }

        startTimer -= delta;
        if (startTimer <= 0){
            start = false;
            paused = false;
        } else {
            timerLabel.setText(Integer.toString((int)Math.ceil(startTimer)));
            stage.act(delta);
            stage.draw();
        }
    }

    private void updateCurrentWinner(){
        StatComponent statA = worldBuilder.bateauA.getComponent(StatComponent.class);
        StatComponent statB = worldBuilder.bateauB.getComponent(StatComponent.class);

        Node nodeA = worldBuilder.bateauA.getComponent(ModelComponent.class).model.getNode("Bateau");
        Node nodeB = worldBuilder.bateauB.getComponent(ModelComponent.class).model.getNode("Bateau");

        if (statA.point == 0 && statB.point == 0){
            couronneA.detach();
            couronneB.detach();
            return;
        }

        if (statA.point > statB.point){
            couronneA.attachTo(nodeA);
            couronneB.detach();
        } else if (statA.point < statB.point){
            couronneB.attachTo(nodeB);
            couronneA.detach();
        } else {
            couronneA.attachTo(nodeA);
            couronneB.attachTo(nodeB);
        }
    }

    /**
     * Vérifie si une commande a été entrée pour passer dans le mode debug ou terminer la partie
     */
    private void checkCtrl(){
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
                if (debugging){
                    System.err.println("Mode debug desactivé");
                    engine.removeSystem(physicsDebugSys);
                    renderSys.setDebugging(false);
                    debugging = false;
                } else {
                    System.err.println("Mode debug activé");
                    engine.addSystem(physicsDebugSys);
                    renderSys.setDebugging(true);
                    debugging = true;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.C)){
                if (freeCam){
                    System.err.println("Caméra libre desactivé");
                    Gdx.input.setInputProcessor(clavier);
                    engine.getSystem(RenderingSystem.class).windowResized();
                    freeCam = false;
                } else {
                    System.err.println("Caméra libre activé");
                    Gdx.input.setInputProcessor(camCtrl);
                    freeCam = true;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.I)){
                renderSys.switchSplit();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F)){
                System.err.println("Partie terminé prématuré");
                endGame(0f);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.T)){
                System.err.println("Temps restant: " + timer + " s");
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
                paused = !paused;
            }
        }
    }

    /**
     * Fonction de vérification de fin de partie
     */
    public void endGameCheck(){
        StatComponent statA = worldBuilder.bateauA.getComponent(StatComponent.class);
        StatComponent statB = worldBuilder.bateauB.getComponent(StatComponent.class);

        if (statA.barreVie <= 0){
            EndScreen.victoryType = "par destruction !";
            EndScreen.pts = statB.point;
            EndScreen.winningTeam = "bleu";
            EndScreen.victoryModel = EndScreen.MODEL_BAT_BLEU;
            endGame(2f);
        }

        if (statB.barreVie <= 0){
            EndScreen.victoryType = "par destruction !";
            EndScreen.pts = statA.point;
            EndScreen.winningTeam = "rouge";
            EndScreen.victoryModel = EndScreen.MODEL_BAT_ROUGE;
            endGame(2f);
        }

        if (timer <= 0f){
            EndScreen.victoryType = "commerciale";

            if (statA.point > statB.point){
                EndScreen.pts = statA.point;
                EndScreen.winningTeam = "rouge";
                EndScreen.victoryModel = EndScreen.MODEL_BAT_ROUGE;
            } else if (statA.point < statB.point){
                EndScreen.pts = statB.point;
                EndScreen.winningTeam = "bleu";
                EndScreen.victoryModel = EndScreen.MODEL_BAT_BLEU;
            } else {
                EndScreen.pts = statA.point;
                EndScreen.draw = true;
                EndScreen.victoryModel = EndScreen.MODEL_EGAL;
            }
            endGame(2f);
        }
    }

    /**
     * Fonction de fin de partie
     */
    public void endGame(float wait){
        for (Entity e: engine.getEntities()) {
            if (e != worldBuilder.bateauA && e != worldBuilder.bateauB && e != worldBuilder.ocean){
                engine.removeEntity(e);
            }
        }
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                EndScreen end = (EndScreen) PARENT.getScreen(Game.END);
                end.updateLabels();

                PARENT.switchScreen(Game.END);
            }
        }, wait);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        engine.getSystem(RenderingSystem.class).windowResized();
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
        engine.clearPools();
        stage.dispose();
    }
}
