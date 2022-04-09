package corp.redacted.game.entity.components;

import com.badlogic.ashley.core.Component;

public class MerchendiseComponent implements Component {
    /*Catégories de marchandises*/
    public static int BIG_MERCHENDISE = 1;
    public static int CLASSIC_MERCHENDISE = 2;
    public static int LITTLE_MERCHENDISE = 3;

    /*Limites de poids pour les catégories*/
    public static int LIMIT_MAX_M = 30;
    public static int LIMIT_MIN_M = 5;
    public static int LIMIT_LITTLE_M = 15; //kg
    public static int LIMIT_CLASSIC_M = 25; //kg

    public float weight; //poids de la marchandise
    public int merchendiseType; //catégorie de la marchandise

}
