package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class DirectionComponent implements Component, Pool.Poolable {
    public Body src = null;
    public Body dest = null;

    public boolean isSet(){ return (src != null && dest != null); }

    @Override
    public void reset() {
        src = null;
        dest = null;
    }
}
