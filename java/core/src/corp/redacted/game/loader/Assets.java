package corp.redacted.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.utils.Array;

public class Assets {
    public final AssetManager manager = new AssetManager();

    // Modeles 3D
    public final String shakeModel = "models/shake.g3dj";
    public final String merchModel = "models/tonneau.g3dj";
    public final String boatBModel = "models/bateauV2Bleu.g3dj";
    public final String boatRModel = "models/bateauV2Rouge.g3dj";
    public final String canonballModel = "models/boulet.g3dj";
    public final String arrowModel = "models/arrow.g3dj";

    // Polices
    public static final String pirateFont = "fonts/TheDarkestPearl.ttf";

    // Textures
    public static final String sandTexture = "models/TextureSand.png";

    // PFX
    public final String flamePFX = "pfx/canon_flame.pfx";

    /**
     * Charge les modèles 3D
     */
    public void queueAdd3DModels(){
        //ModelLoader.ModelParameters mp = new ModelLoader.ModelParameters();
        manager.load(canonballModel, Model.class);
        manager.load(merchModel, Model.class);
        manager.load(boatBModel, Model.class);
        manager.load(boatRModel, Model.class);
        manager.load(arrowModel, Model.class);
        manager.load(shakeModel, Model.class);
    }

    public void queueAddTextures(){
        manager.load(sandTexture, Texture.class);
    }

    public void queueAddPFX(Array<ParticleBatch<?>> batches){
        ParticleEffectLoader.ParticleEffectLoadParameter parameter = new ParticleEffectLoader.ParticleEffectLoadParameter(batches);
        manager.load(flamePFX, ParticleEffect.class, parameter);
    }
}
