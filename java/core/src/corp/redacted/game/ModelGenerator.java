package corp.redacted.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import corp.redacted.game.model.SandShapeBuilder;

/**
 * Méthodes de génération des diffenrents modèles du jeu
 */
public class ModelGenerator {
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
                        ColorAttribute.createDiffuse(Color.SKY),
                        new BlendingAttribute(0.5f)
                ),
                Usage.Position | Usage.Normal
        );

        water.nodes.get(0).id = "water";

        return water;
    }

    public static Model sandModel(
            float viewWidth, float depth, float alt0Width, float alt0Height, int stepNb, float stiffness
    ){
        MeshPartBuilder meshBuilder;
        ModelBuilder builder = new ModelBuilder();

        builder.begin();

        Node node = builder.node();
        node.id = "sand";

        meshBuilder = builder.part("sand", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(
                ColorAttribute.createDiffuse(Color.GOLDENROD)
        ));

        SandShapeBuilder sandBuilder = new SandShapeBuilder(
                meshBuilder, viewWidth, depth, alt0Width, alt0Height, stepNb, stiffness
        );
        sandBuilder.build();

        return builder.end();
    }
}
