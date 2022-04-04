package corp.redacted.game.entity.systems;
import com.badlogic.ashley.core.ComponentMapper;

import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.components.TypeComponent;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.controller.KeyboardController;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;

import corp.redacted.game.IConfig;

import corp.redacted.game.serveur.Task;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.lang.Math;
import java.util.ArrayList;

public class BoatSystem extends IteratingSystem{
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

      // mouvBoat(bodyC, Task.nbLeft, Task.nbRight);

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
          bodyC.body.setAngularVelocity(0);
        }
        if(!controller.up && !controller.down){
          bodyC.body.setLinearVelocity(0,0);
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
    if(controller.isMouseDown){ //Si le bouton de souris est appuyé
      if(boat.dernierTir <=0){  //On vérifie si le temps avant le dernier tir est suffisant
        Vector2 posSouris = new Vector2(controller.mouseLocation.x,controller.mouseLocation.y); // On récupère la position de la souris
        boat.dernierTir = IConfig.DELAIS_TIR; //On met à jour le delais de tir.
        this.world.createCannonball(posSouris);
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

  private void pousseDroite(BodyComponent bodyC){
    bodyC.body.applyTorque(IConfig.MISE_A_NIVEAU, true);
  }

  private void pousseGauche(BodyComponent bodyC){
    bodyC.body.applyTorque(-IConfig.MISE_A_NIVEAU, true);
  }

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


    posP.x = (float)Math.cos(mainM+(float)Math.PI/2.0f);
    posP.y = (float)Math.sin(mainM+(float)Math.PI/2.0f);

    // bodyC.body.applyLinearImpulse(IConfig.VITESSE*pos.x,IConfig.VITESSE*pos.y, pos.x, pos.y, true);
  }

  private void pousseBas(BodyComponent bodyC){
    pousseHaut(bodyC);
  }

/*  private void mouvBoat(BodyComponent bodyC, int left, int right){
    Vector2 pos = bodyC.body.getPosition();
    Vector2 posP = new Vector2();
    int diff = left - right;

    if( diff < 0 && diff > -0){
      bodyC.body.applyTorque(0, true);
      bodyC.body.applyLinearImpulse(0,IConfig.VITESSE, pos.x, pos.y, true);

    }else if(diff > 0){
      float mainM = mainMeasure(bodyC.body.getAngle());

      posP.x = (float)Math.cos(mainM+(float)Math.PI/2.0f);
      posP.y = (float)Math.sin(mainM+(float)Math.PI/2.0f);

      bodyC.body.applyTorque(1000000*IConfig.MISE_A_NIVEAU, true);
      bodyC.body.applyLinearImpulse(IConfig.VITESSE*posP.y,IConfig.VITESSE*posP.x, pos.x, pos.y, true);
    }else if(diff < 0){
      float mainM = mainMeasure(bodyC.body.getAngle());

      posP.x = (float)Math.cos(mainM+(float)Math.PI/2.0f);
      posP.y = (float)Math.sin(mainM+(float)Math.PI/2.0f);

      bodyC.body.applyTorque(-1000000*IConfig.MISE_A_NIVEAU, true);
      bodyC.body.applyLinearImpulse(IConfig.VITESSE*posP.y,IConfig.VITESSE*posP.x, pos.x, pos.y, true);
    }
  }
  */

}
