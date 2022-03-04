package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


/*
 *  Permet de stocker les informations relatives aux déplacement (translation, rotation etc).
 */
public class TransformComponent implements Component {
    public final Vector3 position = new Vector3();
    public final Vector2 scale = new Vector2(1.0f, 1.0f);
    public float rotation = 0.0f;

    @Override
	  public void reset() {
	      rotation = 0.0f;
	    }
}
