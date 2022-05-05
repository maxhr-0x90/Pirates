package corp.redacted.game.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsDebugSystem extends IteratingSystem {
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private Camera cam;

    public PhysicsDebugSystem(World world, Camera cam){
        super(Family.all().get());

        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.cam = cam;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        debugRenderer.render(world, cam.combined);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {}
}
