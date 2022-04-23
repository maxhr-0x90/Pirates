package corp.redacted.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import corp.redacted.game.IConfig;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.entity.components.ModelComponent;
import corp.redacted.game.entity.components.TypeComponent;
import corp.redacted.game.loader.Assets;
import corp.redacted.game.shader.CustomShaderProvider;
import corp.redacted.game.views.MainScreen;

public class RenderingSystem extends IteratingSystem {
    private ComponentMapper<ModelComponent> modelMap;
    private ComponentMapper<BodyComponent> bodyMap;
    private ComponentMapper<TypeComponent> typeMap;

    private PerspectiveCamera cam;
    private float fovV = 80;

    private Array<Entity> renderQueue;
    private Environment environment;
    private ModelBatch modelBatch;

    private Model axisDebug, gridDebug;
    private ModelInstance axisInstance, gridInstance;

    private boolean debugging = false;
    private boolean split = true;

    private Entity batG, batD;

    private SpriteBatch spriteBatch;
    private BitmapFont defaultFont, mainFont;

    public RenderingSystem() {
        super(Family.all(ModelComponent.class).get());

        // Initialisation des maps
        modelMap = ComponentMapper.getFor(ModelComponent.class);
        bodyMap = ComponentMapper.getFor(BodyComponent.class);
        typeMap = ComponentMapper.getFor(TypeComponent.class);

        // Création de l'environnement 3D
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.75f, 0.75f, 0.75f, -1f, 0f, -1f));

        // Placement de la camera
        cam = new PerspectiveCamera(fovV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!split){
            updateCam(IConfig.LARGEUR_CARTE + 20, IConfig.HAUTEUR_CARTE + 20, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            cam.near = 10f;
            cam.far = 200f;
        }

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
        defaultFont = new BitmapFont();

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 48;
        fontParameter.borderWidth = 3;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        mainFont = fontGenerator.generateFont(fontParameter);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (split){
            splitRender();
        } else {
            globalRender();
        }

        hudRender();

        if (debugging) {debugRender();}

        renderQueue.clear();
    }

    /**
     * Fait un rendu global de l'espace de jeu
     */
    private void globalRender(){
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        for (Entity entity: renderQueue) {
            ModelComponent modelComp = modelMap.get(entity);
            BodyComponent bodyComp = bodyMap.get(entity);

            if(modelComp != null){
                applyBodyTransform(modelComp, bodyComp);
                modelBatch.render(modelComp.model, environment);
            }
        }

        modelBatch.end();
    }

    /**
     * Fait un rendu avec la séparation des écran pour chaque bateau
     */
    private void splitRender(){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        int viewWidth = Gdx.graphics.getWidth();
        int viewHeight = Gdx.graphics.getHeight();

        BodyComponent bodyBatG = bodyMap.get(batG);
        BodyComponent bodyBatD = bodyMap.get(batD);
        Vector2 pos;

        // Bateau à gauche
        Gdx.gl.glViewport(0, 0,  viewWidth / 2, viewHeight);
        updateCamSplit(viewWidth, viewHeight, bodyBatG);
        beginBatchRender();

        // Bateau à droite
        Gdx.gl.glViewport(viewWidth /  2, 0, viewWidth / 2, viewHeight);
        updateCamSplit(viewWidth, viewHeight, bodyBatD);
        beginBatchRender();
    }

    private void updateCamSplit(int viewWidth, int viewHeight, BodyComponent bodyBat) {
        Vector2 pos;
        cam.viewportWidth = viewWidth / 2f;
        cam.viewportHeight = viewHeight;

        pos = bodyBat.body.getPosition();
        cam.position.set(pos.x, pos.y - 20, 100);
        cam.lookAt(pos.x, pos.y, 0);

        cam.update();
    }

    private void beginBatchRender(){
        modelBatch.begin(cam);
        for (Entity entity: renderQueue) {
            ModelComponent modelComp = modelMap.get(entity);
            BodyComponent bodyComp = bodyMap.get(entity);

            if(modelComp != null){
                applyBodyTransform(modelComp, bodyComp);

                if (isVisible(modelComp, cam)){ modelBatch.render(modelComp.model, environment); }
                //modelBatch.render(modelComp.model, environment);
            }
        }

        modelBatch.end();
    }

    private void applyBodyTransform(ModelComponent modelComp, BodyComponent bodyComp) {
        if (bodyComp != null){
            Vector2 pos = bodyComp.body.getWorldCenter();
            float angle = bodyComp.body.getAngle();
            modelComp.model.transform.idt();
            modelComp.model.transform.translate(new Vector3(pos, 0));
            modelComp.model.transform.rotateRad(new Vector3(0, 0, 1), angle);
            modelComp.model.transform.mul(modelComp.transform);
        }
    }

    private boolean isVisible(ModelComponent model, PerspectiveCamera cam){
        BoundingBox bb = new BoundingBox(model.bounds);
        return cam.frustum.boundsInFrustum(bb.mul(model.model.transform));
    }

    /**
     * Place la caméra de manière à afficher un objet de dimension width x height sur le plan z = 0 dans son intégralité
     *
     * @param width Largeur de l'objet
     * @param height Hauteur de l'objet
     * @param widthView Largeur du ViewPort (espace d'affichage)
     * @param heightView Hauteur du ViewPort (espace d'affichage)
     */
    private void updateCam(float width, float height, float widthView, float heightView){
        float ratioXY = width / height;
        float ratioXYView = widthView / heightView;
        float z, fovH;

        if (ratioXY < ratioXYView){ // => on veut que la hauteur de l'objet soit la meme que celle de la vue
            z = (float) ((height / 2) / Math.tan(Math.toRadians(fovV / 2)));
        } else {  // => on veut que la largeur de l'objet soit la meme que celle de la vue
            double d = (heightView * 0.5) / Math.tan(Math.toRadians(fovV * 0.5));
            fovH = (float) (2 * Math.toDegrees(Math.atan((widthView * 0.5) / d)));
            z = (float) ((width / 2) / Math.tan(Math.toRadians(fovH / 2)));
        }

        cam.viewportWidth = widthView;
        cam.viewportHeight = heightView;
        cam.position.set(0f, 0f, z);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 2 * z;
        cam.up.set(0, 1, 0);
        cam.update();
    }

    /**
     * Ajoute le rendu de debug
     */
    private void debugRender(){
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        modelBatch.begin(cam);
        modelBatch.render(axisInstance, environment);
        modelBatch.render(gridInstance);
        modelBatch.end();

        spriteBatch.begin();
        defaultFont.draw(spriteBatch, "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, defaultFont.getLineHeight());
        spriteBatch.end();
    }

    private void hudRender(){
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float timer = Math.max(MainScreen.timer, 0);
        String time = String.format("%d:%02d", (int) Math.floor(timer / 60), (int) timer % 60);

        spriteBatch.begin();
        mainFont.draw(spriteBatch, time, 0, Gdx.graphics.getHeight());
        spriteBatch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    /**
     * @return La camera de rendu
     */
    public Camera getCam() {
        return cam;
    }

    /**
     * @param debugging le mode de rendu (debug ou non)
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    /**
     * Change le mode d'affichage: écrans séparés ou non
     */
    public void switchSplit() {
        split = !split;

        if (!split) {
            windowResized();
        }
    }

    public void setBateaux(Entity batG, Entity batD){
        this.batG = batG;
        this.batD = batD;
    }

    /**
     * Fonction à appeler lors du redimenssionnement de la fenetre
     */
    public void windowResized(){
        if (!split){
            updateCam(IConfig.LARGEUR_CARTE + 20, IConfig.HAUTEUR_CARTE + 20, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
}
