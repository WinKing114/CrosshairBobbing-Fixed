package krash220.xbob;

import krash220.xbob.game.api.Player;
import krash220.xbob.game.api.Player.ItemType;
import krash220.xbob.game.api.Render;
import krash220.xbob.game.api.math.MatrixStack;

/**
 * Zero-allocation crosshair transform pipeline.
 * All per-frame scratch buffers are reused; no boxed lambdas or temporary vectors are created.
 */
public final class CrosshairTransformPipeline {

    private final MatrixStack cameraMatrix = new MatrixStack();
    private final float[] posScratch = new float[4];
    private final float[] rotScratch = new float[4];
    private final CrosshairAnimationState state;

    public CrosshairTransformPipeline(CrosshairAnimationState state) {
        this.state = state;
    }

    public void preRenderCrosshair(MatrixStack mat, float partialTicks) {
        mat.push();

        if (Render.isDebugCrosshair()) {
            return;
        }

        long nowNanos = System.nanoTime();
        float dt = this.state.beginFrame(nowNanos);

        this.cameraMatrix.identity();
        Render.updateCameraMatrix(this.cameraMatrix, partialTicks);

        if (CrosshairConfig.isEnabled(CrosshairConfig.BOB)) {
            Render.bobView(this.cameraMatrix, partialTicks);
            Render.distortion(this.cameraMatrix, partialTicks);
            Render.modCamera(this.cameraMatrix);
        }

        float rawItem = 1.0f - Player.changeItemProgress(partialTicks);
        float item = this.state.smoothItemOffset(rawItem, dt);
        if (CrosshairConfig.isEnabled(CrosshairConfig.ITEM)) {
            this.cameraMatrix.translate(item * 0.08, item * -0.3, 0.0);
        }

        float dist = this.state.smoothDepth(Render.getCenterDepth(), dt);
        this.cameraMatrix.transformPoint(0.0f, 0.0f, -dist, 1.0f, this.posScratch);
        this.cameraMatrix.transformPoint(0.0f, 1.0f, -dist, 1.0f, this.rotScratch);

        float invPosW = this.posScratch[3] != 0.0f ? 1.0f / this.posScratch[3] : 1.0f;
        float invRotW = this.rotScratch[3] != 0.0f ? 1.0f / this.rotScratch[3] : 1.0f;

        float offsetX = this.posScratch[0] * invPosW;
        float offsetY = this.posScratch[1] * invPosW;
        float rotOffX = this.rotScratch[0] * invRotW - offsetX;
        float rotOffY = this.rotScratch[1] * invRotW - offsetY;

        float angle = (float) Math.toDegrees(Math.atan2(rotOffX, rotOffY));

        boolean attackBlockingSwing = !CrosshairConfig.isEnabled(CrosshairConfig.ATK) || this.state.isAttackActive(nowNanos);
        float swing = attackBlockingSwing ? 1.0f : Player.swingProgress(partialTicks);
        float scale = 1.0f + CrosshairEasing.sinInterp(swing, 0.5 * Math.PI, Math.PI) * 0.2f;

        if (CrosshairConfig.isEnabled(CrosshairConfig.SPEAR)) {
            angle += CrosshairEasing.sinInterp(Player.spinProgress(partialTicks), 0.0, 0.5 * Math.PI) * -720.0f;
        }

        if (CrosshairConfig.isEnabled(CrosshairConfig.SNEAK)) {
            float sneak = this.state.smoothSneak(Player.sneakProgress(partialTicks), dt);
            angle += -sneak * 70.0f;
        } else {
            this.state.smoothSneak(0.0f, dt);
        }

        ItemType using = Player.getUsingType();
        float usingProgress = Player.usingItemProgress(partialTicks);

        if (using == ItemType.EATING && CrosshairConfig.isEnabled(CrosshairConfig.EAT)) {
            if (usingProgress < 0.2f) {
                angle += CrosshairEasing.sinInterp(Math.min(1.0, usingProgress * 10.0), 0.0, 0.5 * Math.PI) * 135.0f;
            } else if (usingProgress < 0.9f) {
                angle += CrosshairEasing.sinInterp((usingProgress - 0.2) / 0.7, 0.0, 6.0 * Math.PI) * -30.0f + 45.0f;
            } else {
                angle += CrosshairEasing.sinInterp(Math.min(1.0, (usingProgress - 0.9) * 10.0), 0.0, 0.5 * Math.PI) * -135.0f + 45.0f;
            }
        } else if (using == ItemType.CROSSBOW && CrosshairConfig.isEnabled(CrosshairConfig.XBOW)) {
            if (usingProgress < 0.9f) {
                angle += (usingProgress / 0.9f) * 360.0f;
            } else {
                angle += CrosshairEasing.sinInterp(Math.min(1.0, (usingProgress - 0.9) * 10.0), 0.0, 0.5 * Math.PI) * -180.0f + 180.0f;
            }
        }

        float targetAim = 0.0f;
        if (using == ItemType.CROSSBOW && CrosshairConfig.isEnabled(CrosshairConfig.XBOW)) {
            if (usingProgress < 0.9f) {
                scale *= CrosshairEasing.fractInterp(1.0 - usingProgress / 0.9, 0.0, 5.0) * 0.8f + 1.0f;
            }
        } else if ((using == ItemType.BOW || using == ItemType.SPEAR) && CrosshairConfig.isEnabled(CrosshairConfig.AIM)) {
            targetAim = CrosshairEasing.sinInterp(usingProgress, 0.0, 0.5 * Math.PI);
        }

        float aim = this.state.smoothAim(targetAim, dt);
        scale *= aim * 0.5f + 1.0f;
        float scaleBow = 1.0f - aim * 0.6f;

        if (CrosshairConfig.isEnabled(CrosshairConfig.ATK) && this.state.isAttackActive(nowNanos)) {
            if (this.state.isCrit()) {
                float x = CrosshairEasing.progressNanos(
                    this.state.getAttackAnimStartNanos(),
                    CrosshairAnimationState.CRIT_ANIM_NANOS,
                    nowNanos
                );
                angle += CrosshairEasing.sinInterp(x, 0.0, 0.5 * Math.PI) * 360.0f * this.state.getCritDir();
                scale *= CrosshairEasing.sinInterp(x, 0.0, Math.PI) * (CrosshairAnimationState.CRIT_SCALE - 1.0f) + 1.0f;
            } else {
                float x = CrosshairEasing.progressNanos(
                    this.state.getAttackAnimStartNanos(),
                    CrosshairAnimationState.ATTACK_ANIM_NANOS,
                    nowNanos
                );
                float dmg = this.state.getAttackDamage();
                if (dmg > 0.01f) {
                    angle += CrosshairEasing.sinInterp(x, 0.5 * Math.PI, Math.PI) * dmg * this.state.getAttackAnimDegree();
                    scale *= CrosshairEasing.sinInterp(x, 0.5 * Math.PI, Math.PI) * dmg * (this.state.getAttackAnimScale() - 1.0f) + 1.0f;
                } else {
                    offsetX += CrosshairEasing.sinInterp(x, 0.0, 4.0 * Math.PI) * 0.01f;
                }
            }
        }

        float halfWidth = Render.getScaledWidth() * 0.5f;
        float halfHeight = Render.getScaledHeight() * 0.5f;

        mat.translate(halfWidth, halfHeight, 0.0);
        mat.translate(offsetX * halfWidth, -offsetY * halfHeight, 0.0);
        mat.rotate(angle, 0.0f, 0.0f, 1.0f);
        mat.scale(scale, scale, scale);
        mat.scale(1.0f, scaleBow, 1.0f);
        mat.translate(-halfWidth, -halfHeight, 0.0);
    }

    public void postRenderCrosshair(MatrixStack mat, float partialTicks) {
        mat.pop();
    }
}
