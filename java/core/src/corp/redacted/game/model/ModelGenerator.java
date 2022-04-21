package corp.redacted.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import corp.redacted.game.Game;
import corp.redacted.game.loader.Assets;
import corp.redacted.game.model.SandShapeBuilder;

/**
 * Méthodes de génération des diffenrents modèles du jeu
 */
public class ModelGenerator {
    /**
     * @param viewWidth Largeur de l'espace d'affichage ou plus généralement du modèle
     * @param depth Profondeur du modèle
     * @param alt0Width Largeur de la partie "mer" du modèle
     * @param alt0Height Hauteur de la partie "mer" du modèlse
     * @param stepNb Finesse de la partie "plage" du modèle
     * @param stiffness Dénivelé de la partie "plage" du modèle
     * @return Un modèle de mer avec une plage
     */
    public static Model seaModel(
            float viewWidth, float depth, float alt0Width, float alt0Height, int stepNb, float stiffness
    ){
        ModelBuilder builder = new ModelBuilder();

        builder.begin();

        builder.node("sand", sandModel(viewWidth, depth, alt0Width, alt0Height, stepNb, stiffness));
        builder.node("water", waterModel(alt0Width, alt0Height));

        return builder.end();
    }

    /**
     *
     * @param width Largeur du modèle
     * @param height Profondeur du modèle
     * @return Modèle 3D de la mer
     */
    public static Model waterModel(float width, float height){
        ModelBuilder modelBuilder = new ModelBuilder();
        Model water = modelBuilder.createRect(
                -width/2, -height/2, 0,
                width/2, -height/2, 0,
                width/2, height/2, 0,
                -width/2, height/2, 0,
                0, 1, 0,
                new Material(
                        ColorAttribute.createDiffuse(new Color(.05f, .45f, .75f, 1)),
                        ColorAttribute.createSpecular(new Color(1, 1, 1, 1)),
                        new BlendingAttribute(0.8f),
                        TextureAttribute.createNormal(new Texture(Gdx.files.internal("models/Wave A.png")))
                ),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates
        );

        water.nodes.get(0).id = "water";

        return water;
    }

    /**
     * @param viewWidth Largeur de l'espace d'affichage ou plus généralement du modèle
     * @param depth Profondeur du modèle
     * @param alt0Width Largeur du modèle sur le plan z = 0
     * @param alt0Height Hauteur du modèle sur le plan z = 0
     * @param stepNb Finesse de la partie "plage" du modèle
     * @param stiffness Dénivelé de la partie "plage" du modèle
     * @return Un modèle de plage
     */
    public static Model sandModel(
            float viewWidth, float depth, float alt0Width, float alt0Height, int stepNb, float stiffness
    ){
        MeshPartBuilder meshBuilder;
        ModelBuilder builder = new ModelBuilder();

        builder.begin();

        Node node = builder.node();
        node.id = "sand";

        TextureAttribute texAttr = TextureAttribute.createDiffuse(Game.assets.manager.get(Assets.sandTexture, Texture.class));

        meshBuilder = builder.part("sand", GL20.GL_TRIANGLES,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(
                        ColorAttribute.createAmbient(new Color(1, 1, 1, 1)),
                        ColorAttribute.createDiffuse(new Color(0.638190f, 0.486372f, 0.143198f, 1)),
                        ColorAttribute.createSpecular(new Color(1, 1, 1, 1)),
                        texAttr
                )
        );

        SandShapeBuilder sandBuilder = new SandShapeBuilder(
                meshBuilder, viewWidth, depth, alt0Width, alt0Height, stepNb, stiffness
        );
        sandBuilder.build();

        return builder.end();
    }
}
