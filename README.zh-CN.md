# CrosshairBobbing Fixed

![Client-side](https://img.shields.io/badge/Environment-Client-5b9c51?style=flat)
![Version 1.10.1](https://img.shields.io/badge/Version-1.10.1-5b9c51?style=flat)
[![CurseForge Downloads](https://img.shields.io/badge/CurseForge-Download-5b9c51?style=flat)](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed/files/9498005)
![License GPL-3.0-only](https://img.shields.io/badge/License-GPL--3.0--only-5b9c51?style=flat)

[English](README.md) · [下载](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed/files/9498005) · [更新日志](CHANGELOG.md) · [测试说明](TESTING.md)

**CrosshairBobbing Fixed** 是一款 Minecraft 客户端模组，让准星跟随视角摇晃，并在攻击、挖掘、弓箭蓄力、进食和饮水时播放对应动画。**1.10.1** 是基于 [Krash220 的 Crosshair Bobbing](https://github.com/Krash220/CrosshairBobbingMod) 1.10 的非官方修复版，保留原有动画和配置选项。

## 功能与修复

- **准星动画** 随视角移动，并对潜行、切换物品、暴击以及弓、弩和三叉戟蓄力等动作作出反馈。可在 Mod Menu 或 Forge 配置界面逐项开关。
- **HUD 渲染修复** 适用于 Forge **1.20 / 1.20.1 / 1.20.2**。原版准星与准星位置的攻击指示器同步移动，热键栏、生命值以及在 `Gui.renderCrosshair` 之外绘制的 HUD 元素保持原位。
- **启动和配置修复** 针对特定加载器误判和注入失败处理，配置无效时回退到默认值，并处理特定 CameraOverhaul 版本不匹配。完整条目见[更新日志](CHANGELOG.md)。

## 安装

1. 从 [CurseForge 下载 CrosshairBobbing 1.10.1](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed/files/9498005)。
2. 移除原版 Crosshair Bobbing 和重复副本，将修复版 JAR 放入客户端实例的 `mods/` 文件夹。`-dev.jar` 和 `-sources.jar` 供开发使用。
3. 在游戏设置中启用**视角摇晃**，准星才会跟随视角摇晃。

## 运行要求与兼容性

- 既有游戏内测试覆盖 **Minecraft 1.20.1 / Forge 47.4.28**。测试场景、JAR 哈希和构建结果见[测试说明](TESTING.md)。
- 源码包含 **Minecraft 1.16.2–1.20.2** 范围内的部分 Forge 和 Fabric 适配。Forge 1.17 系列的目标是 **1.17.1**。Fabric 和较旧的 Forge 适配仍可能出现原 HUD 问题。
- NeoForge 与第三方准星、HUD 模组组合仍待验证。Quilt 下会跳过模组功能。Minecraft **1.20.3 及更新版本**不受支持。蓄力条等已知问题见[更新日志](CHANGELOG.md)。

## 反馈与问题报告

请在 [GitHub Issues](https://github.com/WinKing114/CrosshairBobbing-Fixed/issues) 提交问题，并附上 Minecraft 与加载器版本、相关 HUD 模组、复现步骤和 `latest.log`。

### 从源码构建

使用 **JDK 17** 和安装在纯 ASCII 路径下的 **Gradle 7.6**，在仓库根目录执行 `gradle buildMod --no-daemon`。项目沿用上游 FFM1 构建框架，需要本机安装 Gradle。输出位于 `dists/`，其中 `-sources.jar` 包含共享核心与 API 桩。详见[构建验证记录](docs/BUILD-2026-11-11.md)。

## 许可证与署名

原始模组由 **Krash220** 创作。本独立分支由 **WinKing** 按 **GPL-3.0-only** 维护，相关条款和署名见 [LICENSE](LICENSE) 与 [NOTICE](NOTICE)。
