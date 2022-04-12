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
import corp.redacted.game.entity.systems.BoatSystem;
import corp.redacted.game.entity.systems.CollisionSystem;
import corp.redacted.game.entity.systems.PhysicsDebugSystem;
import corp.redacted.game.entity.systems.RenderingSystem;

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

    public MainScreen(Game parent){
        PARENT = parent;
        engine = new PooledEngine();

        worldBuilder = new WorldBuilder(engine, Game.assets);
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
        Gdx.input.setInputProcessor(clavier);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
        worldBuilder.getWorld().step(delta, 6, 2);
        checkCtrl();
    }

    /**
     * Vérifie si une commande a été entrée pour passer dans le mode debug ou terminer la partie
     */
    private void checkCtrl(){
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
                if (debugging){
                    engine.removeSystem(physicsDebugSys);
                    renderSys.setDebugging(false);
                    debugging = false;
                } else {
                    engine.addSystem(physicsDebugSys);
                    renderSys.setDebugging(true);
                    debugging = true;
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.C)){
                if (freeCam){
                    Gdx.input.setInputProcessor(clavier);
                    engine.getSystem(RenderingSystem.class).windowResized();
                    freeCam = false;
                } else {
                    Gdx.input.setInputProcessor(camCtrl);
                    freeCam = true;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F)){
                endGame();
            }
        }
    }

    /**
     * Fonction de fin de partie
     */
    public void endGame(){
        // TODO Mettre à jour les labels de l'écran de fin
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
