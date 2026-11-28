package krash220.xbob.game.api.math;

public class MatrixStack {

    public void push() {throw new UnsupportedOperationException();}

    public void pop() {throw new UnsupportedOperationException();}

    public void translate(double x, double y, double z) {throw new UnsupportedOperationException();}

    public void rotate(float angle, float x, float y, float z) {throw new UnsupportedOperationException();}

    public void scale(float x, float y, float z) {throw new UnsupportedOperationException();}

    public void identity() {throw new UnsupportedOperationException();}

    public float[] multiplyVector(float x, float y, float z, float w) {throw new UnsupportedOperationException();}

    public void transformPoint(float x, float y, float z, float w, float[] out4) {
        float[] res = this.multiplyVector(x, y, z, w);
        out4[0] = res[0];
        out4[1] = res[1];
        out4[2] = res[2];
        out4[3] = res[3];
    }
}
