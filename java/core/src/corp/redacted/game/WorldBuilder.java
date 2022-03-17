package corp.redacted.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import corp.redacted.game.entity.components.StatComponent;
import corp.redacted.game.entity.components.BodyComponent;

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

    }

    /** Genère un monde
    */
    public void generateWorld(){
        Entity batA = creeBateau(0,0);
        this.bateauA = batA;
        engine.addEntity(bateauA);
    }

    /** Renvoie une entité bateau
    * @param int posx : position initiale sur l'axe des x
    * @param int posy : position initiale sur l'axe des y
    */
    public Entity creeBateau(int posx, int posy){
      Entity bateau = new Entity(); //Création de l'entité
      StatComponent bateauC = new StatComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      PolygonShape poly = new PolygonShape();
      FixtureDef fixDef = new FixtureDef();

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.DynamicBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      poly.setAsBox(IConfig.LONGUEUR_BATEAU, IConfig.LARGEUR_BATEAU);

      /* Création de la fixture */
      fixDef.density = IConfig.DENSITE_BATEAU;
      fixDef.friction = IConfig.FRICTION_BATEAU;
      fixDef.shape = poly;

      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.

      /* On ajoute les components à l'entité */
      bateau.add(bateauC);
      bateau.add(bodyC);

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
