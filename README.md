# CrosshairBobbing Fixed 1.10.1

An **unofficial** bug-fix fork of [Krash220/CrosshairBobbingMod](https://github.com/Krash220/CrosshairBobbingMod), based on the upstream **1.10** release. This fork is version **1.10.1**.

It keeps the original design, animation values, config keys and multi-version / multi-loader support, and only fixes defects. The upstream author discontinued development in 2024; **no new Minecraft versions are added**.

[中文说明 / Chinese README](README.zh-CN.md) · [Changelog](CHANGELOG.md) · [Detailed fix notes (中文)](docs/修复记录.md) · [Test report (中文)](docs/测试结果.md)

## Supported

- **Verified in-game (previous report): Minecraft 1.20.1 / Forge 47.4.28 only.**
- The source includes Forge / Fabric adapters within Minecraft 1.16.2–1.20.2; this range is not a promise that every combination works. See [TESTING.md](TESTING.md).
- **Forge 1.17 is unsupported; the corresponding build target is 1.17.1.** The internal directory remains `game/forge1.17` for source compatibility.
- NeoForge has not been verified and is not advertised as supported.
- **Quilt**: mod functionality is inactive. Graceful skipping is implemented, but no Quilt in-game test is claimed.
- **1.20.3+ / 1.21 / 25.x / 26.x**: unsupported — a clear error is shown instead of loading a wrong implementation.

## Features (unchanged from upstream)

- Crosshair follows view bobbing (requires "View Bobbing" enabled)
- Shaking on attacking entities / breaking blocks
- Charging animations for bow, trident and crossbow
- Eating / drinking animation
- Sneaking, item-switch and critical-hit animations
- Per-animation toggles via Mod Menu / Forge config screen

## What's fixed

- Startup crashes caused by Quilt platform misdetection and failed core-class injection ([#11](https://github.com/Krash220/CrosshairBobbingMod/issues/11), [#18](https://github.com/Krash220/CrosshairBobbingMod/issues/18)) — unsupported loaders are now skipped gracefully.
- "Progress bars flying around" (MC百科 comment #30, [#3](https://github.com/Krash220/CrosshairBobbingMod/issues/3) / #6 / #13) **and** the vanilla CROSSHAIR attack indicator being left behind while the crosshair rotates: on **Forge 1.20 / 1.20.1 / 1.20.2** the matrix transform is confined to the whole `Gui.renderCrosshair` method body, so the vanilla crosshair and its own attack indicator bob / rotate together (no longer detached), while health bars, the hotbar, the HOTBAR attack indicator and third-party HUD elements (drawn outside that method) are not rotated, scaled or displaced. **All crosshair animations are kept** — nothing was disabled to hide the bug.
- Corrupted or mistyped config silently disabling the mod — now falls back to defaults and rewrites a valid config file.
- CameraOverhaul version mismatches no longer crash; matrix push/pop is strictly paired with a fallback restore; duplicate Forge event registration and swallowed entrypoint errors fixed.
- Build failure caused by a dead TaCZ Maven dependency in upstream `fabric.gradle`.
- Removed a leftover `@Overwrite` that appended debug text to the window title.
- Version / license metadata corrected (`1.10.1`, GPL-3.0-only); snapshot version mapping fixed; README and en_us typo fixed.

## Installation

1. Install Minecraft **1.20.1 with Forge 47.4.28** for the previously tested combination.
2. Download `CrosshairBobbing-1.10.1.jar` from [CrosshairBobbing Fixed on CurseForge](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed).
3. Remove the original Crosshair Bobbing JAR and other copies of this mod, then put the fixed JAR in the client instance's `mods` folder. Do not install the `-dev.jar` or `-sources.jar` as the mod.
4. Start the client; enable View Bobbing for view-bobbing animation. Other loader/version combinations require separate verification.

## Building

This project uses the upstream FFM1 multi-version build framework. There is **no `gradlew` script**; install Gradle yourself.

1. **JDK 17** to run Gradle; the project targets Java 8 / 16 per module and Gradle provisions the toolchains automatically.
2. Use **Gradle 7.6** (see `gradle/wrapper/gradle-wrapper.properties`).
3. **Important:** place Gradle in a **plain ASCII path**. A path containing non-ASCII characters (e.g. Chinese) breaks the JDK 16 toolchain's worker `@argfile` and fails with `ClassNotFoundException: SystemApplicationClassLoaderWorker`.
4. From the repository root:

   ```
   gradle buildMod --no-daemon
   ```

5. Outputs in `dists/`: `CrosshairBobbing-1.10.1.jar` (the usable mod), plus `-dev.jar` and `-sources.jar` for development. Note `-sources.jar` only contains the shared core + API stubs; the full source is this repository.

`buildMod` runs `validateApi` for every version module. First build downloads Minecraft, Forge, Fabric and Yarn dependencies and takes several minutes.

## Verification status (please read)

- Previous in-game testing covers **Minecraft 1.20.1 / Forge 47.4.28 only**. Historical module compilation and the current build attempt are distinguished in [TESTING.md](TESTING.md).
- The precise `renderCrosshair`-scope fix applies **only to Forge 1.20 / 1.20.1 / 1.20.2**. Fabric builds and older Forge builds still use the previous injection and may still show the original bar problems.
- Combinations with third-party crosshair / HUD mods (Dynamic Crosshair, Immersive Aircraft, RRLS, Shoulder Surfing, Custom Crosshairs, Inventory HUD+, TaCZ, CameraOverhaul) were **not** tested.
- Still unresolved: charge-bar behaviour (#6), some third-party HUD conflicts, and issue #16 (crash log unavailable).

See [docs/测试结果.md](docs/测试结果.md) for the full layered test report.

## Credits & license

- Original mod by **Krash220** — <https://github.com/Krash220/CrosshairBobbingMod>. This fork is **not affiliated with** the original author.
- Fix maintainer: **WinKing ([WinKing114](https://github.com/WinKing114))**.
- **GPL-3.0-only** — see [LICENSE](LICENSE) and [NOTICE](NOTICE). This repository contains the complete project source and build logic; the partial `-sources.jar` alone is not the complete project.
- The upstream `mod.properties` mistakenly said MIT; this fork corrects it to GPL-3.0-only to match the repository LICENSE.
