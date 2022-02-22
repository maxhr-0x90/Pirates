package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Modèle 3D d'une entité
 */
public class ModelComponent implements Component, Poolable {
    public ModelInstance model = null;

    @Override
    public void reset() {
        model = null;
    }
}
