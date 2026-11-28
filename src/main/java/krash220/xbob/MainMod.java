package krash220.xbob;

import krash220.xbob.game.api.Loader;
import krash220.xbob.game.api.Logger;
import krash220.xbob.game.api.bus.GuiBus;
import krash220.xbob.game.api.bus.PlayerBus;

public class MainMod {

    private final CrosshairAnimationState animationState = new CrosshairAnimationState();
    private final CrosshairTransformPipeline pipeline = new CrosshairTransformPipeline(this.animationState);

    public MainMod() {
        Logger.info("Hello, ${MOD_NAME}!");
        Logger.info("Platform: {}, Minecraft: {}, isClient: {}", Loader.getPlatform(), Loader.getVersion(), Loader.isClient());

        GuiBus.registerRenderCrosshair(this.pipeline::preRenderCrosshair, this.pipeline::postRenderCrosshair);
        PlayerBus.registerAttack(this::onAttack);
        CrosshairConfig.init();
    }

    public void onAttack(float damage) {
        this.animationState.recordAttack(damage, System.nanoTime());
    }
}
