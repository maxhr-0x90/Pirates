 package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

 /*
* Stocke le type de l'entité
*/
public class TypeComponent implements Component, Poolable {
 public static final int BATEAU_A = 1;
 public static final int BATEAU_B = 2;
 public static final int MARCHANDISE = 3;
 public static final int BOULET = 4;
 public static final int OTHER = 5;

 public int type = OTHER;

 @Override
 public void reset(){
   type = OTHER;
 }

 @Override
 public String toString(){
   return ""+type;
 }

}
