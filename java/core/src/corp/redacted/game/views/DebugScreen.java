package corp.redacted.game.views;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import corp.redacted.game.Game;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.entity.systems.PhysicsDebugSystem;
import corp.redacted.game.entity.systems.RenderingSystem;

import corp.redacted.game.entity.systems.BoatSystem;
import corp.redacted.game.entity.systems.CollisionSystem;
import corp.redacted.game.controller.KeyboardController;

/**
 * Écran de debug
 */
public class DebugScreen implements Screen {
    private final Game PARENT;

    private PooledEngine engine;
    private WorldBuilder worldBuilder;

    private CameraInputController camController;

    private KeyboardController clavier = new KeyboardController();

    public DebugScreen(Game parent){
        super();

        PARENT = parent;
        engine = new PooledEngine();

        worldBuilder = new WorldBuilder(engine);
        worldBuilder.generateWorld();

        RenderingSystem renderSys = new RenderingSystem(false);
        renderSys.setDebugging(true);
        engine.addSystem(renderSys);
        engine.addSystem(new PhysicsDebugSystem(worldBuilder.getWorld(), renderSys.getCam()));
        engine.addSystem(new BoatSystem(clavier, worldBuilder));
        engine.addSystem(new CollisionSystem());

        camController = new CameraInputController(renderSys.getCam());
        Gdx.input.setInputProcessor(camController);
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
    }

    @Override
    public void resize(int width, int height) {

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
    public void dispose() { engine.clearPools(); }
}
