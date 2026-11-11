package krash220.xbob.loader.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;

public class FabricQuiltUtils {

    private static final boolean isQuilt;

    static {
        boolean q = false;

        try {
            // 只凭 Quilt API 类判定会误伤「只有 API、没有实现」的环境（issue #18）：
            // 该环境会走进 Quilt 分支并触发 NoClassDefFoundError: QuiltLoaderImpl。
            // 必须以 impl 类是否存在为准。
            Class.forName("org.quiltmc.loader.impl.QuiltLoaderImpl");
            q = true;
        } catch (ClassNotFoundException | LinkageError e) {
            q = false;
        }

        isQuilt = q;
    }

    public static boolean isQuilt() {
        return isQuilt;
    }

    public static String getPlatform() {
        return isQuilt ? "Quilt" : "Fabric";
    }

    public static String getVersion() {
        return FabricLoaderImpl.INSTANCE.getGameProvider().getRawGameVersion();
    }

    public static void displayError(String main, String err, boolean exitAfter) {
        FabricGuiEntry.displayError(main, new RuntimeException(err), exitAfter);

        throw new RuntimeException(err);
    }

    public static Object getGameTransformer() {
        return FabricLoaderImpl.INSTANCE.getGameProvider().getEntrypointTransformer();
    }

    public static EnvType getEnvironmentType() {
        return FabricLoaderImpl.INSTANCE.getEnvironmentType();
    }

    public static FileSystem getFileSystem(URI uri) throws IOException {
        return FileSystems.newFileSystem(uri, new HashMap<>());
    }

    public static File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }
}
