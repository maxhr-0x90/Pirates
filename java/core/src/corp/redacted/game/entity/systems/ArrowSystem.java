package corp.redacted.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import corp.redacted.game.entity.components.DirectionComponent;
import corp.redacted.game.entity.components.ModelComponent;

public class ArrowSystem extends IteratingSystem {
    private ComponentMapper<DirectionComponent> directMap;
    private ComponentMapper<ModelComponent> modelMap;

    public ArrowSystem() {
        super(Family.all(DirectionComponent.class).get());

        directMap = ComponentMapper.getFor(DirectionComponent.class);
        modelMap = ComponentMapper.getFor(ModelComponent.class);;
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        DirectionComponent dirC = directMap.get(entity);
        ModelComponent modC = modelMap.get(entity);

        if (dirC != null && dirC.isSet() && modC != null){
            Vector2 src = dirC.src.getPosition();
            Vector2 dest = dirC.dest.getPosition();
            Vector2 dir = new Vector2(src).add(new Vector2(dest).sub(src).nor().scl(Math.min(30, src.dst(dest))));
            float scale = dir.dst(dest) / 10;

            modC.transform.idt();
            modC.transform.translate(new Vector3(dir, 0));
            modC.transform.rotate(new Vector3(0 , 0, 1), new Vector2(dest).sub(src).angleDeg(new Vector2(0, 1)));
            modC.transform.scale(scale, scale, scale);
        }
    }
}
