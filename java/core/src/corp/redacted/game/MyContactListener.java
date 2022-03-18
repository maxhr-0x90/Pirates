package corp.redacted.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.math.MathUtils;

import corp.redacted.game.entity.components.CollisionComponent;
import corp.redacted.game.entity.components.TypeComponent;
import corp.redacted.game.entity.components.BodyComponent;

public class MyContactListener implements ContactListener{
  public MyContactListener(){



  }

  @Override
  public void beginContact(Contact contact){
		  Fixture fa = contact.getFixtureA();
		  Fixture fb = contact.getFixtureB();

      System.out.println("Contact");
			Entity ent = (Entity) fa.getBody().getUserData();
      Entity colEnt = (Entity) fb.getBody().getUserData();

      CollisionComponent col = ent.getComponent(CollisionComponent.class);
      CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);


				col.collisionEntite = colEnt;
				colb.collisionEntite = ent;




  }

  @Override
	public void endContact(Contact contact){
    Fixture fa = contact.getFixtureA();
    Fixture fb = contact.getFixtureB();

    Entity ent = (Entity) fa.getBody().getUserData();
    Entity entB = (Entity) fb.getBody().getUserData();

    CollisionComponent col = ent.getComponent(CollisionComponent.class);
    CollisionComponent colb = entB.getComponent(CollisionComponent.class);

    col = null;
    colb = null;

    TypeComponent typeA = ent.getComponent(TypeComponent.class); // On recupere son type
    TypeComponent typeB = ent.getComponent(TypeComponent.class); // On recupere son type

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold){
	}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse){
	}


}
