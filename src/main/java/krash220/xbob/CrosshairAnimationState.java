package krash220.xbob;

/**
 * Monotonic nanosecond-precision animation state and frame-rate independent smoothers.
 */
public final class CrosshairAnimationState {

    public static final float MIN_ATTACK_SCALE = 1.4f;
    public static final float MAX_ATTACK_SCALE = 2.0f;
    public static final float MIN_ATTACK_DEGREES = 15.0f;
    public static final float MAX_ATTACK_DEGREES = 25.0f;
    public static final long ATTACK_ANIM_NANOS = 200_000_000L;
    public static final long CRIT_ANIM_NANOS = 300_000_000L;
    public static final float CRIT_SCALE = 2.0f;

    private static final float MIN_DEPTH = 0.5f;
    private static final float MAX_DEPTH = 128.0f;

    private long lastFrameNanos = 0L;
    private float smoothedDepth = 16.0f;
    private boolean depthInitialized = false;

    private float smoothedAim = 0.0f;
    private float smoothedSneak = 0.0f;
    private float smoothedItemOffset = 0.0f;

    private boolean isCrit = false;
    private int critDir = 1;
    private long attackAnimStartNanos = 0L;
    private float attackAnimDegree = 0.0f;
    private float attackAnimScale = 1.0f;
    private float attackDamage = 0.0f;

    public float beginFrame(long nowNanos) {
        if (this.lastFrameNanos == 0L) {
            this.lastFrameNanos = nowNanos;
            return 1.0f / 60.0f;
        }
        float dt = (float) (nowNanos - this.lastFrameNanos) * 1.0e-9f;
        this.lastFrameNanos = nowNanos;
        return CrosshairEasing.clamp(dt, 0.0f, 0.05f);
    }

    public void recordAttack(float damage, long nowNanos) {
        boolean crit = damage > 1.0f;
        boolean critFinished = !this.isCrit || (nowNanos - this.attackAnimStartNanos) > CRIT_ANIM_NANOS;
        if (crit || critFinished) {
            this.attackDamage = Math.min(1.0f, damage);
            this.attackAnimStartNanos = nowNanos;
            float r = (float) Math.random();
            this.attackAnimDegree = r * (MAX_ATTACK_DEGREES - MIN_ATTACK_DEGREES) + MIN_ATTACK_DEGREES;
            this.attackAnimScale = r * (MAX_ATTACK_SCALE - MIN_ATTACK_SCALE) + MIN_ATTACK_SCALE;
            this.isCrit = crit;
            this.critDir = -this.critDir;
        }
    }

    public boolean isAttackActive(long nowNanos) {
        long len = this.isCrit ? CRIT_ANIM_NANOS : ATTACK_ANIM_NANOS;
        return this.attackAnimStartNanos != 0L && (nowNanos - this.attackAnimStartNanos) < len;
    }

    public float smoothDepth(float rawDepth, float dt) {
        float clamped = CrosshairEasing.clamp(rawDepth, MIN_DEPTH, MAX_DEPTH);
        if (!this.depthInitialized) {
            this.smoothedDepth = clamped;
            this.depthInitialized = true;
            return clamped;
        }
        // Damp in reciprocal space so near-to-far transitions feel visually linear
        float curInv = 1.0f / this.smoothedDepth;
        float tgtInv = 1.0f / clamped;
        float nextInv = CrosshairEasing.damp(curInv, tgtInv, 26.0f, dt);
        this.smoothedDepth = CrosshairEasing.clamp(1.0f / Math.max(nextInv, 1.0e-4f), MIN_DEPTH, MAX_DEPTH);
        return this.smoothedDepth;
    }

    public float smoothAim(float targetAim, float dt) {
        this.smoothedAim = CrosshairEasing.damp(this.smoothedAim, targetAim, 24.0f, dt);
        return this.smoothedAim;
    }

    public float smoothSneak(float targetSneak, float dt) {
        this.smoothedSneak = CrosshairEasing.damp(this.smoothedSneak, targetSneak, 30.0f, dt);
        return this.smoothedSneak;
    }

    public float smoothItemOffset(float targetItem, float dt) {
        this.smoothedItemOffset = CrosshairEasing.damp(this.smoothedItemOffset, targetItem, 28.0f, dt);
        return this.smoothedItemOffset;
    }

    public boolean isCrit() {
        return this.isCrit;
    }

    public int getCritDir() {
        return this.critDir;
    }

    public long getAttackAnimStartNanos() {
        return this.attackAnimStartNanos;
    }

    public float getAttackAnimDegree() {
        return this.attackAnimDegree;
    }

    public float getAttackAnimScale() {
        return this.attackAnimScale;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }
}
