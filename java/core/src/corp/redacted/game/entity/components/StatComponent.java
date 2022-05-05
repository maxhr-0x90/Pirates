package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.math.Vector2;


/*
 *  Permet de stocker des informations relatives aux bateaux
 */
public class StatComponent implements Component, Poolable {
  public int barreVie = 100; //En poucentage
  public int point = 0;
  public int nombreEquipage;
  public int dernierTir = 0;

  @Override
  public void reset(){
      barreVie = 100;
      point = 0;
      dernierTir = 0;
  }

}
