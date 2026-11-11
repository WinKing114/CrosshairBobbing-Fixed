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
 * 把准星动画的矩阵变换严格限定在 vanilla 的 Gui.renderCrosshair 方法体内。
 *
 * 这个方法只画两样东西：准星贴图，以及 attackIndicator=CROSSHAIR 时的
 * 攻击指示器进度条（在准星贴图之后、同一方法内绘制）。让窗口包住整个方法体，
 * 准星与它自带的攻击指示器就会作为一个整体同步摇晃/旋转 —— 这是官方 1.10
 * 的行为，也是「原版攻击指示器跟随准星」的预期。F3 调试准星分支下
 * MainMod.preRenderCrossHair 会跳过变换，只保留配对的 push/pop。
 *
 * 与最初实现（RenderGuiOverlayEvent.Pre/Post(CROSSHAIR)）的区别：事件包裹的是
 * 整个 overlay，第三方模组在该事件里绘制的 HUD（MC百科 30 楼「其他锚点的
 * 进度条乱飞」、#3/#6/#13）也会被以屏幕中心为支点变换。这里改为在
 * Gui.renderCrosshair 方法内部注入，窗口在方法返回前就闭合，
 * 热键栏、血条、Boss 血条、经验条以及第三方在 Pre/Post 事件里画的 UI
 * 全部落在窗口之外，不再被误变换。
 *
 * HEAD / RETURN 严格配对；RETURN 会覆盖方法内所有提前 return（第三人称、
 * 旁观者），保证任何分支都不会把变换矩阵泄漏到方法之外。GuiBus 另有
 * finishFrame 兜底，防止异常路径残留矩阵。
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
