package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import krash220.xbob.game.api.bus.GuiBus;
import krash220.xbob.game.api.math.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 1.20.2 版本：与 1.20 相同的策略，把变换窗口限定在 vanilla 的
 * Gui.renderCrosshair 方法体内。
 *
 * 该方法只画准星贴图（1.20.2 用 GuiGraphics.blitSprite）与
 * attackIndicator=CROSSHAIR 时的攻击指示器进度条。窗口包住整个方法体，
 * 两者作为整体同步变换，攻击指示器恢复跟随准星；热键栏、血条、Boss 血条、
 * 经验条以及第三方在 RenderGuiOverlayEvent.Pre/Post 里绘制的 HUD 均在窗口之外。
 *
 * HEAD / RETURN 严格配对，RETURN 覆盖方法内所有提前 return（第三人称、
 * 旁观者），任何分支都不泄漏矩阵；GuiBus.finishFrame 提供额外兜底。
 */
@Mixin(Gui.class)
public class CrosshairRenderMixin {

    private static final String RENDER_CROSSHAIR = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V";

    @Inject(method = RENDER_CROSSHAIR,
            at = @At("HEAD"),
            require = 0)
    private void xbobBeforeCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
        GuiBus.doPre(new MatrixStack(guiGraphics.pose()), Minecraft.getInstance().getPartialTick());
    }

    @Inject(method = RENDER_CROSSHAIR,
            at = @At("RETURN"),
            require = 0)
    private void xbobAfterCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
        GuiBus.doPost(new MatrixStack(guiGraphics.pose()), Minecraft.getInstance().getPartialTick());
    }
}
