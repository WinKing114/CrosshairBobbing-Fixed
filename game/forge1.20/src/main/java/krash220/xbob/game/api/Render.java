package krash220.xbob.game.api;

import com.tacz.guns.client.event.CameraSetupEvent;
import krash220.xbob.game.api.math.MatrixStack;
import krash220.xbob.game.api.mod.TacZ;
import krash220.xbob.mixin.GameRendererAccessor;
import mirsario.cameraoverhaul.core.callbacks.ModifyCameraTransformCallback;
import mirsario.cameraoverhaul.core.structures.Transform;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ViewportEvent;

public class Render {

    public static int getScaledWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public static int getScaledHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public static int getBlitOffset() {
        return 0;
    }

    public static boolean isDebugCrosshair() {
        Minecraft mc = Minecraft.getInstance();

        return mc.options.hideGui || mc.options.renderDebug && !mc.player.isReducedDebugInfo() && !mc.options.reducedDebugInfo().get();
    }

    public static void bobView(MatrixStack mat, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        ((GameRendererAccessor) mc.gameRenderer).bobHurt(mat.mat, partialTicks);
        if (mc.options.bobView().get().booleanValue()) {
            ((GameRendererAccessor) mc.gameRenderer).bobView(mat.mat, partialTicks);
        }
    }

    private static Boolean coh = null;

    public static void modCamera(MatrixStack mat) {
        double rotX = 0;
        double rotY = 0;
        double rotZ = 0;

        if (coh == null) {
            try {
                Class.forName("mirsario.cameraoverhaul.core.callbacks.ModifyCameraTransformCallback");
                coh = true;
            } catch (ClassNotFoundException e) {
                coh = false;
            }
        }

        if (coh) {
            try {
                Transform t = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform(null, new Transform());

                rotX += t.eulerRot.x;
                rotY += t.eulerRot.y;
                rotZ += t.eulerRot.z;
            } catch (LinkageError | RuntimeException e) {
                // 相机模组版本不匹配时禁用联动，避免 NoSuchFieldError / NoSuchMethodError 崩溃
                coh = false;
            }
        }

        if (TacZ.loaded()) {
            Minecraft mc = Minecraft.getInstance();
            ViewportEvent.ComputeCameraAngles event = new ViewportEvent.ComputeCameraAngles(mc.gameRenderer, mc.gameRenderer.getMainCamera(), mc.getPartialTick(), 0F, 0F, 0F);
            CameraSetupEvent.applyLevelCameraAnimation(event);

            rotX += event.getPitch();
            rotY += event.getYaw();
            rotZ += event.getRoll();
        }

        mat.rotate((float) rotZ, 0, 0, 1);
        mat.rotate((float) rotX, 1, 0, 0);
        mat.rotate((float) rotY, 0, 1, 0);
    }

    public static void updateCameraMatrix(MatrixStack mat, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        mat.mat.mulPoseMatrix(mc.gameRenderer.getProjectionMatrix(((GameRendererAccessor) mc.gameRenderer).getFov(mc.gameRenderer.getMainCamera(), partialTicks, true)));
    }

    @SuppressWarnings("resource")
    public static float getReachDistance() {
        return Minecraft.getInstance().gameMode.getPickRange();
    }

    public static void distortion(MatrixStack mat, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        float g = mc.options.screenEffectScale().get().floatValue();
        float f = Mth.lerp(partialTicks, mc.player.oSpinningEffectIntensity, mc.player.spinningEffectIntensity) * g * g;
        if (f > 0.0F) {
           int i = mc.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
           float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
           f1 = f1 * f1;
           mat.rotate((((GameRendererAccessor) mc.gameRenderer).getTick() + partialTicks) * i, 0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
           mat.scale(1.0F / f1, 1.0F, 1.0F);
           float f2 = -(((GameRendererAccessor) mc.gameRenderer).getTick() + partialTicks) * i;
           mat.rotate(f2, 0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
        }
    }

    private static final float MAX_DEPTH = 128.0F;
    private static int cachedDepthTick = Integer.MIN_VALUE;
    private static double cachedEyeX, cachedEyeY, cachedEyeZ;
    private static double cachedViewX, cachedViewY, cachedViewZ;
    private static float cachedDepth = MAX_DEPTH;

    public static float getCenterDepth() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        if (entity == null || mc.level == null) {
            return MAX_DEPTH;
        }

        float partialTicks = mc.getFrameTime();
        Vec3 eye = entity.getEyePosition(partialTicks);

        // Fast path: reuse Minecraft's current crosshair hitResult when it already hit a block/entity
        HitResult crosshairHit = mc.hitResult;
        if (crosshairHit != null && crosshairHit.getType() != HitResult.Type.MISS) {
            float d = (float) crosshairHit.getLocation().distanceTo(eye);
            cachedDepth = Math.min(d, MAX_DEPTH);
            return cachedDepth;
        }

        Vec3 view = entity.getViewVector(partialTicks);
        int tick = entity.tickCount;
        if (tick == cachedDepthTick
                && Math.abs(eye.x - cachedEyeX) < 1.0e-3
                && Math.abs(eye.y - cachedEyeY) < 1.0e-3
                && Math.abs(eye.z - cachedEyeZ) < 1.0e-3
                && Math.abs(view.x - cachedViewX) < 5.0e-4
                && Math.abs(view.y - cachedViewY) < 5.0e-4
                && Math.abs(view.z - cachedViewZ) < 5.0e-4) {
            return cachedDepth;
        }

        Vec3 end = eye.add(view.x * MAX_DEPTH, view.y * MAX_DEPTH, view.z * MAX_DEPTH);
        HitResult result = mc.level.clip(new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));

        cachedDepthTick = tick;
        cachedEyeX = eye.x;
        cachedEyeY = eye.y;
        cachedEyeZ = eye.z;
        cachedViewX = view.x;
        cachedViewY = view.y;
        cachedViewZ = view.z;
        cachedDepth = result.getType() != HitResult.Type.MISS ? (float) result.getLocation().distanceTo(eye) : MAX_DEPTH;
        return cachedDepth;
    }
}
