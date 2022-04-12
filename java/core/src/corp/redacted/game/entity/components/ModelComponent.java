package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Modèle 3D d'une entité
 */
public class ModelComponent implements Component, Poolable {
    public ModelInstance model = null;
    public Matrix4 transform = new Matrix4();

    @Override
    public void reset() {
        transform = new Matrix4();
        model = null;
    }
}
