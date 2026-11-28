package krash220.xbob.game.api.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;

public class MatrixStack {

    public PoseStack mat;

    private final Vector3f scratchAxis = new Vector3f();
    private final Quaternionf scratchQuat = new Quaternionf();
    private final Vector4f scratchVec = new Vector4f();
    private final float[] scratchOut = new float[4];

    public MatrixStack() {
        this.mat = new PoseStack();
    }

    public MatrixStack(PoseStack mat) {
        this.mat = mat;
    }

    public MatrixStack rebind(PoseStack mat) {
        this.mat = mat;
        return this;
    }

    public void push() {
        this.mat.pushPose();
    }

    public void pop() {
        this.mat.popPose();
    }

    public void translate(double x, double y, double z) {
        this.mat.translate(x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        this.scratchAxis.set(x, y, z);
        this.scratchQuat.fromAxisAngleDeg(this.scratchAxis, angle);
        this.mat.mulPose(this.scratchQuat);
    }

    public void scale(float x, float y, float z) {
        this.mat.scale(x, y, z);
    }

    public void identity() {
        this.mat.last().pose().identity();
    }

    public float[] multiplyVector(float x, float y, float z, float w) {
        this.transformPoint(x, y, z, w, this.scratchOut);
        return new float[] {this.scratchOut[0], this.scratchOut[1], this.scratchOut[2], this.scratchOut[3]};
    }

    public void transformPoint(float x, float y, float z, float w, float[] out4) {
        this.scratchVec.set(x, y, z, w);
        this.scratchVec.mul(this.mat.last().pose());
        out4[0] = this.scratchVec.x();
        out4[1] = this.scratchVec.y();
        out4[2] = this.scratchVec.z();
        out4[3] = this.scratchVec.w();
    }
}
