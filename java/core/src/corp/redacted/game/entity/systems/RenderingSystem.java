package corp.redacted.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.entity.components.ModelComponent;
import corp.redacted.game.shader.CustomShaderProvider;

public class RenderingSystem extends IteratingSystem {
    private ComponentMapper<ModelComponent> modelMap;
    private ComponentMapper<BodyComponent> bodyMap;

    private Array<Entity> renderQueue;
    private Camera cam;
    private Environment environment;
    private ModelBatch modelBatch;

    private Model axisDebug, gridDebug;
    private ModelInstance axisInstance, gridInstance;

    private boolean debugging = false;

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public RenderingSystem(boolean ortho) {
        super(Family.all(ModelComponent.class).get());

        // Initialisation des maps
        modelMap = ComponentMapper.getFor(ModelComponent.class);
        bodyMap = ComponentMapper.getFor(BodyComponent.class);

        // Création de l'environnement 3D
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Placement de la camera
        if (ortho){
            cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        cam.position.set(0f, 0f, 75f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        renderQueue = new Array<>();

        CustomShaderProvider csp = new CustomShaderProvider(null);
        modelBatch = new ModelBatch(csp);

        ModelBuilder modBuild = new ModelBuilder();

        // Création des éléments de debug
        axisDebug = modBuild.createXYZCoordinates(1f,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        gridDebug = modBuild.createLineGrid(20, 20, 20, 20,
                new Material(ColorAttribute.createDiffuse(Color.VIOLET)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        axisInstance = new ModelInstance(axisDebug);
        axisInstance.transform.scale(10, 10, 10);
        gridInstance = new ModelInstance(gridDebug);
        gridInstance.transform.rotate(new Vector3(1, 0, 0), 90f);

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
            BodyComponent bodyComp = bodyMap.get(entity);

            if(modelComp != null){
                if (bodyComp != null){
                    Vector2 pos = bodyComp.body.getWorldCenter();
                    float angle = bodyComp.body.getAngle();
                    modelComp.model.transform.idt();
                    modelComp.model.transform.translate(new Vector3(pos, 0));
                    modelComp.model.transform.rotateRad(new Vector3(0, 0, 1), angle);
                }
                modelBatch.render(modelComp.model, environment);
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
    public Camera getCam() {
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
