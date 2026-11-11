package krash220.xbob.mixin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import krash220.xbob.loader.utils.FabricQuiltUtils;
import krash220.xbob.loader.utils.VersionMapping;

public class MixinPlugin implements IMixinConfigPlugin {

    private String version;
    private Map<String, List<String>> plugins;

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad(String mixinPackage) {
        // 与 modloader 侧保持一致：Quilt 与不支持的 MC 版本一律不应用任何 Mixin，
        // 避免目标类缺失导致的启动崩溃。
        if (FabricQuiltUtils.isQuilt()) {
            this.version = null;
            return;
        }

        this.version = VersionMapping.get(FabricQuiltUtils.getVersion());

        if (this.version == null) {
            System.err.println("[${MOD_ID}] Unsupported Minecraft version; mixins disabled.");
            return;
        }

        try {
            this.plugins = (Map<String, List<String>>) new Gson().fromJson("<MIXINS_JSON>", TypeToken.getParameterized(Map.class, String.class, TypeToken.getParameterized(List.class, String.class).getType()).getType());
        } catch (JsonIOException | JsonSyntaxException e) {}
    }

    @Override
    public String getRefMapperConfig() {
        return this.version == null ? null : "mixin.${MOD_ID}.refmap." + this.version + ".json";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        if (this.version == null || this.plugins == null) {
            return Collections.emptyList();
        }

        List<String> list = this.plugins.get(this.version);

        return list != null ? list : Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
