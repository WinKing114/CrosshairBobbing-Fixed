# Testing and build verification

Updated: 2026-11-11. Compilation, loading and gameplay are separate evidence levels.

## Previous in-game evidence

Only **Minecraft 1.20.1 / Forge 47.4.28** has the previously reported in-game tests. See [the historical report](docs/测试结果.md), especially section 9 for the final attack-indicator fix. Those tests were performed before this source-publication task; no new game session is claimed here.

The report covers loading/world entry, crosshair animation, the vanilla CROSSHAIR attack indicator following the crosshair, HOTBAR/HUD isolation, and the recorded configuration combinations. It is not a test of every third-party mod combination, multiplayer, or a dedicated server.

The final previously tested binary is `CrosshairBobbing-1.10.1.jar`, 302088 bytes, SHA-256:

```text
f1d9ed3dcf42f909636f44e89bd24850a91c83abd52494502e449ba2565e5e79
```

Older binaries/hashes in the historical report refer to superseded iterations.

## Compilation is not gameplay verification

The prior report records a successful `buildMod` across the version modules. These are build targets, not a list of game-tested versions:

| Loader | Module / mapping targets | In-game status |
| --- | --- | --- |
| Forge | 1.16.5, **1.17.1**, 1.19, 1.19.3, 1.20.1, 1.20.2 | Only 1.20.1 / Forge 47.4.28 previously tested |
| Fabric | See `game/fabric*/gradle.properties` for the exact pinned targets | No in-game validation reported |
| Quilt | Functionality inactive; loader skip implemented | Not gameplay tested |
| NeoForge | No separately validated target | Not tested |

**Forge 1.17 is unsupported.** The internal `game/forge1.17` directory builds against Minecraft **1.17.1 / Forge 37.1.1**. Directory names are implementation selectors and are not a precise support matrix. Likewise, the 1.20.1 Forge compile dependency is 47.0.34, while the previous game test used 47.4.28.

The refined HUD scope applies to the Forge 1.20 / 1.20.1 / 1.20.2 adapter family. Fabric and older Forge adapters retain earlier injection behavior. Third-party crosshair/HUD combinations and unresolved upstream issues are listed in [CHANGELOG.md](CHANGELOG.md).

## Current build attempt

The dated result is recorded in [docs/BUILD-2026-11-11.md](docs/BUILD-2026-11-11.md). Use **Gradle 7.6** with **JDK 17** to run:

```text
gradle buildMod --no-daemon
```

Install Gradle under an ASCII-only path. The existing build provisions additional Java toolchains for older modules. It downloads dependencies and uses dynamic plugin versions, so a successful local build does not establish an offline or byte-for-byte reproducible build. Generated `build/` trees, caches and game instances are excluded; their source templates and custom Gradle build logic are included.

## Source provenance

This is the existing modified 1.10.1 project, not a renamed upstream release or reconstructed decompilation. The local source history used for publication is:

- Upstream base: `67608daea2e8c2c24cee91a595412f8b295c4f91` in [Krash220/CrosshairBobbingMod](https://github.com/Krash220/CrosshairBobbingMod/commit/67608daea2e8c2c24cee91a595412f8b295c4f91).
- Local first fix: `d257c7df8f4272300c8eebd37cd2cfc8f794bcd6`.
- Local final implementation: `a8ae9e915749a19f7647b89ef3d1658aefcec4d6`.

Publication updates documentation and ignore rules only. The new GitHub repository imports a source snapshot; its publication commit IDs differ from the pre-existing local history. Original authorship and GPL-3.0-only notices remain intact.
