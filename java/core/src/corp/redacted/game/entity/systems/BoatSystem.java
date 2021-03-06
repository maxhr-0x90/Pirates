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
import java.lang.Math;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
	protected void processEntity(Entity entite, float dt){
		BodyComponent bodyC = bodyMap.get(entite);
    StatComponent boat = statMap.get(entite);
    TypeComponent typeC = typeMap.get(entite);
    int i, r, k;

    if(typeC.type == TypeComponent.BATEAU_A){

      //Gestion des mouvements
      mouvBoat(bodyC, Task.nbLeftR, Task.nbRightR);

      //Gestion des tirs
      if(Task.nbShotLeftR != 0){
        r  = Task.nbShotLeftR%3;
        k = Task.nbShotLeftR/3;
        Task.nbShotLeftR = 0;
        for(i = 0; i<r ;i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, LEFT_SHOT,false);
        }
        for(i = 0; i<k; i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, LEFT_SHOT,true);
        }
      }

      if(Task.nbShotRightR != 0){
         r  = Task.nbShotRightR%3;
         k = Task.nbShotRightR/3;
         Task.nbShotRightR = 0;
        for(i = 0; i<r ;i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, RIGHT_SHOT,false);
        }
        for(i = 0; i<k; i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, RIGHT_SHOT,true);
        }
      }
    }

    if(typeC.type == TypeComponent.BATEAU_B){
      //Gestion des mouvements
      mouvBoat(bodyC, Task.nbLeftB, Task.nbRightB);

      //Gestion des tirs
      if(Task.nbShotLeftB != 0){
         r  = Task.nbShotLeftB%3;
         k = Task.nbShotLeftB/3;
         Task.nbShotLeftB = 0;
        for(i = 0; i<r ;i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_B, LEFT_SHOT,false);
        }
        for(i = 0; i<k; i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_B, LEFT_SHOT,true);
        }
      }

      if(Task.nbShotRightB != 0){
         r  = Task.nbShotRightB%3;
         k = Task.nbShotRightB/3;
         Task.nbShotRightB = 0;
        for(i = 0; i<r ;i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_B, RIGHT_SHOT,false);
        }
        for(i = 0; i<k; i++){
          shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_B, RIGHT_SHOT,true);
        }
      }
    }


    /* CONTROLE CLAVIER/SOURIS*/
    if(typeC.type == TypeComponent.BATEAU_A){


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
        }
        if(!controller.upB && !controller.downB){
          // bodyC.body.setLinearVelocity(0,0);
      }
    }
    if(controller.isMouseDown){ //Si le bouton de souris est appuyé
      if(typeC.type == TypeComponent.BATEAU_A){
        shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, LEFT_SHOT,true);
        shotBoat(boat, bodyC, typeC, CannonballComponent.BATEAU_A, RIGHT_SHOT,true);
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
  * @param bodyC : le corps en question
  */
  private void pousseDroite(BodyComponent bodyC){
    bodyC.body.setAngularVelocity(-IConfig.MISE_A_NIVEAU/250000);
  }

  /** Change l'angle de velocité d'un corps vers la gauche
  * @param bodyC : le corps en question
  */
  private void pousseGauche(BodyComponent bodyC){
    bodyC.body.setAngularVelocity(IConfig.MISE_A_NIVEAU/250000);
  }

  /** Change la velocité linéaire d'un corps
  * @param bodyC : le corps en question
  */
  private void pousseHaut(BodyComponent bodyC){
    float mainM = mainMeasure(bodyC.body.getAngle());
    float velocity = 70f;

    float velX = MathUtils.cos(mainM)*velocity;
    float velY = MathUtils.sin(mainM)*velocity;

    Vector2 vel = new Vector2(velX, velY);
    vel.rotate90(1);

    bodyC.body.setLinearVelocity(vel.x, vel.y);
    }

  /** Change la velocité linéaire d'un corps
  * @param bodyC : le corps en question
  */
  private void pousseBas(BodyComponent bodyC){
    pousseHaut(bodyC);
  }


  /** Change la velocité linéaire d'un corps
  * @param bodyC : le corps en question
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
     bodyC.body.setAngularVelocity(IConfig.MISE_A_NIVEAU/500000);
      // bodyC.body.applyTorque(-angle/100, true);
    }else if(diff < 0){
      bodyC.body.setAngularVelocity(-IConfig.MISE_A_NIVEAU/500000);
      // bodyC.body.setAngularVelocity(angle);
    }else{
      bodyC.body.setAngularVelocity(0);
    }
  }


  /** Permet le tir des bateaux
  * @param boat : le bateau en question
  * @param bodyC : son body
  * @param typeC : son type
  * @param camps : son camps (A ou B)
  * @param side : de quel côté me tir est effectué
  * @param bonus : true si tir à 3 canon, false si tir à un canon
  */
  private void shotBoat(StatComponent boat, BodyComponent bodyC, TypeComponent typeC, int camps, int side, boolean bonus){

      float mainM = mainMeasure(bodyC.body.getAngle());
      float velocity = 50f ; /// VITESSE A DETERMINER

      /*On détermine la direction dans laquelle aller*/
      float velY = MathUtils.sin(mainM)*velocity;
      float velX = MathUtils.cos(mainM)*velocity;

      Vector2 vel = new Vector2(velX, velY);

      if(side == LEFT_SHOT){
        vel.rotate(180f);
      }

      if(bonus){
        Vector2 vel2 = new Vector2(vel);
        vel2.rotate(90);
        System.out.println(vel2);
        /*On place le boulet de cannon*/
        BodyComponent bodyCB = this.world.createCannonball(bodyC.body.getWorldCenter(), camps);
        bodyCB.body.setLinearVelocity(vel.x, vel.y);

        Vector2 posCB2 = new Vector2( (bodyC.body.getWorldCenter().x + 0.15f * vel2.x) , (bodyC.body.getWorldCenter().y + 0.15f * vel2.y));
        BodyComponent bodyCB2 = this.world.createCannonball(posCB2, camps);
        bodyCB2.body.setLinearVelocity(vel.x, vel.y);

        Vector2 posCB3 = new Vector2( (bodyC.body.getWorldCenter().x - 0.15f * vel2.x) , (bodyC.body.getWorldCenter().y - 0.15f * vel2.y));
        BodyComponent bodyCB3 = this.world.createCannonball(posCB3, camps);
        bodyCB3.body.setLinearVelocity(vel.x, vel.y);
      }else{
        /*On place le boulet de cannon*/
        BodyComponent bodyCB = this.world.createCannonball(bodyC.body.getWorldCenter(), camps);
        bodyCB.body.setLinearVelocity(vel.x, vel.y);
      }


    }


}
