package corp.redacted.game.entity.systems;

import corp.redacted.game.entity.components.CollisionComponent;
import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.components.TypeComponent;
import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.IConfig;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.MathUtils;


public class CollisionSystem extends IteratingSystem{
  ComponentMapper<CollisionComponent> colM;
  ComponentMapper<StatComponent> boatM;
  ComponentMapper<BodyComponent> bodyMap;

	public CollisionSystem() {
		super(Family.all(CollisionComponent.class).get());
		this.colM = ComponentMapper.getFor(CollisionComponent.class);
		this.boatM = ComponentMapper.getFor(StatComponent.class);
    this.bodyMap = ComponentMapper.getFor(BodyComponent.class);
	}

  @Override
  protected void processEntity(Entity entite, float temps){
    CollisionComponent colC = colM.get(entite); //On recupere une collision
    Entity entiteEnCollision = colC.collisionEntite; //On recupere l'entite en collision

    TypeComponent typeEntite = entite.getComponent(TypeComponent.class); // On recupere son type

    /*Consernant les collisions des joueurs (bateau)*/
    if(typeEntite.type == TypeComponent.BATEAU_A){
      BodyComponent bodyA = bodyMap.get(entite);
      StatComponent boat = boatM.get(entite);

      if(entiteEnCollision != null){
        TypeComponent typeEnCollsion = entiteEnCollision.getComponent(TypeComponent.class);
        if(typeEnCollsion != null){
          switch(typeEnCollsion.type){
            case TypeComponent.BATEAU_B :
            //On applique une force sur les bateaux.
            BodyComponent bodyB= bodyMap.get(entiteEnCollision);

            boat.barreVie -= IConfig.DEGAT_B_B; //Degat de la collision

            colC.collisionEntite = null;
            break;
            case TypeComponent.MARCHANDISE:
            //On recupere la marchandise
            //La marchandise disparait de la map
            // Mise à jour des scores
            // Manche finie

            break;
            case TypeComponent.BOULET:
            //Mise à jour de la bar de vie
            //Verif si pas de morts
            default:
            break;
          }
        }
      }
    }

    /*Consernant les collisions des joueurs (bateau)*/
    else if(typeEntite.type == TypeComponent.BATEAU_B){
      StatComponent boat = boatM.get(entite);
      BodyComponent bodyB = bodyMap.get(entite);

      if(entiteEnCollision != null){
        TypeComponent typeEnCollsion = entiteEnCollision.getComponent(TypeComponent.class);
        if(typeEnCollsion != null){
          switch(typeEnCollsion.type){
            case TypeComponent.BATEAU_A:

            boat.barreVie -= IConfig.DEGAT_B_B; //Degat de la collision
            colC.collisionEntite = null;
            break;
            case TypeComponent.MARCHANDISE:
            //On recupere la marchandise
            //La marchandise disparait de la map
            // Mise à jour des scores
            // Manche finie
            break;
            case TypeComponent.BOULET:
            //Mise à jour de la bar de vie
            //Verif si pas de morts
            default:
            break;
          }
        }
      }
    }



  }


}
