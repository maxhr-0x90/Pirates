package corp.redacted.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import corp.redacted.game.entity.components.ModelComponent;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<ModelComponent> modelMap;

    public ZComparator(){
        modelMap = ComponentMapper.getFor(ModelComponent.class);
    }

    @Override
    public int compare(Entity entity, Entity t1) {
        return 0;
    }
}
