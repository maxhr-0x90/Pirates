package corp.redacted.game.entity.systems;
import com.badlogic.ashley.core.ComponentMapper;

import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.entity.components.CannonballComponent;
import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.components.TypeComponent;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.controller.KeyboardController;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import corp.redacted.game.IConfig;

import corp.redacted.game.serveur.Task;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.lang.Math;
import java.util.ArrayList;

public class BoatSystem extends IteratingSystem{
  private final int LEFT_SHOT = 0;
  private final int RIGHT_SHOT = 1;
  private WorldBuilder world;
  private ComponentMapper<BodyComponent> bodyMap;
  private ComponentMapper<StatComponent> statMap;
  private ComponentMapper<TypeComponent> typeMap;
  private KeyboardController controller;

	public BoatSystem(KeyboardController keyControl, WorldBuilder world) {
		super(Family.all(StatComponent.class).get());
		this.world = world;
    this.bodyMap = ComponentMapper.getFor(BodyComponent.class);
    this.statMap = ComponentMapper.getFor(StatComponent.class);
    this.typeMap = ComponentMapper.getFor(TypeComponent.class);
    this.controller = keyControl;
	}

  //Méthode appliqué lors d'update, dt := temps depuis la derniere mise à jour
  @Override
	protected void processEntity(Entity entite, float dt) {
		BodyComponent bodyC = bodyMap.get(entite);
    StatComponent boat = statMap.get(entite);
    TypeComponent typeC = typeMap.get(entite);

    if(typeC.type == TypeComponent.BATEAU_A){

      mouvBoat(bodyC, Task.nbLeft, Task.nbRight);

      /* CONTROLEUR CLAVIER */

      if(controller.left){
        pousseGauche(bodyC);
		    }

		   if(controller.right){
         pousseDroite(bodyC);
		    }

        if(controller.up){
          pousseHaut(bodyC);
        }

        if(controller.down){
          pousseBas(bodyC);
        }

        //Permet d'arreter le bateau
        if(!controller.left && ! controller.right){
          // bodyC.body.setAngularVelocity(0);
        }
        if(!controller.up && !controller.down){
          // bodyC.body.setLinearVelocity(0,0);
        }

    }else if(typeC.type == TypeComponent.BATEAU_B){
      /* CONTROLEUR CLAVIER */
      if(controller.leftB){
        pousseGauche(bodyC);
		    }

		   if(controller.rightB){
         pousseDroite(bodyC);
		    }

        if(controller.upB){
          pousseHaut(bodyC);
        }

        if(controller.downB){
          pousseBas(bodyC);
        }

        //Permet d'arreter le bateau
        if(!controller.leftB && ! controller.rightB){
          // bodyC.body.setAngularVelocity(0);
          // bodyC.body.setLinearVelocity(0,0);
        }
        if(!controller.upB && !controller.downB){
          // bodyC.body.setAngularVelocity(0);
          // bodyC.body.setLinearVelocity(0,0);
      }
    }

    //Gestion du temps entre 2 tires
    if(boat.dernierTir > 0){
      boat.dernierTir -= dt;
    }

    //Gestion quant au tire
    if(typeC.type == TypeComponent.BATEAU_A){
      if(Task.nbShotLeft != 0){
        shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, LEFT_SHOT);
      }

      if(Task.nbShotRight != 0){
        shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, RIGHT_SHOT);
      }

    }
    if(controller.isMouseDown){ //Si le bouton de souris est appuyé

      if(typeC.type == TypeComponent.BATEAU_A){
        shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, LEFT_SHOT);
        shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, RIGHT_SHOT);


      }

    }
	}

  /** Renvoie la mesure principale d'un angle donnée
  * @param angle en question
  * @return angle entre 0 et 2pi
  */
  private float mainMeasure(float angle){
    float mainM;
    int k;
    k = Math.round(((float)Math.PI - angle)/(2.0f*(float)Math.PI));
    mainM = angle + (float)k * 2.0f *(float)Math.PI;
    return mainM;
  }

  /** Change l'angle de velocité d'un corps vers la droite
  * @param BodyComponent bodyC : le corps en question
  */
  private void pousseDroite(BodyComponent bodyC){
    bodyC.body.setAngularVelocity(-IConfig.MISE_A_NIVEAU/100000);
    // bodyC.body.applyTorque(IConfig.MISE_A_NIVEAU, true);
  }

  /** Change l'angle de velocité d'un corps vers la gauche
  * @param BodyComponent bodyC : le corps en question
  */
  private void pousseGauche(BodyComponent bodyC){
    bodyC.body.setAngularVelocity(IConfig.MISE_A_NIVEAU/100000);
    // bodyC.body.applyTorque(-IConfig.MISE_A_NIVEAU, true);
  }

  /** Change la velocité linéaire d'un corps
  * @param BodyComponent bodyC : le corps en question
  */
  private void pousseHaut(BodyComponent bodyC){
    Vector2 pos = bodyC.body.getPosition();
    Vector2 posP = new Vector2();
    float mainM = mainMeasure(bodyC.body.getAngle());
    float velocity = 9000f;

    float velX = MathUtils.cos(mainM)*velocity;
    float velY = MathUtils.sin(mainM)*velocity;

    Vector2 vel = new Vector2(velX, velY);
    vel.rotate90(1);

    bodyC.body.setLinearVelocity(vel.x, vel.y);
    }

  /** Change la velocité linéaire d'un corps
  * @param BodyComponent bodyC : le corps en question
  */
  private void pousseBas(BodyComponent bodyC){
    pousseHaut(bodyC);
  }


  /** Change la velocité linéaire d'un corps
  * @param BodyComponent bodyC : le corps en question
  * @param left : le nombre de clic gauche
  * @param right : le npmbre de clique droit
  */
  private void mouvBoat(BodyComponent bodyC, int left, int right){
    int diff = left - right;

    Vector2 pos = bodyC.body.getPosition();
    Vector2 posP = new Vector2();
    float mainM = mainMeasure(bodyC.body.getAngle());
    float velocity = (left+right+1)*10f ; /// VITESSE A DETERMINER

    /*On détermine la direction dans laquelle aller*/
    float velY = MathUtils.sin(mainM)*velocity;
    float velX = MathUtils.cos(mainM)*velocity;

    Vector2 vel = new Vector2(velX, velY);
    vel.rotate90(1);

    /*On défini l'angle pour la rotation*/
    float angle = MathUtils.acos(diff);

    bodyC.body.setLinearVelocity(vel.x, vel.y);
   if(diff > 0){
     bodyC.body.setAngularVelocity(-angle);
     bodyC.body.setAngularVelocity(IConfig.MISE_A_NIVEAU/1000000);
      // bodyC.body.applyTorque(-angle/100, true);
    }else if(diff < 0){
      bodyC.body.setAngularVelocity(-IConfig.MISE_A_NIVEAU/1000000);
      // bodyC.body.setAngularVelocity(angle);
    }else{
      bodyC.body.setAngularVelocity(0);
    }
  }

  private void shotBoat(StatComponent boat, BodyComponent bodyC, TypeComponent typeC, int camps, int side){

    if(boat.dernierTir <=0){  //On vérifie si le temps avant le dernier tir est suffisant
      float mainM = mainMeasure(bodyC.body.getAngle());
      float velocity = 50f ; /// VITESSE A DETERMINER

      /*On détermine la direction dans laquelle aller*/
      float velY = MathUtils.sin(mainM)*velocity;
      float velX = MathUtils.cos(mainM)*velocity;

      Vector2 vel = new Vector2(velX, velY);

      if(side == LEFT_SHOT){
        vel.rotate(180f);
      }

      boat.dernierTir = IConfig.DELAIS_TIR; //On met à jour le delais de tir.
      /*On place le boulet de cannon*/
      BodyComponent bodyCB = this.world.createCannonball(bodyC.body.getPosition(), camps);
      bodyCB.body.setLinearVelocity(vel.x, vel.y);

    }
  }

}
