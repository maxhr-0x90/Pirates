package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Modèle 3D d'une entité
 */
public class ModelComponent implements Component, Poolable {
    public ModelInstance model = null;
    public Matrix4 transform = new Matrix4();
    public BoundingBox bounds = new BoundingBox();

    public void setModel(ModelInstance model){
        this.model = model;
        model.calculateBoundingBox(bounds);
    }

    @Override
    public void reset() {
        transform = new Matrix4();
        bounds = new BoundingBox();
        model = null;
    }
}
