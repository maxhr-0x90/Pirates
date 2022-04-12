package corp.redacted.game.model;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BaseShapeBuilder;
import com.badlogic.gdx.math.Vector3;

public class SandShapeBuilder extends BaseShapeBuilder {
    MeshPartBuilder builder;
    private final float viewWidth, depth, waterHeight, waterWidth, stiffness, ratioYX, offsetX, stepSize;
    private final int stepNb;

    public SandShapeBuilder(
        MeshPartBuilder builder, float viewWidth, float depth,
        float alt0Width, float alt0Height, int stepNb, float stiffness
    ){
        this.builder = builder;
        this.viewWidth = viewWidth;
        this.depth = depth;
        this.waterWidth = alt0Width;
        this.waterHeight = alt0Height;
        this.stepNb = Math.max(1, stepNb);
        this.stiffness = stiffness;

        ratioYX = alt0Height / alt0Width;
        offsetX = viewWidth - waterWidth;
        stepSize = 3f / (stepNb * stiffness);
    }

    /**
     * Crée le modèle de sable
     */
    public void build(){
        float viewHeight = viewWidth * ratioYX;

        builder.ensureVertices(8 + stepNb * 8);
        builder.ensureRectangleIndices(5 + stepNb * 8);

        MeshPartBuilder.VertexInfo[] vi = new MeshPartBuilder.VertexInfo[8];
        for (int i = 0; i < vi.length; i++) {
            vi[i] = new MeshPartBuilder.VertexInfo();
            vi[i].set(null, null, null, null);
            vi[i].hasPosition = vi[i].hasNormal = true;
        }

        short[] ind = new short[8];

        setVertexSand(vi[4], new Vector3(-viewWidth /2, -viewHeight/2, depth/2), -offsetX);
        setVertexSand(vi[5], new Vector3(viewWidth /2, -viewHeight/2, depth/2), offsetX);
        setVertexSand(vi[6], new Vector3(viewWidth /2, viewHeight/2, depth/2), offsetX);
        setVertexSand(vi[7], new Vector3(-viewWidth /2, viewHeight/2, depth/2), -offsetX);

        ind[4] = builder.vertex(vi[4]);
        ind[5] = builder.vertex(vi[5]);
        ind[6] = builder.vertex(vi[6]);
        ind[7] = builder.vertex(vi[7]);

        float step = -stepNb * stepSize;
        for (int i = 0; i < stepNb * 2 + 1; i++) {
            vi[0].set(vi[4]); vi[1].set(vi[5]); vi[2].set(vi[6]); vi[3].set(vi[7]);
            ind[0] = ind[4]; ind[1] = ind[5]; ind[2] = ind[6]; ind[3] = ind[7];

            float x = waterWidth / 2 - step;
            float y = waterHeight / 2 - step;
            setVertexSand(vi[4], new Vector3(-x, -y, z(step)), -offsetX);
            setVertexSand(vi[5], new Vector3(x, -y, z(step)), offsetX);
            setVertexSand(vi[6], new Vector3(x, y, z(step)), offsetX);
            setVertexSand(vi[7], new Vector3(-x, y, z(step)), -offsetX);

            ind[4] = builder.vertex(vi[4]);
            ind[5] = builder.vertex(vi[5]);
            ind[6] = builder.vertex(vi[6]);
            ind[7] = builder.vertex(vi[7]);

            builder.rect(ind[1], ind[5], ind[4], ind[0]);
            builder.rect(ind[2], ind[6], ind[5], ind[1]);
            builder.rect(ind[3], ind[7], ind[6], ind[2]);
            builder.rect(ind[0], ind[4], ind[7], ind[3]);

            step += stepSize;
        }

        builder.rect(ind[4], ind[5], ind[6], ind[7]);
    }

    private float z(float x){
        return (float) (depth / 2 * Math.tanh(-x * stiffness));
    }

    private void setVertexSand(MeshPartBuilder.VertexInfo vi, Vector3 pos, float offsetX){
        vi.position.set(pos);
        vi.normal.set(normalSandVertex(vi.position, offsetX));
    }

    private Vector3 normalSandVertex(Vector3 vertex, float offsetX){
        float amplitude = depth / 2;
        Vector3 tangent = sigmoidTangent(vertex.x + offsetX, amplitude, vertex.x > 0 ? stiffness : -stiffness);
        Vector3 binormal = new Vector3(0, -1, 0);

        return binormal.crs(tangent).nor();
    }

    private Vector3 sigmoidTangent(float x, float amplitude, float stiffness){
        return new Vector3(
                1, ratioYX,amplitude * stiffness * (float) (1 - Math.pow(Math.tanh(stiffness * x), 2))
        ).nor();
    }
}
