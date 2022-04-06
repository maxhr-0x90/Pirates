package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/*
 *  Permet de stocker des informations relatives aux colissions
 */
public class CollisionComponent implements Component, Poolable {
	public Entity collisionEntite;
	public static final short CATEGORY_BOAT_A = 0x0001;
	public static final short CATEGORY_BOAT_B = 0x0002;
	public static final short CATEGORY_MERCHENDISE = 0x0004;
	public static final short CATEGORY_CANNONBAL_A = 0x0008;
	public static final short CATEGORY_CANNONBAL_B = 0x0010;
	public static final short CATEGORY_OCEAN = 0x0020;


	public static final int MASK_BOAT_A = CATEGORY_BOAT_B | CATEGORY_CANNONBAL_B | CATEGORY_MERCHENDISE | CATEGORY_OCEAN;
	public static final int MASK_BOAT_B = CATEGORY_BOAT_A | CATEGORY_CANNONBAL_A | CATEGORY_MERCHENDISE | CATEGORY_OCEAN;
	public static final int MASK_MERCHENDISE = CATEGORY_BOAT_A | CATEGORY_BOAT_B;
	public static final int MASK_CANNONBAL_A = CATEGORY_BOAT_B;
	public static final int MASK_CANNONBAL_B = CATEGORY_BOAT_A;
	public static final int MASK_OCEAN = CATEGORY_BOAT_A | CATEGORY_BOAT_B | CATEGORY_MERCHENDISE;


	@Override
	public void reset() {
		collisionEntite = null;
	}
}
