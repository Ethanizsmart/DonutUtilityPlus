# SeaATS Auto Sell — restored Fabric project

Minecraft **1.21.11**, Java **21**, mod ID **seaats**, version
**1.8.5+backgroundgui**. This project restores the supplied CFR 0.152 sources.

Build and artifact verification succeeded. See [the restoration report](RESTORATION_REPORT.md)
for the changed files, logs, checksums and remaining verification limits.

## Build on Windows

Install a JDK 21 (for example [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21))
and set `JAVA_HOME` to its directory, not its `bin` directory. In PowerShell:

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
.\gradlew.bat build
```

On the restoration machine a checksum-verified portable Temurin JDK is available:

```powershell
$env:JAVA_HOME = (Resolve-Path '.tools/jdk-21.0.12.1+1').Path
New-Item -ItemType Directory -Force .tools/gradle-home, .tools/tmp | Out-Null
$env:GRADLE_USER_HOME = (Resolve-Path '.tools/gradle-home').Path
$env:TEMP = (Resolve-Path '.tools/tmp').Path
$env:TMP = $env:TEMP
.\gradlew.bat build
```

The restoration machine's C: drive ran out of space when the wrapper attempted
to extract Gradle in the default user cache. The commands above keep Gradle's
cache and temporary files on D: inside this workspace. They only affect the
current PowerShell session. `.tools/` is local tooling, not part of the mod.

The wrapper downloads Gradle 9.2.1 and verifies its distribution SHA-256.
The first build requires Internet access to download build tools, Minecraft,
and dependencies. No mod entrypoint is executed by `build`.

Install `build/libs/SeaAutoSell-1.8.5+backgroundgui.jar` in a Minecraft 1.21.11
Fabric client with Fabric API. The `-sources.jar` is for source inspection.
Use Java 21, Fabric Loader 0.18.4 and Fabric API 0.141.3+1.21.11 for the
dependency versions used to compile this restoration.

## Mappings and dependencies

The supplied sources reference `net.minecraft.class_*`, `method_*` and `field_*`:
these are **Fabric Intermediary** names. The original development mappings
(Yarn, Mojang, or custom) cannot be inferred from a production intermediary JAR.
This build does not migrate sources to another namespace.

`build.gradle` resolves `net.fabricmc:intermediary:1.21.11:v2` and adds a `named`
column identical to `intermediary` in its Tiny v2 mapping file. The generated
mapping JAR lives in `.gradle/intermediary-mappings/` and is regenerated from
the published mapping artifact during configuration. Loom can therefore compile
Minecraft and Fabric API against the same names already present in these sources.
The final `remapJar` output is in the production intermediary namespace.

| Component | Version |
| --- | --- |
| Gradle Wrapper | 9.2.1 |
| Fabric Loom | 1.14.10 |
| Minecraft | 1.21.11 |
| Intermediary mappings | 1.21.11, classifier `v2` |
| Fabric Loader | 0.18.4 |
| Fabric API | 0.141.3+1.21.11 |
| Java toolchain / bytecode target | 21 |

Gson, Brigadier and JOML come from Minecraft's dependency graph. Java's HTTP
client is part of the JDK. No Cloth Config, Mod Menu, or additional webhook
library is required by the supplied imports.

References: [Fabric 1.21.11 development guidance](https://www.fabricmc.net/2025/12/05/12111.html),
[Fabric mappings documentation](https://wiki.fabricmc.net/tutorial:mappings).

## Source layout and behavior

- `com.seastore.auto.seaats`: `Config`, `ConfigPathCompat`, `ModConfig` — defaults,
  config location, compatibility fields, JSON load and atomic save.
- `SeaatsClient`: the registered client entrypoint; keybindings, `/homeset`,
  lifecycle, tick, chat and HUD callbacks.
- `AutoSellLogic`: sale GUI recovery/retry, item movement and confirmation,
  background focus handling, home recovery, ghost recovery, nearby-player
  protection and server-confirmed payments.
- `SellCycleScheduler`: four shuffled cycles, timing and selected delay.
- `ChatMoneyListener`, `PayBalanceReply`, `PayReceipt`: sale/balance/payment
  message parsing and confirmation matching.
- `ChatCommandSender`: the original chat-based command submission.
- `ScreenHotkeys`, `ModConfigScreen`, `SeaSettingsScreen`, `SeaButton`: F8/F9,
  key-repeat suppression, settings fields, validation and controls.
- `HudTracker`: statistics, status and tracker scaling.
- `WebhookHelper`: asynchronous report, alert and payment notifications.
- `Seaats`: retained empty initializer; it is not an entrypoint in the supplied metadata.

All 17 top-level source files are retained. `fabric.mod.json` still registers
only `com.seastore.auto.seaats.client.SeaatsClient`, uses client environment,
and has an empty `mixins` array. Both supplied mixin configs have empty class
lists and remain packaged as resources. There are no missing mixin classes
indicated by those configs. The original manifest attributes are retained;
Loom adds its build metadata to the output manifest.

Existing behavior is not redesigned: for example `WebhookHelper.sendProfit`
is already empty in the original JAR, while `sendReport` sends profit reports.
The HUD's CFR warning about a removed self-catching try block is retained;
its matrix cleanup remains in `finally`. Existing configuration persistence
and webhook logging are unchanged.

This restoration does not launch Minecraft, connect to a game server, read a
player's runtime configuration, or call a webhook. Compilation and artifact
verification do not establish full in-game behavioral equivalence.
