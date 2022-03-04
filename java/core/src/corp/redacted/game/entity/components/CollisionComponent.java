package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/*
 *  Permet de stocker des informations relatives aux colissions
 */
public class CollisionComponent implements Component, Poolable {
	public Entity collisionEntite;

	@Override
	public void reset() {
		collisionEntite = null;
	}
}
