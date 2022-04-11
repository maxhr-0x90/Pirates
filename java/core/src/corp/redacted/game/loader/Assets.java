package corp.redacted.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;

public class Assets {
    public final AssetManager manager = new AssetManager();

    // Modeles 3D
    public final String merchModel = "models/tonneau.g3dj";
    public final String boatModel = "models/bateau.g3dj";
    public final String canonballModel = "models/boulet.g3dj";

    public void queueAdd3DModels(){
        //ModelLoader.ModelParameters mp = new ModelLoader.ModelParameters();
        manager.load(boatModel, Model.class);
        manager.load(canonballModel, Model.class);
        manager.load(merchModel, Model.class);
    }
}
