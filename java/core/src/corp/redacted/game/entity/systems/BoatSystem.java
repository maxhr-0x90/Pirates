package corp.redacted.game.entity.systems;
import com.badlogic.ashley.core.ComponentMapper;

import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.WorldBuilder;
import corp.redacted.game.controller.KeyboardController;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import corp.redacted.game.IConfig;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BoatSystem extends IteratingSystem{
  private WorldBuilder monde;
  private ComponentMapper<BodyComponent> bodyMap;
  private ComponentMapper<StatComponent> statMap;
  private KeyboardController controller;
  private static final float VITESSE = 50f;

	public BoatSystem(KeyboardController keyControl, WorldBuilder monde) {
		super(Family.all(StatComponent.class).get());
		this.monde = monde;
    this.bodyMap = ComponentMapper.getFor(BodyComponent.class);
    this.statMap = ComponentMapper.getFor(StatComponent.class);
    this.controller = keyControl;
	}

  //Méthode appliqué lors d'update, dt := temps depuis la derniere mise à jour
  @Override
	protected void processEntity(Entity entite, float dt) {
		BodyComponent bodyC = bodyMap.get(entite);
    StatComponent boat = statMap.get(entite);

    /* CONTROLEUR CLAVIER */
    float angle = 0.1f;
    if(controller.left){
      pousseDroite(bodyC);
		}

		if(controller.right){
      pousseGauche(bodyC);
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
      bodyC.body.setLinearVelocity(MathUtils.lerp(bodyC.body.getLinearVelocity().x, 0, 0.1f),bodyC.body.getLinearVelocity().y);
    }
    if(!controller.up && !controller.down){
      bodyC.body.setAngularVelocity(0);
      bodyC.body.setLinearVelocity(bodyC.body.getLinearVelocity().x,MathUtils.lerp(bodyC.body.getLinearVelocity().y, 0f, 0.1f));
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

        //ACTION DE TIR
      }
    }



	}

  private void pousseDroite(BodyComponent bodyC){
    bodyC.body.setLinearVelocity(MathUtils.lerp(bodyC.body.getLinearVelocity().x, -VITESSE, 0.2f),bodyC.body.getLinearVelocity().y);
  }

  private void pousseGauche(BodyComponent bodyC){
    bodyC.body.setLinearVelocity(MathUtils.lerp(bodyC.body.getLinearVelocity().x, VITESSE, 0.2f),bodyC.body.getLinearVelocity().y);
  }

  private void pousseHaut(BodyComponent bodyC){
    bodyC.body.setLinearVelocity(bodyC.body.getLinearVelocity().x,MathUtils.lerp(bodyC.body.getLinearVelocity().y, VITESSE, 0.2f));
  }

  private void pousseBas(BodyComponent bodyC){
    bodyC.body.setLinearVelocity(bodyC.body.getLinearVelocity().x,MathUtils.lerp(bodyC.body.getLinearVelocity().y, -VITESSE, 0.2f));
  }

}
