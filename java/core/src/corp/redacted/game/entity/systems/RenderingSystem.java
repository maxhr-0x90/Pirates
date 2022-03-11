package corp.redacted.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import corp.redacted.game.entity.components.ModelComponent;

public class RenderingSystem extends IteratingSystem {
    private ComponentMapper<ModelComponent> modelMap;

    private Array<Entity> renderQueue;
    private PerspectiveCamera cam;
    private Environment environment;
    private ModelBatch modelBatch;

    private Model axisDebug, gridDebug;
    private ModelInstance axisInstance, gridInstance;

    private boolean debugging = false;

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public RenderingSystem() {
        super(Family.all(ModelComponent.class).get());

        modelMap = ComponentMapper.getFor(ModelComponent.class);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        renderQueue = new Array<>();

        ModelBuilder modBuild = new ModelBuilder();

        axisDebug = modBuild.createXYZCoordinates(1f,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        gridDebug = modBuild.createLineGrid(20, 20, 20, 20,
                new Material(ColorAttribute.createDiffuse(Color.VIOLET)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        axisInstance = new ModelInstance(axisDebug);
        gridInstance = new ModelInstance(gridDebug);

        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        for (Entity entity: renderQueue) {
            ModelComponent modelComp = modelMap.get(entity);
            try {
                modelBatch.render(modelComp.model, environment);
            } catch (NullPointerException ex){
                System.err.println("Modèle non chargé");
            }
        }

        if (debugging) {debugRender();}
        modelBatch.end();

        spriteBatch.begin();
        font.draw(spriteBatch, "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, font.getLineHeight());
        spriteBatch.end();

        renderQueue.clear();
    }

    /**
     * Ajoute le rendu de debug
     */
    private void debugRender(){
        modelBatch.render(axisInstance, environment);
        modelBatch.render(gridInstance);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    /**
     *
     * @return La camera de rendu
     */
    public PerspectiveCamera getCam() {
        return cam;
    }

    /**
     *
     * @param debugging le mode de rendu (debug ou non)
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }
}
