package krash220.xbob;

/**
 * Allocation-free primitive math and easing helpers for crosshair animation.
 */
public final class CrosshairEasing {

    private CrosshairEasing() {
    }

    public static float clamp01(float x) {
        return x < 0.0f ? 0.0f : (x > 1.0f ? 1.0f : x);
    }

    public static float clamp(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /**
     * Frame-rate independent exponential damping toward {@code target}.
     */
    public static float damp(float current, float target, float rate, float dtSeconds) {
        if (dtSeconds <= 0.0f) {
            return current;
        }
        float alpha = 1.0f - (float) Math.exp(-rate * dtSeconds);
        return current + (target - current) * alpha;
    }

    /**
     * Unboxed equivalent of {@code interpolation(Math::sin, x, begin, end, false)}.
     */
    public static float sinInterp(double x, double begin, double end) {
        double cx = x < 0.0 ? 0.0 : (x > 1.0 ? 1.0 : x);
        return (float) Math.sin((end - begin) * cx + begin);
    }

    /**
     * Unboxed equivalent of {@code interpolation(MainMod::fract, x, begin, end, false)}.
     */
    public static float fractInterp(double x, double begin, double end) {
        double cx = x < 0.0 ? 0.0 : (x > 1.0 ? 1.0 : x);
        double v = (end - begin) * cx + begin;
        return (float) (v - Math.floor(v));
    }

    public static float progressNanos(long startNanos, long durationNanos, long nowNanos) {
        if (durationNanos <= 0L) {
            return 1.0f;
        }
        return clamp01((float) (nowNanos - startNanos) / (float) durationNanos);
    }
}
