package krash220.xbob;

import krash220.xbob.game.api.Config;

/**
 * Central configuration keys and registration for CrosshairBobbing.
 * Preserves all existing 1.10.1 boolean keys in {@code config/xbob.json}.
 */
public final class CrosshairConfig {

    public static final String BOB = "bob";
    public static final String SNEAK = "sneak";
    public static final String ITEM = "item";
    public static final String AIM = "aim";
    public static final String SPEAR = "spear";
    public static final String XBOW = "xbow";
    public static final String EAT = "eat";
    public static final String ATK = "atk";

    private CrosshairConfig() {
    }

    public static void init() {
        Config.define(BOB, true);
        Config.define(SNEAK, true);
        Config.define(ITEM, true);
        Config.define(AIM, true);
        Config.define(SPEAR, true);
        Config.define(XBOW, true);
        Config.define(EAT, true);
        Config.define(ATK, true);
        Config.load();
        Config.registerGui();
    }

    public static boolean isEnabled(String key) {
        return Config.check(key);
    }
}
