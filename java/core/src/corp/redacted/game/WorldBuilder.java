package corp.redacted.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.components.BodyComponent;
import corp.redacted.game.entity.components.TypeComponent;
import corp.redacted.game.entity.components.CollisionComponent;

/**
 * Permet la mise en place des entités dans le jeu
 */
public class WorldBuilder {
    private Engine engine;
    private World world;
    public int niveauCourant = 0;
    public Entity bateauA;
    public Entity bateauB;

    public WorldBuilder(Engine engine){
        world = new World(new Vector2(0, 0), true);
        this.engine = engine;
        world.setContactListener(new MyContactListener());

    }

    /** Genère un monde
    */
    public void generateWorld(){
        Entity batA = creeBateau(-10,-10,'A');
        this.bateauA = batA;
        engine.addEntity(bateauA);

        Entity batB = creeBateau(7,7,'B');
        this.bateauB = batB;
        engine.addEntity(bateauB);
    }

    /** Renvoie une entité bateau
    * @param int posx : position initiale sur l'axe des x
    * @param int posy : position initiale sur l'axe des y
    */
    public Entity creeBateau(int posx, int posy, char camps){
      Entity bateau = new Entity(); //Création de l'entité
      StatComponent bateauC = new StatComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.DynamicBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      Vector2[] vect = new Vector2[5];
      PolygonShape poly = new PolygonShape();
      vect[0] = new Vector2(posx, posy);
      vect[1] = new Vector2(posx+IConfig.LARGEUR_BATEAU,posy);
      vect[2] = new Vector2(posx+IConfig.LARGEUR_BATEAU, posy+4*IConfig.LONGUEUR_BATEAU/5);
      vect[3] =  new Vector2(posx+IConfig.LARGEUR_BATEAU/2, posy+IConfig.LONGUEUR_BATEAU);
      vect[4] = new Vector2(posx, posy+4*IConfig.LONGUEUR_BATEAU/5);

      poly.set(vect);

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_BATEAU;
      fixDef.friction = IConfig.FRICTION_BATEAU;
      fixDef.shape = poly;
      fixDef.restitution = 0;

      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.


      /* Assignation du type */
      if(camps == 'A'){
        typeC.type = TypeComponent.BATEAU_A;
      }else if(camps == 'B'){
        typeC.type = TypeComponent.BATEAU_B;
      }


      //On précise les capteurs pour les mouvements.
		  for(Fixture fix : bodyC.body.getFixtureList()){
			   fix.setSensor(true);
      }

      bodyC.body.setUserData(bateau);



      /* On ajoute les components à l'entité */
      bateau.add(bateauC);
      bateau.add(bodyC);
      bateau.add(typeC);
      bateau.add(colC);

      return bateau;
    }



    public World getWorld() {
        return world;
    }

    /** Supprime une entité
    */
    public void supprimerEntite(Entity ent){
      engine.removeEntity(ent);
    }

}
