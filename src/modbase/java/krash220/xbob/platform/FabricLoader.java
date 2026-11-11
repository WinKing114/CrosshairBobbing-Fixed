package krash220.xbob.platform;

import java.lang.reflect.InvocationTargetException;

import net.fabricmc.api.ClientModInitializer;

public class FabricLoader implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        try {
            // Quilt 的加载器结构与 Fabric 不兼容（issue #11 / #18）：直接跳过初始化，
            // 让游戏正常启动，而不是在 Mixin 阶段崩溃。
            Class.forName("org.quiltmc.loader.impl.QuiltLoaderImpl");
            System.err.println("[${MOD_ID}] Quilt is not supported; the mod will be inactive.");
            return;
        } catch (ClassNotFoundException | LinkageError e) {
            // Fabric 环境，继续初始化
        }

        try {
            Class.forName("${MOD_ENTRYPOINT}").getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
