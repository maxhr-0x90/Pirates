package corp.redacted.game.views;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import corp.redacted.game.Game;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.controller.KeyboardController;
import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.systems.BoatSystem;
import corp.redacted.game.entity.systems.CollisionSystem;
import corp.redacted.game.entity.systems.PhysicsDebugSystem;
import corp.redacted.game.entity.systems.RenderingSystem;

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

    private final float TIMER_INIT = 1 * 30f;
    public static float timer;

    public MainScreen(Game parent){
        PARENT = parent;
        engine = new PooledEngine();

        Game.worldBuilder = new WorldBuilder(engine, Game.assets);
        worldBuilder = Game.worldBuilder;
        worldBuilder.generateWorld();

        renderSys = new RenderingSystem();
        renderSys.setDebugging(false);

        physicsDebugSys = new PhysicsDebugSystem(worldBuilder.getWorld(), renderSys.getCam());

        engine.addSystem(renderSys);
        engine.addSystem(new BoatSystem(clavier, worldBuilder));
        engine.addSystem(new CollisionSystem(worldBuilder));

        camCtrl = new CameraInputController(renderSys.getCam());
    }

    @Override
    public void show() {
        Socket.separation();
        Gdx.input.setInputProcessor(clavier);
        clavier.reset();
        renderSys.setBateaux(worldBuilder.bateauA, worldBuilder.bateauB);
        timer = TIMER_INIT;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
        worldBuilder.getWorld().step(delta, 6, 2);

        timer -= delta;
        checkCtrl();
        endGameCheck();
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
                endGame();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.T)){
                System.err.println("Temps restant: " + timer + " s");
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
            endGame();
        }

        if (statB.barreVie <= 0){
            EndScreen.victoryType = "par destruction !";
            EndScreen.pts = statA.point;
            EndScreen.winningTeam = "rouge";
            endGame();
        }

        if (timer <= 0f){
            EndScreen.victoryType = "commerciale";

            if (statA.point > statB.point){
                EndScreen.pts = statA.point;
                EndScreen.winningTeam = "rouge";
            } else if (statA.point < statB.point){
                EndScreen.pts = statB.point;
                EndScreen.winningTeam = "bleu";
            } else {
                EndScreen.pts = 0;
                EndScreen.winningTeam = "de Personne";
            }
            endGame();
        }
    }

    /**
     * Fonction de fin de partie
     */
    public void endGame(){
        EndScreen end = (EndScreen) PARENT.getScreen(Game.END);
        end.updateLabels();

        PARENT.switchScreen(Game.END);
    }

    @Override
    public void resize(int width, int height) {
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
    }
}
