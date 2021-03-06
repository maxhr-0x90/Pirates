package corp.redacted.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import corp.redacted.game.entity.components.*;
import corp.redacted.game.loader.Assets;
import com.badlogic.gdx.math.MathUtils;
import corp.redacted.game.model.ModelGenerator;
import corp.redacted.game.serveur.Socket;

import java.lang.Math;

/**
 * Permet la mise en place des entités dans le jeu
 */
public class WorldBuilder {
    private Engine engine;
    private World world;
    public int niveauCourant = 0;

    public Entity bateauA, bateauB, ocean, firstMerch;

    public Entity flecheA;
    public Entity flecheB;

    private Assets assets;

    public WorldBuilder(Engine engine, Assets assets, ParticleBatch<?> batch){
        world = new World(new Vector2(0, 0), true);
        this.engine = engine;
        world.setContactListener(new MyContactListener());
        this.assets = assets;

        assets.queueAdd3DModels();
        assets.queueAddTextures();
        Array<ParticleBatch<?>> singleton = new Array<>();
        singleton.add(batch);
        assets.queueAddPFX(singleton);
        assets.manager.finishLoading();
    }

    /** Genère un monde
    */
    public void generateWorld(){
        Entity batA = creeBateau(-180,-110,'A');
        this.bateauA = batA;
        engine.addEntity(bateauA);

        Entity batB = creeBateau(180,110,'B');
        this.bateauB = batB;
        engine.addEntity(bateauB);

        createArrows();
        creeMarchandise(0,0);
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
      ModelComponent modC = new ModelComponent();

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.DynamicBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      Vector2[] vect = new Vector2[5];
      PolygonShape poly = new PolygonShape();
      vect[0] = new Vector2(-IConfig.LARGEUR_BATEAU/2, -IConfig.LONGUEUR_BATEAU/2);
      vect[1] =  new Vector2(IConfig.LARGEUR_BATEAU/2, -IConfig.LONGUEUR_BATEAU/2);

      vect[2] = new Vector2(IConfig.LARGEUR_BATEAU/2, 8*IConfig.LONGUEUR_BATEAU/10);
      vect[3] = new Vector2(0, IConfig.LONGUEUR_BATEAU);
      vect[4] = new Vector2(- IConfig.LARGEUR_BATEAU/2,  8*IConfig.LONGUEUR_BATEAU/10);


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

        /*On tourne le bateau*/
        bodyC.body.setTransform(bodyC.body.getPosition(), (float)(-Math.PI/3.0f));


      }else if(camps == 'B'){
        fixDef.filter.categoryBits = CollisionComponent.CATEGORY_BOAT_B;
        fixDef.filter.maskBits = CollisionComponent.MASK_BOAT_B;
        typeC.type = TypeComponent.BATEAU_B;

        /*On tourne le bateau*/
        bodyC.body.setTransform(bodyC.body.getPosition(), (float)(2*Math.PI/3.0f));
      }

      bodyC.body.createFixture(fixDef);
      poly.dispose(); //On libère l'enveloppe.


      bodyC.body.setUserData(bateau);
      Model model;

      if(typeC.type == TypeComponent.BATEAU_A){
        model = assets.manager.get(assets.boatRModel, Model.class);
      }else{
        model = assets.manager.get(assets.boatBModel, Model.class);
      }

      modC.setModel(new ModelInstance(model));
      modC.transform.translate(0, 0, 1);

      /* On ajoute les components à l'entité */
      bateau.add(bateauC);
      bateau.add(bodyC);
      bateau.add(typeC);
      bateau.add(colC);
      bateau.add(modC);

      return bateau;
    }

    /** Place une entité marchandise à une position donnée
    * @param posx : position initiale sur l'axe des x
    * @param posy : position initiale sur l'axe des y
    */
    public void creeMarchandise(int posx, int posy){
      Entity merchendise = new Entity(); //Création de l'entité
      MerchendiseComponent merchendiseC = new MerchendiseComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();
      ModelComponent modC = new ModelComponent();
      float weight;

      /*Définition de ses caractéristique*/
      weight = MathUtils.random(MerchendiseComponent.LIMIT_MIN_M, MerchendiseComponent.LIMIT_MAX_M);
      merchendiseC.weight = weight;

      if(weight < MerchendiseComponent.LIMIT_LITTLE_M){
        merchendiseC.merchendiseType = MerchendiseComponent.LITTLE_MERCHENDISE;
        Socket.instantMerch = "P";
        Socket.envoyerWhiteLists("merch:P", -1);
      }else if(weight < MerchendiseComponent.LIMIT_CLASSIC_M){
        merchendiseC.merchendiseType = MerchendiseComponent.CLASSIC_MERCHENDISE;
        Socket.instantMerch = "M";
        Socket.envoyerWhiteLists("merch:M", -1);
      }else{
        merchendiseC.merchendiseType = MerchendiseComponent.BIG_MERCHENDISE ;
        Socket.instantMerch = "G";
        Socket.envoyerWhiteLists("merch:G", -1);
      }

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.StaticBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      CircleShape circle = new CircleShape();
      circle.setRadius(weight/2);
      circle.setPosition(new Vector2(0,0));

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_MARCHANDISE;
      fixDef.friction = IConfig.FRICTION_MARCHANDISE;
      fixDef.restitution = 0f;
      fixDef.shape = circle;
      fixDef.filter.categoryBits = CollisionComponent.CATEGORY_MERCHENDISE;
      fixDef.filter.maskBits = CollisionComponent.MASK_MERCHENDISE;

      /* Assignation du type/categorie */
      typeC.type = TypeComponent.MARCHANDISE;
      bodyC.body.createFixture(fixDef);
      circle.dispose(); //On libère l'enveloppe.

      bodyC.body.setUserData(merchendise);

      /* Définition du modèle de l'entité */
      modC.setModel(new ModelInstance(assets.manager.get(assets.merchModel, Model.class)));
      modC.transform.scale(weight/2.3f, weight/2.3f, weight/2.3f);

      /*On ajoute les components à l'entité*/
      merchendise.add(merchendiseC);
      merchendise.add(bodyC);
      merchendise.add(typeC);
      merchendise.add(colC);
      merchendise.add(modC);

      flecheA.getComponent(DirectionComponent.class).dest = bodyC.body;
      flecheB.getComponent(DirectionComponent.class).dest = bodyC.body;

      this.firstMerch = merchendise;
      engine.addEntity(merchendise);
    }


    /** Place une entité marchandise de manière "speudo-aléatoire"
    */
    public Entity creeMarchandise(){
      Entity merchendise = new Entity(); //Création de l'entité
      MerchendiseComponent merchendiseC = new MerchendiseComponent();
      BodyComponent bodyC = new BodyComponent();
      BodyDef bodyD = new BodyDef();
      FixtureDef fixDef = new FixtureDef();
      TypeComponent typeC =  new TypeComponent();
      CollisionComponent colC = new CollisionComponent();
      ModelComponent modC = new ModelComponent();
      float weight, posx, posy;


      /*Définition de ses caractéristique*/
      weight = MathUtils.random(MerchendiseComponent.LIMIT_MIN_M, MerchendiseComponent.LIMIT_MAX_M);
      merchendiseC.weight = weight;


      if(weight < MerchendiseComponent.LIMIT_LITTLE_M){
        merchendiseC.merchendiseType = MerchendiseComponent.LITTLE_MERCHENDISE;
        Socket.envoyerWhiteLists("merch:P", -1);
      }else if(weight < MerchendiseComponent.LIMIT_CLASSIC_M){
        merchendiseC.merchendiseType = MerchendiseComponent.CLASSIC_MERCHENDISE;
        Socket.envoyerWhiteLists("merch:M", -1);
      }else{
        merchendiseC.merchendiseType = MerchendiseComponent.BIG_MERCHENDISE ;
        Socket.envoyerWhiteLists("merch:G", -1);
      }

      /*Définition de la position de la marchandise*/
      Vector2 pos;
      do{
        pos = positionAleaMarch(weight);
      }while( Math.abs(pos.x) > (IConfig.LARGEUR_CARTE/2 - weight) || Math.abs(pos.y) > (IConfig.HAUTEUR_CARTE/2 - weight) );
      posx = pos.x;
      posy = pos.y;

      /* Définition du corps de l'enité */
      bodyD.type = BodyDef.BodyType.StaticBody;
      bodyD.position.x = posx;
      bodyD.position.y = posy;
      bodyC.body = world.createBody(bodyD);

      /* Création de l'enveloppe du bateau */
      CircleShape circle = new CircleShape();
      circle.setRadius(weight/2);
      circle.setPosition(new Vector2(0,0));

      /* Création de la fixture/ envrionnement */
      fixDef.density = IConfig.DENSITE_MARCHANDISE;
      fixDef.friction = IConfig.FRICTION_MARCHANDISE;
      fixDef.restitution = 0f;
      fixDef.shape = circle;
      fixDef.filter.categoryBits = CollisionComponent.CATEGORY_MERCHENDISE;
      fixDef.filter.maskBits = CollisionComponent.MASK_MERCHENDISE;

      /* Assignation du type/categorie */
      typeC.type = TypeComponent.MARCHANDISE;
      bodyC.body.createFixture(fixDef);
      circle.dispose(); //On libère l'enveloppe.

      bodyC.body.setUserData(merchendise);

      /* Définition du modèle de l'entité */
      modC.setModel(new ModelInstance(assets.manager.get(assets.merchModel, Model.class)));
      modC.transform.scale(weight/2, weight/2, weight/2);

      /*On ajoute les components à l'entité*/
      merchendise.add(merchendiseC);
      merchendise.add(bodyC);
      merchendise.add(typeC);
      merchendise.add(colC);
      merchendise.add(modC);

      flecheA.getComponent(DirectionComponent.class).dest = bodyC.body;
      flecheB.getComponent(DirectionComponent.class).dest = bodyC.body;

      engine.addEntity(merchendise);
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
      ModelComponent modC = new ModelComponent();
      PFXComponent pfxC = new PFXComponent();

      /* Définition du modèle de l'entité */
      modC.setModel(new ModelInstance(assets.manager.get(assets.canonballModel, Model.class)));
      modC.transform.scale(4, 4, 4);
      modC.transform.translate(0, 0, .5f);

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

      pfxC = new PFXComponent();
      pfxC.pfx = new ParticleEffect(assets.manager.get(assets.flamePFX, ParticleEffect.class));
      //pfxC.transform.scale(2, 2, 2);

      pfxC.pfx.init();
      pfxC.pfx.start();

      /*On ajoute les components à l'entité*/
      cannonball.add(cannonballC);
      cannonball.add(bodyC);
      cannonball.add(typeC);
      cannonball.add(colC);
      cannonball.add(modC);
      cannonball.add(pfxC);

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
      ModelComponent modC = new ModelComponent();

      /* Définition du modèle de l'entité */
      modC.setModel(new ModelInstance(ModelGenerator.seaModel(
              IConfig.LARGEUR_CARTE * 1.5f, 30f,
              IConfig.LARGEUR_CARTE, IConfig.HAUTEUR_CARTE, 4, 1f/4
      )));

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
      ocean.add(modC);

      engine.addEntity(ocean);
      this.ocean = ocean;
    }

    private void createArrows(){
      flecheA = new Entity();
      flecheB = new Entity();

      Model flecheMod = assets.manager.get(assets.arrowModel, Model.class);
      ModelComponent modC = new ModelComponent();
      DirectionComponent dirC = new DirectionComponent();

      /* Définition du modèle de l'entité */
      modC.setModel(new ModelInstance(flecheMod));
      dirC.src = bateauA.getComponent(BodyComponent.class).body;

      flecheA.add(modC);
      flecheA.add(dirC);

      modC = new ModelComponent();
      dirC = new DirectionComponent();

      /* Définition du modèle de l'entité */
      modC.setModel(new ModelInstance(flecheMod));
      dirC.src = bateauB.getComponent(BodyComponent.class).body;

      flecheB.add(modC);
      flecheB.add(dirC);

      engine.addEntity(flecheA);
      engine.addEntity(flecheB);
    }

    /** Selection via un aléatoire controlé d'une position sur la carte
    * @param weight de la marchandise
    */
    private Vector2 positionAleaMarch(float weight){
      float posx, posy;
      ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
      BodyComponent bodyA = bodyMap.get(this.bateauA);
      BodyComponent bodyB = bodyMap.get(this.bateauB);
      /*On récupère les coordonées des bateaux*/
      float posxA = bodyA.body.getWorldCenter().x;
      float posyA = bodyA.body.getWorldCenter().y;
      float posxB = bodyB.body.getWorldCenter().x;
      float posyB = bodyB.body.getWorldCenter().y;

      /*On détermine à quelle partie de la carte le bateau A apparatient*/
      int zoneA = oceanZone(posxA, posyA);

      /*On détermine à quelle partie de la carte le bateau B apparatient*/
      int zoneB = oceanZone(posxB, posyB);

      /*On crée la liste des zones candidats*/
      /*Règle de selection : - ni zone de A, ni zone de B
                            - ni zone de A +/- 1, ni zone de B +/- 1 */
      int[] zoneCandidate = new int[8];
      int nbCandidat = 0;

      int zoneInt1 = Math.floorMod(zoneA-1, 8); //ZoneA -1
      int zoneInt2 = Math.floorMod(zoneA+1, 8); //ZoneA +1
      int zoneInt3 = Math.floorMod(zoneB-1, 8); //ZoneB -1
      int zoneInt4 = Math.floorMod(zoneB+1, 8); //ZoneB +1

      for(int i = 0; i <= 7; i++){
          if( (i != zoneA) && (i != zoneB) && (i!=zoneInt1) && (i!=zoneInt2) && (i!=zoneInt3) && (i!=zoneInt4) ){
            zoneCandidate[nbCandidat] = i;
            nbCandidat++;
          }
      }


      /*On tire aléatoire une zone parmi celles candidates*/
      int indiceCandidat = (int)(Math.random()*(nbCandidat));
      int zoneM = zoneCandidate[indiceCandidat]; //Zone de la marchandise
      Random r = new Random();
      float moyenneX, moyenneY, ecartTX, ecartTY;
      float m1, m2;
      m1 = (float)IConfig.HAUTEUR_CARTE / (float)IConfig.LARGEUR_CARTE;
      m2 = -m1;

      /*On tire aléatoire une position qui sera le centre*/
      ecartTX = IConfig.LARGEUR_CARTE/15;
      ecartTY = IConfig.HAUTEUR_CARTE/20;
      moyenneX = (IConfig.LARGEUR_CARTE/2)/2;
      int nb_tirX_max= 50; //tirage aléatoire limité
      int nb_tirY_max = 50;
      int nb_tirX = 0;
      int nb_tirY = 0;

      switch(zoneM){
        case 0:
            posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

            /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
            if(posx > (IConfig.LARGEUR_CARTE/2 -weight)){
              posx = IConfig.LARGEUR_CARTE/2 - weight;
            }else if(posx < weight){
              posx = weight;
            }

            moyenneY = (posx)/4; //Calcule la moyenne de Y
            posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

            // /*On vérifie que y soit dans le bon intervalle*/
            // if(posy > (m1*posx -weight)){
            //   posy = m1*posx - weight;
            // }else if(posy < weight){
            //   posy = weight;
            // }
        break;

        case 1:
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx > (IConfig.LARGEUR_CARTE/2 -weight)){
            posx = IConfig.LARGEUR_CARTE/2 - weight;
          }else if(posx < weight){
            posx = weight;
          }

          moyenneY = (IConfig.HAUTEUR_CARTE/2 + posx)/3; //Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

          // /*On vérifie que y soit dans le bon intervalle*/
          // if(posy < (m1*posx -weight)){
          //   posy = m1*posx - weight;
          // }else if(posy > IConfig.HAUTEUR_CARTE/2 - weight){
          //   posy = IConfig.HAUTEUR_CARTE/2 - weight;
          // }
        break;

        case 2:
          moyenneX *= -1;
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx < -(IConfig.LARGEUR_CARTE/2 -weight)){
            posx = -IConfig.LARGEUR_CARTE/2 + weight;
          }else if(posx > -weight){
            posx = -weight;
          }

          moyenneY = (IConfig.HAUTEUR_CARTE/2 -posx)/3;//Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

          // /*On vérifie que y soit dans le bon intervalle*/
          // if(posy < (m2*posx -weight)){
          //   posy = m2*posx - weight;
          // }else if(posy > IConfig.HAUTEUR_CARTE/2 -weight){
          //   posy = IConfig.HAUTEUR_CARTE/2 - weight;
          // }
        break;

        case 3:
          moyenneX *= -1;
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx < -(IConfig.LARGEUR_CARTE/2 -weight)){
            posx = - IConfig.LARGEUR_CARTE/2 + weight;
          }else if(posx > -weight){
            posx = -weight;
          }

          moyenneY = (-posx)/4; //Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

          // /*On vérifie que y soit dans le bon intervalle*/
          // if(posy > (m2*posx -weight)){
          //   posy = m2*posx - weight;
          // }else if(posy < weight){
          //   posy = weight;
          // }
        break;

        case 4:
          moyenneX *= -1;
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx < -(IConfig.LARGEUR_CARTE/2 -weight)){
            posx = -IConfig.LARGEUR_CARTE/2 + weight;
          }else if(posx > -weight){
            posx = -weight;
          }

          moyenneY = (posx)/4; //Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

        break;

        case 5:
          moyenneX *= -1;
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx < -(IConfig.LARGEUR_CARTE/2 -weight)){
            posx = -IConfig.LARGEUR_CARTE/2 + weight;
          }else if(posx > -weight){
            posx = -weight;
          }

          moyenneY = (-IConfig.HAUTEUR_CARTE/2 + posx)/4; //Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

        break;

        case 6:
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx > (IConfig.LARGEUR_CARTE/2 -weight)){
            posx = IConfig.LARGEUR_CARTE/2 - weight;
          }else if(posx < weight){
            posx = weight;
          }

          moyenneY = (-IConfig.HAUTEUR_CARTE/2 - posx)/4;//Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

        break;

        case 7:
          moyenneX = 2*(IConfig.LARGEUR_CARTE/2)/3;
          posx = (float)r.nextGaussian()*(ecartTX) + moyenneX; //On tire x

          /*On vérifie que x soit dans le bon intervalle, sinon on tronque*/
          if(posx > (IConfig.LARGEUR_CARTE/2 -weight)){
            posx = IConfig.LARGEUR_CARTE/2 - weight;
          }else if(posx < weight){
            posx = weight;
          }

          moyenneY = (-posx)/4;//Calcule la moyenne de Y
          posy = (float)r.nextGaussian()*(ecartTY) + moyenneY; //On tir y

        break;

        default:
          posx = 0;
          posy = 0;
        break;
      }


      return new Vector2(posx, posy);
    }

    /** Renvoie le numéro de la zone dans laquelle se trouve le point (x,y)
    * @param x : l'abscisse du point
    * @param y : l'ordonnée du point
    * @return -1 si le point n'est pas dedans, et entre 0 et 8 sinon.
    */
    private int oceanZone(float x, float y){
      float m1, m2;
      m1 = (float)IConfig.HAUTEUR_CARTE / (float)IConfig.LARGEUR_CARTE;
      m2 = -m1;

      /*On regarde si le point est dans l'ocean*/
      if( (Math.abs(x) >= IConfig.LARGEUR_CARTE) || (Math.abs(y) >= IConfig.HAUTEUR_CARTE)){
        return -1;
      }

      if(x > 0){ //Zone 0,1,6 ou 7
        if(y> 0){ //Zone 0 ou 1
          if(y < m1*x){ // Zone 0
            return 0;
          }else{ //Zone 1
            return 1;
          }
        }else{ //Zone 6 ou 7
          if(y > m2*x){ //Zone 7
            return 7;
          }else{ //Zone 6
            return 6;
          }
        }
      }else{ //Zone 2,3,4 ou 5
        if(y>0){ //Zone 2 ou 3
           if(y < m2*x){ //Zone 3
             return 3;
           }else{ // Zone 2
             return 2;
           }
        }else{ //Zone 4 ou 5
          if(y > m1*x){ //Zone 4
            return 4;
          }else{ //Zone 5
            return 5;
          }
        }
      }


    }

    public World getWorld() {
        return world;
    }

    /** Supprime une entité
    */
    public void removeEntite(Entity ent){
      engine.removeEntity(ent);
    }


    /** Permet de remettre le monde à 0
    */
    public void reset(){
      Array<Body> bodies = new Array<>();
      engine.removeAllEntities();
      world.getBodies(bodies);
      for (Body body: bodies) {
        world.destroyBody(body);
      }

      generateWorld();
    }

}
