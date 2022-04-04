package corp.redacted.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;

import com.badlogic.gdx.utils.Array;
import corp.redacted.game.entity.components.*;
import corp.redacted.game.loader.Assets;

/**
 * Permet la mise en place des entités dans le jeu
 */
public class WorldBuilder {
    private Engine engine;
    private World world;
    public int niveauCourant = 0;
    public Entity bateauA;
    public Entity bateauB;

    private Assets assets;

    public WorldBuilder(Engine engine, Assets assets){
        world = new World(new Vector2(-10, -10), true);
        this.engine = engine;
        world.setContactListener(new MyContactListener());
        this.assets = assets;

        assets.queueAdd3DModels();
        assets.manager.finishLoading();
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

        Entity marchandise = creeMarchandise(-20,20,10,5, 5);
        engine.addEntity(marchandise);
    }

    /** Renvoie une entité bateau
    * @param posx : position initiale sur l'axe des x
    * @param posy : position initiale sur l'axe des y
    * @param camps : donne son nom d'équipe('A', ..)
    */
    public Entity creeBateau(int posx, int posy, char camps){
      Entity bateau = new Entity(); //Création de l'entité
      StatComponent bateauC = new StatComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();
      ModelComponent modelC = new ModelComponent();

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
      fixDef.restitution = 1f;


      /* Assignation du type/categorie */
      if(camps == 'A'){
        typeC.type = TypeComponent.BATEAU_A;
      }else if(camps == 'B'){
        typeC.type = TypeComponent.BATEAU_B;
      }

      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.


      bodyC.body.setUserData(bateau);

      Model boat = assets.manager.get(assets.cubeModel, Model.class);
      ModelInstance boatInstance = new ModelInstance(boat);

      modelC.model = boatInstance;

      /* On ajoute les components à l'entité */
      bateau.add(bateauC);
      bateau.add(bodyC);
      bateau.add(typeC);
      bateau.add(colC);
      bateau.add(modelC);

      return bateau;
    }

    /** Renvoie une entité marchandise
    * @param posx : position initiale sur l'axe des x
    * @param posy : position initiale sur l'axe des y
    * @param taillex : taille sur l'axe Ox de la marchandise
    * @param tailley : taille sur l'axe Oy de la marchandise
    */
    public Entity creeMarchandise(int posx, int posy, float taillex, float tailley, float weight){
      Entity merchendise = new Entity(); //Création de l'entité
      MerchendiseComponent merchendiseC = new MerchendiseComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();


      /*Définition de ses caractéristique*/
      merchendiseC.weight = weight;

      if(weight < MerchendiseComponent.LIMIT_LITTLE_M){
        merchendiseC.merchendiseType = MerchendiseComponent.LITTLE_MERCHENDISE;
      }else if(weight < MerchendiseComponent.LIMIT_CLASSIC_M){
        merchendiseC.merchendiseType = MerchendiseComponent.CLASSIC_MERCHENDISE;
      }else{
        merchendiseC.merchendiseType = MerchendiseComponent.BIG_MERCHENDISE ;
      }


      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.StaticBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      PolygonShape poly = new PolygonShape();
      poly.setAsBox(taillex, tailley);

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_MARCHANDISE;
      fixDef.friction = IConfig.FRICTION_MARCHANDISE;
      fixDef.restitution = 0f;
      fixDef.shape = poly;

      /* Assignation du type/categorie */
      typeC.type = TypeComponent.MARCHANDISE;
      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.

      bodyC.body.setUserData(merchendise);

      /*On ajoute les components à l'entité*/
      merchendise.add(merchendiseC);
      merchendise.add(bodyC);
      merchendise.add(typeC);
      merchendise.add(colC);

      return merchendise;
    }


    /** Crée et place une entité boulet de canon
    * @param pos : vecteur de position d'arrivé.
    */
    public void createCannonball(Vector2 pos){
      Entity cannonball = new Entity(); //Création de l'entité
      MerchendiseComponent cannonballC = new MerchendiseComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();


      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.StaticBody;
      bodyD.position.x = pos.x/10; //A VOIR AVEC SOAM
      bodyD.position.y = pos.y/10;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      PolygonShape poly = new PolygonShape();
      poly.setAsBox(IConfig.TAILLE_CANNONBALL, IConfig.TAILLE_CANNONBALL);

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_CANNONBALL;
      fixDef.friction = IConfig.FRICTION_CANNONBALL;
      fixDef.restitution = 0f;
      fixDef.shape = poly;

      /* Assignation du type/categorie */
      typeC.type = TypeComponent.CANNONBALL;
      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.

      bodyC.body.setUserData(cannonball);

      /*On ajoute les components à l'entité*/
      cannonball.add(cannonballC);
      cannonball.add(bodyC);
      cannonball.add(typeC);
      cannonball.add(colC);

      engine.addEntity(cannonball);
    }

    public World getWorld() {
        return world;
    }

    /** Supprime une entité
    */
    public void removeEntite(Entity ent){
      engine.removeEntity(ent);
    }

}
