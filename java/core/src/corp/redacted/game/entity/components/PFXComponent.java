package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool;

public class PFXComponent implements Component, Pool.Poolable {
    public Matrix4 transform = new Matrix4();
    public ParticleEffect pfx = null;

    @Override
    public void reset() {
        if (pfx != null){ pfx.reset(); }
        pfx = null;
        transform.idt();
    }
}
