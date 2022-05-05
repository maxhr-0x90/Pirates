package corp.redacted.game.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class CustomShaderProvider extends DefaultShaderProvider {
    DefaultShader.Config shadeConf;

    public CustomShaderProvider(com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config defaultConf){
        super(defaultConf);

        shadeConf = new DefaultShader.Config();
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        if (renderable.material.has(TextureAttribute.Ambient) && renderable.material.has(TextureAttribute.Normal)){
            shadeConf.vertexShader = Gdx.files.internal("shaders/wave.vertex.glsl").readString();
            shadeConf.fragmentShader = Gdx.files.internal("shaders/wave.fragment.glsl").readString();
            return new DefaultShader(renderable, shadeConf);
        } else if (renderable.material.has(TextureAttribute.Normal)){
            shadeConf.vertexShader = Gdx.files.internal("shaders/basic.vertex.glsl").readString();
            shadeConf.fragmentShader = Gdx.files.internal("shaders/basic.fragment.glsl").readString();
            return new DefaultShader(renderable, shadeConf);
        } else {
            shadeConf.vertexShader = Gdx.files.internal("shaders/basic.vertex.glsl").readString();
            shadeConf.fragmentShader = Gdx.files.internal("shaders/basic.fragment.glsl").readString();
            return new DefaultShader(renderable, shadeConf);
        }
    }
}
