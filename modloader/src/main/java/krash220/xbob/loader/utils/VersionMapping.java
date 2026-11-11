package krash220.xbob.loader.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class VersionMapping {

    private static final int MIN_SUPPORTED = 11602; // 1.16.2
    private static final int MAX_SUPPORTED = 12002; // 1.20.2

    private static final Map<Integer, String> mapping = ImmutableMap.<Integer, String>builder()
            .put(11602, "1.16.2")
            .put(11700, "1.17")
//            .put(11800, "1.18")
            .put(11900, "1.19")
            .put(11903, "1.19.3")
            .put(12000, "1.20")
            .put(12002, "1.20.2")
            .build();

    private static final Pattern RELEASE_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private static final Pattern SNAPSHOT_PATTERN = Pattern.compile("(?:Snapshot )?(\\d+)w0?(0|[1-9]\\d*)([a-z])");

    public static String getSupportedRange() {
        return "1.16.2-1.20.2";
    }

    public static int getNum(String version) {
        Matcher matcher = SNAPSHOT_PATTERN.matcher(version);

        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group(1));
            int week = Integer.parseInt(matcher.group(2));

            // 必须从新到旧并用 else-if：原实现用连续 if，靠后的旧条件会覆盖更新的结果。
            if (year >= 24) {
                return 0; // 1.20.2 之后的快照不在支持范围
            } else if (year == 23 && week >= 31) {
                version = "1.20.2";
            } else if (year == 23 && week >= 12) {
                version = "1.20";
            } else if (year == 23 && week >= 3) {
                version = "1.19.4";
            } else if (year == 22 && week >= 42) {
                version = "1.19.3";
            } else if (year == 22 && week == 24) {
                version = "1.19.1";
            } else if (year == 22 && week >= 11 && week <= 19) {
                version = "1.19";
            } else if (year == 22 && week >= 3 && week <= 7) {
                version = "1.18.2";
            } else if (year == 21 && week >= 37 && week <= 44) {
                version = "1.18";
            } else if ((year == 20 && week >= 45) || (year == 21 && week <= 20)) {
                version = "1.17";
            } else if (year == 20 && week >= 27 && week <= 30) {
                version = "1.16.2";
            } else if (year == 20 && week >= 6 && week <= 22) {
                version = "1.16";
            } else if (year == 19 && week >= 34) {
                version = "1.15";
            } else if ((year == 18 && week >= 43) || (year == 19 && week <= 14)) {
                version = "1.14";
            } else if (year == 18 && week >= 30 && week <= 33) {
                version = "1.13.1";
            } else if ((year == 17 && week >= 43) || (year == 18 && week <= 22)) {
                version = "1.13";
            } else if (year == 17 && week == 31) {
                version = "1.12.1";
            } else if (year == 17 && week >= 6 && week <= 18) {
                version = "1.12";
            } else if (year == 16 && week == 50) {
                version = "1.11.1";
            } else if (year == 16 && week >= 32 && week <= 44) {
                version = "1.11";
            } else if (year == 16 && week >= 20 && week <= 21) {
                version = "1.10";
            } else if (year == 16 && week >= 14 && week <= 15) {
                version = "1.9.3";
            } else if ((year == 15 && week >= 31) || (year == 16 && week <= 7)) {
                version = "1.9";
            } else if (year == 14 && week >= 2 && week <= 34) {
                version = "1.8";
            } else if (year == 13 && week >= 47 && week <= 49) {
                version = "1.7.4";
            } else if (year == 13 && week >= 36 && week <= 43) {
                version = "1.7.2";
            } else if (year == 13 && week >= 16 && week <= 26) {
                version = "1.6";
            } else if (year == 13 && week >= 11 && week <= 12) {
                version = "1.5.1";
            } else if (year == 13 && week >= 1 && week <= 10) {
                version = "1.5";
            } else if (year == 12 && week >= 49 && week <= 50) {
                version = "1.4.6";
            } else if (year == 12 && week >= 32 && week <= 42) {
                version = "1.4.2";
            } else if (year == 12 && week >= 15 && week <= 30) {
                version = "1.3.1";
            } else if (year == 12 && week >= 3 && week <= 8) {
                version = "1.2.1";
            } else if ((year == 11 && week >= 47) || (year == 12 && week <= 1)) {
                version = "1.1";
            }
        }

        matcher = RELEASE_PATTERN.matcher(version);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1)) * 10000 + Integer.parseInt(matcher.group(2)) * 100 + (matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0);
        } else {
            return 0;
        }
    }

    /**
     * 返回对应实现目录名；版本超出支持范围时返回 null，避免把 1.20.2 的实现
     * 套用到 1.21 / 26.x 等不兼容版本上导致 Mixin 目标错乱崩溃。
     */
    public static String get(String version) {
        final int v = getNum(version);

        if (v < MIN_SUPPORTED || v > MAX_SUPPORTED) {
            return null;
        }

        Optional<Entry<Integer, String>> result = mapping.entrySet().stream().filter(e -> e.getKey() <= v).max(Comparator.comparingInt(Entry::getKey));

        return result.isPresent() ? result.get().getValue() : null;
    }
}
