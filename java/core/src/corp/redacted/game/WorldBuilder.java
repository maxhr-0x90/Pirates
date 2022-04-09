package corp.redacted.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

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
        world = new World(new Vector2(0, 0), true);
        this.engine = engine;
        world.setContactListener(new MyContactListener());
        this.assets = assets;

        assets.queueAdd3DModels();
        assets.manager.finishLoading();
    }

    /** Genère un monde
    */
    public void generateWorld(){
        Entity batA = creeBateau(0,50,'A');
        this.bateauA = batA;
        engine.addEntity(bateauA);

        Entity batB = creeBateau(-50,-50,'B');
        this.bateauB = batB;
        engine.addEntity(bateauB);

        Entity marchandise = creeMarchandise(-20,20,10,5, 5);
        engine.addEntity(marchandise);

        createOcean();
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

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.DynamicBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      System.out.println(bodyC.body.getPosition());
      /* Création de l'enveloppe du bateau */
      Vector2[] vect = new Vector2[5];
      PolygonShape poly = new PolygonShape();
      vect[0] = new Vector2(posx-IConfig.LARGEUR_BATEAU/2, posy - IConfig.LONGUEUR_BATEAU/2);
      vect[1] =  new Vector2(posx + IConfig.LARGEUR_BATEAU/2, posy - IConfig.LONGUEUR_BATEAU/2);

      vect[2] = new Vector2(posx+ IConfig.LARGEUR_BATEAU/2, posy + IConfig.LONGUEUR_BATEAU/5);
      vect[3] = new Vector2(posx, posy + IConfig.LONGUEUR_BATEAU/2);
      vect[4] = new Vector2(posx - IConfig.LARGEUR_BATEAU/2, posy + IConfig.LONGUEUR_BATEAU/5);
      /*
      vect[0] = new Vector2(0, 0);
      vect[1] =  new Vector2(IConfig.LARGEUR_BATEAU,0);
      vect[3] = new Vector2(IConfig.LARGEUR_BATEAU, 4*IConfig.LONGUEUR_BATEAU/5);
      vect[2] = new Vector2(IConfig.LARGEUR_BATEAU/2, IConfig.LONGUEUR_BATEAU);
      vect[4] = new Vector2(0,4*IConfig.LONGUEUR_BATEAU/5);
      */

      poly.set(vect);

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_BATEAU;
      fixDef.friction = IConfig.FRICTION_BATEAU;
      fixDef.shape = poly;
      fixDef.restitution = 1f;


      /* Assignation du type/categorie */
      if(camps == 'A'){
        fixDef.filter.categoryBits = CollisionComponent.CATEGORY_BOAT_A;
        fixDef.filter.maskBits = CollisionComponent.MASK_BOAT_A;
        typeC.type = TypeComponent.BATEAU_A;
      }else if(camps == 'B'){
        fixDef.filter.categoryBits = CollisionComponent.CATEGORY_BOAT_B;
        fixDef.filter.maskBits = CollisionComponent.MASK_BOAT_B;
        typeC.type = TypeComponent.BATEAU_B;
      }

      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.


      bodyC.body.setUserData(bateau);


      /* On ajoute les components à l'entité */
      bateau.add(bateauC);
      bateau.add(bodyC);
      bateau.add(typeC);
      bateau.add(colC);

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
      fixDef.filter.categoryBits = CollisionComponent.CATEGORY_MERCHENDISE;
      fixDef.filter.maskBits = CollisionComponent.MASK_MERCHENDISE;

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
    public BodyComponent createCannonball(Vector2 pos, int camps){
      Entity cannonball = new Entity(); //Création de l'entité
      CannonballComponent cannonballC = new CannonballComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();

      cannonballC.camps = camps;
      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.DynamicBody;
      bodyD.position.x = pos.x; //A VOIR AVEC SOAM
      bodyD.position.y = pos.y;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      CircleShape circle = new CircleShape();
      circle.setRadius(IConfig.TAILLE_CANNONBALL);
      circle.setPosition(new Vector2(0,0));

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_CANNONBALL;
      fixDef.friction = IConfig.FRICTION_CANNONBALL;
      fixDef.restitution = 0f;
      fixDef.shape = circle;
      if(camps == CannonballComponent.BATEAU_A){
        fixDef.filter.categoryBits = CollisionComponent.CATEGORY_CANNONBAL_A;
        fixDef.filter.maskBits = CollisionComponent.MASK_CANNONBAL_A;
      }else if(camps == CannonballComponent.BATEAU_B){
        fixDef.filter.categoryBits = CollisionComponent.CATEGORY_CANNONBAL_B;
        fixDef.filter.maskBits = CollisionComponent.MASK_CANNONBAL_B;
      }

      /* Assignation du type/categorie */
      typeC.type = TypeComponent.CANNONBALL;
      bodyC.body.createFixture(fixDef);
      circle.dispose(); //On libère l'enveloppe.

      bodyC.body.setUserData(cannonball);

      /*On ajoute les components à l'entité*/
      cannonball.add(cannonballC);
      cannonball.add(bodyC);
      cannonball.add(typeC);
      cannonball.add(colC);

      engine.addEntity(cannonball);

      return bodyC;
    }

    /** Crée et place une entité correspondant à l'océan
    */
    public void createOcean(){
      Entity ocean = new Entity(); //Création de l'entité
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.StaticBody;
      bodyD.position.x = 0;
      bodyD.position.y = 0;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      Vector2[] vect = new Vector2[5];
      ChainShape chain = new ChainShape();
      vect[0] =  new Vector2(-IConfig.LARGEUR_CARTE/2, -IConfig.HAUTEUR_CARTE/2);
      vect[1] = new Vector2(IConfig.LARGEUR_CARTE/2, -IConfig.HAUTEUR_CARTE/2);
      vect[2] =  new Vector2(IConfig.LARGEUR_CARTE/2, IConfig.HAUTEUR_CARTE/2);
      vect[3] =   new Vector2(-IConfig.LARGEUR_CARTE/2, IConfig.HAUTEUR_CARTE/2);
      vect[4] =  new Vector2(-IConfig.LARGEUR_CARTE/2, -IConfig.HAUTEUR_CARTE/2);

      chain.createChain(vect);

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_EAU;
      fixDef.friction = 0;
      fixDef.shape = chain;
      fixDef.restitution = 1f;
      fixDef.filter.categoryBits = CollisionComponent.CATEGORY_OCEAN;
      fixDef.filter.maskBits = CollisionComponent.MASK_OCEAN;

      bodyC.body.createFixture(fixDef);
      chain.dispose(); //On libère l'enveloppe.

      bodyC.body.setUserData(ocean);


      typeC.type = TypeComponent.OCEAN;

      /* On ajoute les components à l'entité */
      ocean.add(bodyC);
      ocean.add(typeC);
      ocean.add(colC);

      engine.addEntity(ocean);
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
