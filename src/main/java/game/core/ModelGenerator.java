package game.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * Méthodes de génération des diffenrents modèles du jeu
 */
public class ModelGenerator {
    private static final float BOAT_HEIGHT = 4f;

    /**
     *
     * @param width Largeur du modele
     * @param depth Profondeur du modèle
     * @param color Couleur du modèle
     * @return Modèle 3D de bateau (version debug)
     */
    public static Model debugBoatModel(float width, float depth, Color color){
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createBox(width, BOAT_HEIGHT, depth,
                new Material(ColorAttribute.createDiffuse(color)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    /**
     *
     * @param radius Rayon du boulet
     * @return Modèle 3D de boulet de canon (version debug)
     */
    public static Model debugCanonBallModel(float radius){
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createSphere(radius, radius, radius, 50, 50,
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    /**
     *
     * @param width Largeur du modèle
     * @param depth Profondeur du modèle
     * @return Modèle 3D de la mer
     */
    public static Model debugSeaModel(float width, float depth){
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createRect(
                width/2, 0, -depth/2,
                -width/2, 0, -depth/2,
                -width/2, 0, depth/2,
                width/2, 0, depth/2,
                0, 1, 0,
                new Material(ColorAttribute.createDiffuse(Color.SKY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
    }
}
