# Release notes — v1.10.1

Use this text when creating the GitHub Release for tag `v1.10.1`.

---

First release of **Crosshair Bobbing fixed**, an unofficial bug-fix fork of Krash220's Crosshair Bobbing (based on upstream 1.10).

**Highlights**

- Startup crashes on Quilt / mismatched loaders are now skipped gracefully instead of crashing the game (upstream #11, #18).
- "Progress bars flying around" (MC百科 comment #30, #3 / #6 / #13) **and** the vanilla CROSSHAIR attack indicator being left behind while the crosshair rotates: on Forge 1.20 / 1.20.1 / 1.20.2 the matrix transform is confined to the whole `Gui.renderCrosshair` method body, so the vanilla crosshair and its own attack indicator bob / rotate together, while health bars, the hotbar, the HOTBAR attack indicator and third-party HUD elements (drawn outside that method) are not rotated, scaled or displaced. All crosshair animations are kept.
- Config corruption, CameraOverhaul version mismatches, matrix restore, duplicate event registration, swallowed entrypoint errors and a dead TaCZ build dependency are fixed.

See [CHANGELOG.md](../CHANGELOG.md) for the full list, and [docs/修复记录.md](../docs/修复记录.md) / [docs/测试结果.md](../docs/测试结果.md) for the detailed record (Chinese).

**Supported**: Minecraft 1.16.2 – 1.20.2, Forge / Fabric. Quilt: inactive. 1.21 / 25.x / 26.x: unsupported with a clear error.

**Please read**: in-game tested on Minecraft 1.20.1 Forge only; the precise `renderCrosshair`-scope fix applies to Forge 1.20.x only; third-party crosshair / HUD mod combinations were not tested. The charge bar (#6) and several upstream issues remain unresolved.

**Assets to attach**

- `CrosshairBobbing-1.10.1.jar` — build it from source with `gradle buildMod --no-daemon` (Gradle 7.6 + JDK 17, plain ASCII path), or use the prebuilt artifact from `dists/`. Do **not** commit binaries to the repository. Size: 302088 bytes.
- Verify with `Get-FileHash CrosshairBobbing-1.10.1.jar -Algorithm SHA256` (expected `F1D9ED3DCF42F909636F44E89BD24850A91C83ABD52494502E449BA2565E5E79`).

**License**: GPL-3.0-only. Full corresponding source is this repository at tag `v1.10.1`. This fork is not affiliated with the original author.
