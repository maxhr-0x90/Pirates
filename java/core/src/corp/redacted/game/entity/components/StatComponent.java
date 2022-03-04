package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;


/*
 *  Permet de stocker des informations relatives aux bateaux
 */
public class StatComponent implements Component{
  public int barreVie = 100; //En poucentage
  public int point = 0;
  public int nombreEquipage;

  @Override
  public void reset(){
      barreVie = 100;
      point = 0;
  }

}
