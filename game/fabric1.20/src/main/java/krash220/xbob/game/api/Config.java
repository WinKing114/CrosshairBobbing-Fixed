package krash220.xbob.game.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import krash220.xbob.loader.utils.FabricQuiltUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, Boolean> DEFAULT = new LinkedHashMap<>();
    private static final Map<String, Boolean> CONFIG = new LinkedHashMap<>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void load() {
        File config = new File(FabricQuiltUtils.getConfigDir(), "${MOD_ID}.json");

        if (!config.exists()) {
            save();
        }

        try (FileInputStream fis = new FileInputStream(config)) {
            Object parsed = GSON.fromJson(new InputStreamReader(fis, StandardCharsets.UTF_8), Object.class);

            if (parsed instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) parsed).entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();

                    if (key instanceof String && value instanceof Boolean && DEFAULT.containsKey(key)) {
                        CONFIG.put((String) key, (Boolean) value);
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            // 配置损坏或字段类型不匹配：回退默认值并重写，避免启动时崩溃
            CONFIG.clear();
            CONFIG.putAll(DEFAULT);
            save();
        }
    }

    public static void save() {
        String cfg = GSON.toJson(CONFIG);

        try (FileOutputStream fis = new FileOutputStream(new File(FabricQuiltUtils.getConfigDir(), "${MOD_ID}.json"))) {
            fis.write(cfg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {}
    }

    public static void define(String key, Boolean defaultValue) {
        DEFAULT.put(key, defaultValue);
        CONFIG.put(key, defaultValue);
    }

    public static void set(String key, Boolean value) {
        CONFIG.put(key, value);
    }

    public static boolean check(String key) {
        Boolean val = CONFIG.get(key);

        if (val != null) {
            return val.booleanValue();
        }

        Boolean def = DEFAULT.get(key);

        return def != null && def.booleanValue();
    }

    public static void registerGui() {}

    public static class ModMenu implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return previous -> new ConfigScreen(previous);
        }
    }

    private static class ConfigScreen extends Screen {

        private static final Text VALUE_ENABLE = Text.translatable("${MOD_ID}.config.value.enable").formatted(Formatting.GREEN);
        private static final Text VALUE_DISABLE = Text.translatable("${MOD_ID}.config.value.disable").formatted(Formatting.DARK_RED);

        public static Text getValueText(String key) {
            boolean bool = check(key);

            return Text.translatable("%s: %s", Text.translatable("${MOD_ID}.config." + key), bool ? VALUE_ENABLE : VALUE_DISABLE);
        }

        private Screen previous;

        public ConfigScreen(Screen previous) {
            super(Text.translatable("${MOD_ID}.config.title"));

            this.previous = previous;
        }

        @Override
        public void close() {
            save();

            MinecraftClient.getInstance().setScreen(this.previous);
        }

        @Override
        protected void init() {
            int i = this.height / 6 - 12;

            for (String key : DEFAULT.keySet()) {
                this.addDrawableChild(ButtonWidget.builder(getValueText(key), btn -> {
                    set(key, !check(key));

                    btn.setMessage(getValueText(key));
                }).dimensions(this.width / 2 - 150, i, 300, 20).build());

                i += 24;
            }

            this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }
    }
}
