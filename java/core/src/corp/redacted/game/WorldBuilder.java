package corp.redacted.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Permet la mise en place des entités dans le jeu
 */
public class WorldBuilder {
    private Engine engine;
    private World world;

    public WorldBuilder(Engine engine){
        world = new World(new Vector2(0, 0), true);
        this.engine = engine;
    }

    public void generateWorld(){
        // TODO créer une génération de monde
    }

    public World getWorld() {
        return world;
    }
}
