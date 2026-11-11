package krash220.xbob.loader.fabric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import krash220.xbob.loader.utils.FabricQuiltUtils;
import krash220.xbob.loader.utils.VersionMapping;

public class MixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        // Quilt 的 GameTransformer 结构与 Fabric 不同，反射注入内核类会失败并连带
        // 使 Mixin 转换期缺少 PlayerBus 等类（issue #11）。这里优雅跳过：
        // 本模组在 Quilt 上不生效，但不再让游戏启动崩溃。
        if (FabricQuiltUtils.isQuilt()) {
            System.err.println("[${MOD_ID}] Quilt is not supported; skipping core injection. The mod will be inactive, the game will keep running.");
            return;
        }

        String version = FabricQuiltUtils.getVersion();
        String lib = VersionMapping.get(version);

        if (lib == null) {
            FabricQuiltUtils.displayError("Incompatible mod.", "[${MOD_ID}] This mod supports Minecraft " + VersionMapping.getSupportedRange() + ", but found " + version, true);
            return;
        }

        String coreFile = "/META-INF/core/fabric" + lib + ".jar";
        FileSystem fs = null;

        try {
            URI uri = MixinPlugin.class.getResource("MixinPlugin.class").toURI();
            fs = FabricQuiltUtils.getFileSystem(uri);
        } catch (URISyntaxException | IOException | RuntimeException e) {
            FabricQuiltUtils.displayError("Incompatible mod.", "[${MOD_ID}] Unable to open the mod jar.", true);
            return;
        }

        if (fs == null) {
            FabricQuiltUtils.displayError("Incompatible mod.", "[${MOD_ID}] Unable to open the mod jar.", true);
            return;
        }

        Path core = fs.getPath(coreFile);

        if (!Files.exists(core)) {
            FabricQuiltUtils.displayError("Incompatible mod.", "[${MOD_ID}] Missing core for Minecraft " + version, true);
            return;
        }

        Map<String, byte[]> patchedClasses = findPatchedClasses();

        if (patchedClasses == null) {
            FabricQuiltUtils.displayError("Incompatible mod.", "[${MOD_ID}] Unable to access the loader class transformer.", true);
            return;
        }

        try {
            ZipInputStream input = new ZipInputStream(Files.newInputStream(core));
            for (ZipEntry entry = input.getNextEntry(); entry != null; input.closeEntry(), entry = input.getNextEntry()) {
                String name = entry.getName();

                if (name.endsWith(".class")) {
                    byte[] buf = new byte[4096];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read = 0;

                    while ((read = input.read(buf)) > 0) {
                        bos.write(buf, 0, read);
                    }

                    patchedClasses.put(name.replace("/", ".").substring(0, name.length() - 6), bos.toByteArray());
                    bos.close();
                }
            }

            input.close();
        } catch (IOException e1) {
            FabricQuiltUtils.displayError("Incompatible mod.", "[${MOD_ID}] This mod is damaged.", true);
        }
    }

    /** 兼容不同 Fabric Loader 版本的字段名/继承层级，失败时返回 null 由调用方给出明确错误。 */
    @SuppressWarnings("unchecked")
    private static Map<String, byte[]> findPatchedClasses() {
        try {
            Object transformer = FabricQuiltUtils.getGameTransformer();
            Class<?> type = transformer.getClass();

            while (type != null) {
                for (String fieldName : new String[] { "patchedClasses", "patchedClassMap" }) {
                    try {
                        Field field = type.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = field.get(transformer);

                        if (value instanceof Map) {
                            return (Map<String, byte[]>) value;
                        }
                    } catch (NoSuchFieldException e) {
                        // 尝试下一个候选字段名
                    }
                }

                type = type.getSuperclass();
            }
        } catch (Throwable t) {
            return null;
        }

        return null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
