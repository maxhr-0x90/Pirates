package corp.redacted.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;

public class Assets {
    public final AssetManager manager = new AssetManager();

    // Modeles 3D
    public final String boatModel = "models/boat.g3db";
    public final String waterModel = "models/water.g3db";
    public final String canonballModel = "models/canonball.g3db";
    public final String cubeModel = "models/test.g3db";

    public void queueAdd3DModels(){
        //ModelLoader.ModelParameters mp = new ModelLoader.ModelParameters();
        manager.load(boatModel, Model.class);
        manager.load(waterModel, Model.class);
        manager.load(canonballModel, Model.class);
        manager.load(cubeModel, Model.class);
    }
}
