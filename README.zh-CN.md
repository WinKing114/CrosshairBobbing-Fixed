# Crosshair Bobbing fixed（中文说明）

[English README](README.md)

基于 [Krash220/CrosshairBobbingMod](https://github.com/Krash220/CrosshairBobbingMod) 官方 **1.10** 的**非官方修复分支**，版本 **1.10.1**。原作者已于 2024 年停止更新；本分支只修缺陷，不改动画算法、数值与配置键，也不新增游戏版本支持。

## 支持范围

- 先前游戏内实测仅覆盖 **Minecraft 1.20.1 / Forge 47.4.28**。
- 源码含 1.16.2–1.20.2 范围的 Forge / Fabric 适配，不代表所有组合均可用。**Forge 1.17 不支持，对应目标为 1.17.1**；内部目录名 `forge1.17` 保留。NeoForge 未验证。
- **Quilt**：模组功能不生效；源码实现了跳过加载，未做 Quilt 游戏内验证。
- **1.20.3+ / 1.21 / 25.x / 26.x**：不支持，会给出明确错误而不是加载错误实现。

## 修复内容

- Quilt 平台误判、内核类反射注入失败导致的启动崩溃（#11 / #18）：改为优雅跳过。
- 「准星与攻击指示器脱节 / 其它 HUD 乱飞」（MC百科 30 楼、#3 / #6 / #13）：**Forge 1.20 / 1.20.1 / 1.20.2** 上把矩阵变换精确限定在 `Gui.renderCrosshair` 方法体内，vanilla 准星与它自带的 CROSSHAIR 攻击指示器共用同一套变换、一起摇晃旋转（不再脱节）；血条、热键栏、HOTBAR 攻击指示器与第三方 HUD 画在方法之外，不受旋转 / 缩放 / 位移影响。准星动画全部保留，没有靠关动画掩盖问题。
- 配置损坏 / 类型错误导致模组静默失效：回退默认值并重写配置。
- CameraOverhaul 版本不匹配崩溃、矩阵未恢复兜底、Forge 事件重复注册、入口点异常被吞掉。
- 官方仓库里已下线的 TaCZ 依赖导致 `buildMod` 失败。
- 删除改写窗口标题的 `@Overwrite` 调试残留；版本号 / 许可证标注更正（GPL-3.0-only）。

## 安装

从 [CurseForge](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed) 下载正式模组 JAR，移除原版和重复副本，放入客户端实例的 `mods` 文件夹。先前实测组合为 Minecraft 1.20.1 / Forge 47.4.28。不要安装开发包或源码包；视角摇晃动画需要开启游戏的视角摇晃设置。

## 构建

项目沿用上游 FFM1 框架，**没有 gradlew**，需自备 **Gradle 7.6 + JDK 17**。

> **重要**：Gradle 必须放在**纯英文路径**。中文路径会让 JDK 16 toolchain 的 worker `@argfile` 乱码，报 `ClassNotFoundException: SystemApplicationClassLoaderWorker`。

仓库根目录执行 `gradle buildMod --no-daemon`，产物在 `dists/`。`-sources.jar` 只含共享核心 + API 桩，完整源码就是本仓库。

## 验证状态

- 先前游戏内实测**只有 Minecraft 1.20.1 / Forge 47.4.28**；历史编译记录与本轮构建结果分开记录在 [TESTING.md](TESTING.md)。
- 精确 HUD 隔离**只在 Forge 1.20.x 生效**；Fabric 全系与更旧 Forge 版本可能仍有同类问题。
- 第三方准星 / HUD 模组组合未实测；#6 蓄力条、部分上游 issue 仍未解决。

详见 [docs/修复记录.md](docs/修复记录.md) 与 [docs/测试结果.md](docs/测试结果.md)。

## 署名与许可证

- 原作者 **Krash220**，本分支与原作者无关。
- 修复者：**WinKing（GitHub：WinKing114）**。
- **GPL-3.0-only**（见 `LICENSE` 与 `NOTICE`）；本仓库保留完整工程源码和构建逻辑，部分内容的 sources.jar 不能替代本仓库。
