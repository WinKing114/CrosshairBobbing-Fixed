# Changelog

All notable changes to this fork. Versions are based on the upstream release they fork from.

## Source publication documentation — 2026-11-11

- Added maintainer attribution, installation instructions and a separate TESTING.md.
- Clarified that Forge 1.17 is unsupported (the target is 1.17.1), Quilt functionality is inactive, and prior game testing covers only 1.20.1 / Forge 47.4.28.
- No implementation changes in this documentation update.

## 1.10.1

First release of this unofficial fix branch, based on upstream 1.10.

### Fixed

- Startup crashes from Quilt platform misdetection and failed core-class injection (upstream issues #11, #18): unsupported loaders are now skipped gracefully instead of crashing the game.
- "Progress bars flying around" (MC百科 comment #30, issues #3 / #6 / #13) **and** the vanilla CROSSHAIR attack indicator being left behind while the crosshair rotates: on Forge 1.20 / 1.20.1 / 1.20.2 the matrix transform is now confined to the whole `Gui.renderCrosshair` method body, so the vanilla crosshair and its own attack indicator bob / rotate together (no longer detached), while health bars, the hotbar, the HOTBAR attack indicator and third-party HUD elements (drawn outside that method) are not rotated, scaled or displaced. All crosshair animations are preserved — nothing was disabled to hide the bug.
- Corrupted or mistyped config silently disabling the mod: now falls back to defaults and rewrites a valid config file.
- Crashes with mismatched CameraOverhaul versions, duplicate Forge event registration, un-restored matrix stack (fallback restore added), and swallowed entrypoint exceptions.
- Build failure caused by a dead TaCZ Maven dependency in upstream `fabric.gradle`.
- Removed a leftover `@Overwrite` that appended debug text to the window title.

### Changed

- Version metadata set to 1.10.1; license corrected to GPL-3.0-only (matching the repository LICENSE).
- Unsupported Minecraft versions now show a clear error instead of loading the wrong core; snapshot version mapping corrected.
- Added build instructions and support notes to README; fixed a typo in `en_us.json`.

### Known limitations

- In-game verified on Minecraft 1.20.1 Forge only; other versions are compile-tested, not play-tested.
- The precise `renderCrosshair`-scope fix applies to Forge 1.20.x only — Fabric builds and older Forge builds may still show the original bar problems.
- Third-party crosshair / HUD mod combinations were not tested; the charge bar (#6) and several upstream issues (#12, #13, #14, #16) remain unresolved.

See [docs/修复记录.md](docs/修复记录.md) and [docs/测试结果.md](docs/测试结果.md) for the full record (Chinese).
