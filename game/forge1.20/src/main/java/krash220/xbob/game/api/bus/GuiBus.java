package krash220.xbob.game.api.bus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import krash220.xbob.game.api.math.MatrixStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiBus {

    private static boolean registered = false;

    /** 当前是否处于准星变换窗口内（push 未 pop）。用于防止同帧重复变换与矩阵残留。 */
    private static boolean active = false;

    private static List<BiConsumer<MatrixStack, Float>> pre = new ArrayList<>();
    private static List<BiConsumer<MatrixStack, Float>> post = new ArrayList<>();

    public static void registerRenderCrosshair(BiConsumer<MatrixStack, Float> pre, BiConsumer<MatrixStack, Float> post) {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(GuiBus.class);
            registered = true;
        }

        GuiBus.pre.add(pre);
        GuiBus.post.add(post);
    }

    /** 由 CrosshairRenderMixin 在 renderCrosshair 方法入口调用；同帧已激活时忽略，避免双重变换。 */
    public static void doPre(MatrixStack mat, float partialTicks) {
        if (active) {
            return;
        }

        active = true;

        for (BiConsumer<MatrixStack, Float> handler : pre) {
            handler.accept(mat, partialTicks);
        }
    }

    /** 由 CrosshairRenderMixin 在 renderCrosshair 的每个 return 前调用；严格与 doPre 配对恢复矩阵。 */
    public static void doPost(MatrixStack mat, float partialTicks) {
        if (!active) {
            return;
        }

        active = false;

        for (BiConsumer<MatrixStack, Float> handler : post) {
            handler.accept(mat, partialTicks);
        }
    }

    /** 兜底：若某帧因注入点缺失 / 异常导致 doPost 未执行，在准星层结束时强制恢复矩阵。 */
    public static void finishFrame(MatrixStack mat, float partialTicks) {
        if (!active) {
            return;
        }

        active = false;

        for (BiConsumer<MatrixStack, Float> handler : post) {
            handler.accept(mat, partialTicks);
        }
    }

    @SubscribeEvent
    public static void post(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
            finishFrame(new MatrixStack(event.getGuiGraphics().pose()), event.getPartialTick());
        }
    }
}
