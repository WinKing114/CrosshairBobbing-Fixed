# CrosshairBobbing Fixed

![Client-side](https://img.shields.io/badge/Environment-Client-5b9c51?style=flat)
![Version 1.10.1](https://img.shields.io/badge/Version-1.10.1-5b9c51?style=flat)
[![CurseForge Downloads](https://img.shields.io/badge/CurseForge-Download-5b9c51?style=flat)](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed/files/9498005)
![License GPL-3.0-only](https://img.shields.io/badge/License-GPL--3.0--only-5b9c51?style=flat)

[简体中文](README.zh-CN.md) · [Download](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed/files/9498005) · [Changelog](CHANGELOG.md) · [Testing](TESTING.md)

**CrosshairBobbing Fixed** is a client-side Minecraft mod that makes the crosshair follow view bobbing and react when you attack, break blocks, charge a bow, eat, or drink. Version **1.10.1** is an unofficial bug-fix fork of [Krash220's Crosshair Bobbing](https://github.com/Krash220/CrosshairBobbingMod) 1.10 and retains the original animations and configuration options.

## What's Included

- **Crosshair animations** follow camera movement and react to actions including sneaking, item switching, critical hits, and charging a bow, crossbow, or trident. You can toggle individual animations in Mod Menu or the Forge config screen.
- **HUD rendering fix** on Forge **1.20 / 1.20.1 / 1.20.2**. The crosshair and its vanilla attack indicator share the same movement. The hotbar, health bars, and HUD elements drawn outside `Gui.renderCrosshair` stay in place.
- **Startup and configuration fixes** address specific loader-detection and injection failures, recover invalid config files with defaults, and guard against certain CameraOverhaul version mismatches. See the [changelog](CHANGELOG.md) for the full list.

## Installation

1. Download [CrosshairBobbing 1.10.1 on CurseForge](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed/files/9498005).
2. Remove the original Crosshair Bobbing JAR and any duplicate copies. Install the fixed JAR in your client instance's `mods/` folder. The `-dev.jar` and `-sources.jar` are for development.
3. Enable **View Bobbing** in Minecraft for the crosshair to follow camera bobbing.

## Requirements & Compatibility

- **Previously tested in game:** Minecraft **1.20.1 / Forge 47.4.28**. See [Testing](TESTING.md) for the tested scenarios, binary hash, and separate build results.
- **Forge and Fabric source adapters** cover selected versions in the Minecraft **1.16.2–1.20.2** range. The Forge 1.17-series target is **1.17.1**. Older Forge and Fabric adapters may still show the original HUD issue.
- **NeoForge** and third-party crosshair/HUD mod combinations remain unverified. **Quilt** skips mod activation. Minecraft **1.20.3 and later** are unsupported. Charge-bar behavior and other open items are listed in the [changelog](CHANGELOG.md).

## Feedback & Issue Reporting

Report issues on [GitHub](https://github.com/WinKing114/CrosshairBobbing-Fixed/issues). Include the Minecraft and loader versions, relevant HUD mods, steps to reproduce, and your `latest.log`.

### Building from Source

Use **JDK 17** and **Gradle 7.6** installed in an ASCII-only path, then run `gradle buildMod --no-daemon` from the repository root. The project uses the upstream FFM1 build framework and requires a local Gradle installation. Build output goes to `dists/`, and the `-sources.jar` contains the shared core and API stubs. See [build verification](docs/BUILD-2026-11-11.md) for details.

## License & Credits

Original mod by **Krash220**. This independent fork is maintained by **WinKing** under **GPL-3.0-only**. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
