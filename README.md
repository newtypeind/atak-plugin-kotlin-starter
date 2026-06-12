# ATAK Plugin Kotlin + Compose Starter

A starting point for building ATAK plugins with **Kotlin and Jetpack Compose
(Material 3)** instead of Java and Android XML layouts.

The hard part of using Compose in an ATAK plugin is getting it to render at all:
ATAK's host environment breaks Compose in a few specific ways. This starter ships
a small, validated bridge that solves that, plus a theme, a set of reusable UI
components, and two worked examples that touch the ATAK map. Fork it, rename it,
and start writing screens.

The default screen is a small but real plugin: it shows your own position (MGRS,
lat/lon, GPS fix) read from ATAK's self marker, and drops or removes CoT markers
on the map from a Compose UI.

## Why this exists

The ATAK SDK ships a Compose sample (`samples/plugintemplate-compose`), but it
renders a single static row (an icon and a label) inside a bare `ComposeView`.
That bare approach falls over as soon as the UI does anything real on current ATAK
builds, because:

- the plugin context has a null application context, which NPEs inside Compose's
  graphics layer,
- ATAK's runtime `androidx.lifecycle` has an abstract `DefaultLifecycleObserver.onCreate`,
  so Compose's `AndroidComposeView` crashes with `AbstractMethodError` the moment
  it receives `ON_CREATE`,
- ATAK can wrap a plugin view in its own Fragment, which forces a lifecycle owner
  onto the `ComposeView`.

This starter hosts the `ComposeView` through `ComposeHost` + `SelectiveLifecycle`
(in `app/.../compose/`), which dispatches lifecycle events only to the observer
that needs them and drives recomposition off a private `Recomposer`. The result
is a full Material 3 app, animated theme switching, scrolling, text input, and
dialogs, all running inside an ATAK DropDown.

## What's inside

```
app/src/main/kotlin/com/atakmap/android/kotlinstarter/
  PluginMain.kt              entry point (declared in assets/plugin.xml)
  PluginTool.kt              the Tools-menu button
  PluginMapComponent.kt      registers the DropDown receiver
  PluginDropDownReceiver.kt  hosts the Compose UI in a DropDown
  PluginController.kt        StateFlow state + the only code that talks to ATAK
  compose/
    ComposeHost.kt           hosts ComposeView inside ATAK (the core workaround)
    SelectiveLifecycle.kt    the lifecycle that makes Compose behave in ATAK
    Theme.kt                 Material 3 theme, animated light/dark cross-fade
    Color.kt                 neutral palette color tokens
    Type.kt                  typography
  ui/
    PluginApp.kt             top-level UI: theme wrapper + screen
    components/              the reusable UI kit (see below)
  feature/main/
    MainScreen.kt            stateful wrapper (collects the StateFlow)
    MainScreenContent.kt     stateless screen body (renders in @Preview)
    AppHeader.kt             title + theme toggle
    LocationCard.kt          example: ATAK self position -> Compose
    MarkerCard.kt            example: Compose -> drop a CoT marker on the map
  util/L.kt                  one logging tag
.mcp.json                    registers the atak-mcp MCP server (uvx)
scripts/reload.sh            build + reinstall + auto-load on a device
```

### The UI kit (`ui/components/`)

Generic, theme-aware Material 3 components, each with an `@Preview`. Build screens
from these or copy them as a pattern:

- `PrimaryButton` / `SecondaryButton` / `DestructiveButton` (`Buttons.kt`)
- `LabeledTextField` text input with a label above it
- `SwitchRow` label + Material 3 switch, whole row tappable
- `SectionCard` titled card container with optional icon and trailing slot
- `ListItemRow` leading icon + title/subtitle + trailing, optionally clickable
- `StatusChip` colored status pill (positive / negative / neutral)
- `InlineBanner` persistent info / success / error message strip
- `ConfirmDialog` yes/no dialog, with a destructive variant
- `SegmentedTabs` single-select segmented control (generic over the option type)
- `BusyRow` spinner + label for in-progress states
- `PreviewSupport` shared `@Preview` scaffolding (`PreviewContainer` + sample data)

### The ATAK examples (`feature/main/`)

- **My Location** (`LocationCard`) reads the self marker through `PluginController`
  and shows MGRS + lat/lon + a GPS-fix chip. This is the "ATAK state -> Compose"
  direction.
- **Map markers** (`MarkerCard`) places a CoT marker at your position with ATAK's
  `PlacePointTool.MarkerCreator`, and removes them again behind a confirm dialog.
  This is the "Compose -> ATAK map" direction. The result is an ordinary map item:
  tap it on the map, find it in Overlay Manager, share it over the network.

## Requirements

- **JDK 17**
- **Android SDK** (via Android Studio or the command-line tools)
- **The ATAK SDK** for the ATAK version you target (this starter targets ATAK
  **5.6.0 CIV**). See "Get the ATAK SDK" below.
- A device or emulator with **ATAK CIV installed at the same version** (5.6.0),
  USB debugging enabled
- **adb** on your PATH
- **uv** (optional) for the `atak-mcp` test bridge used by `scripts/reload.sh`

Toolchain versions are pinned and validated together: Gradle 8.14.3, Android
Gradle Plugin 8.13.0, Kotlin 2.2.10, Compose BOM 2025.02.00 (Material 3),
compileSdk 36, minSdk 26. Bumping any of these is a deliberate, test-it change:
the Compose-in-ATAK bridge depends on the bundled `androidx.lifecycle` /
`androidx.savedstate` versions in `app/build.gradle.kts`.

## Get the ATAK SDK

The ATAK SDK is not redistributable, so it is not committed here. Download it from
the official TAK Product Center:

1. Register and sign in at **https://tak.gov** (account approval is required).
2. Find the **ATAK-CIV** downloads and get the **SDK** archive for your target
   version, e.g. `ATAK-CIV-5.6.0-SDK.zip`. While you are there, also download the
   matching **ATAK CIV APK** (5.6.0) and install it on your device.
3. Unzip the SDK somewhere stable. The directory must contain `main.jar` and
   `atak-gradle-takdev.jar` (it also ships `atak-javadoc.jar`, the development
   guide PDF, the debug keystore, and the SDK samples).

The plugin's ATAK API version and the ATAK app on the device must match. To target
a different ATAK version, download that SDK, install that ATAK APK, and set
`ATAK_VERSION` in `app/build.gradle.kts`.

## Configure your machine

Copy the template and fill in your paths. `local.properties` is gitignored, so
your machine-specific paths never get committed:

```bash
cp template.local.properties local.properties
```

Set, at minimum:

- `sdk.dir` the Android SDK directory (e.g. `~/Library/Android/sdk`)
- `sdk.path` the unzipped ATAK SDK directory (the one with `main.jar` +
  `atak-gradle-takdev.jar`)

The build fails with a clear message if either path is missing or wrong. You can
instead build against a TAK Maven repo by setting `takrepo.url` (then `sdk.path`
is not required); see the comments in `template.local.properties`.

Signing is handled automatically for development: the takdev Gradle plugin
generates an ATAK debug keystore into the build directory and signs with it, so no
manual keystore setup is needed for debug builds.

## Build and run

```bash
export JAVA_HOME=<path to your JDK 17>

# Build the debug APK only
./gradlew assembleCivDebug

# Build, reinstall on a connected device, and auto-confirm ATAK's load dialog
./scripts/reload.sh
```

Then in ATAK open the **Tools** menu and tap **Kotlin Compose Starter**. The plugin
opens in a DropDown on the right.

### Why `reload.sh` instead of a plain install

ATAK keeps a plugin's already-loaded classes across a plain reinstall, so your code
changes are ignored unless the package is uninstalled first. ATAK also does not
reliably auto-load a reinstalled plugin while it is running; it shows a "load this
plugin?" dialog instead. `reload.sh` uninstalls, installs, and uses the `atak-mcp`
bridge to confirm that dialog, end to end. If you do not have `uv`/`uvx`, the build
and install still run; just tap to load the plugin yourself.

## Rename it for your plugin

This starter uses the placeholder package `com.atakmap.android.kotlinstarter` and
the name "Kotlin Compose Starter". To make it yours:

1. Rename the package directory `app/src/main/kotlin/com/atakmap/android/kotlinstarter`
   and replace `com.atakmap.android.kotlinstarter` everywhere (the `namespace` in
   `app/build.gradle.kts`, every `package`/`import`, the `impl` in
   `app/src/main/assets/plugin.xml`, and the `SHOW_PLUGIN` action string in
   `PluginDropDownReceiver`).
2. Edit `app/src/main/res/values/strings.xml` (`app_name`, `app_desc`).
3. Edit the `title`/`subtitle` in `MainScreenContent.kt` (or wire them to
   `strings.xml`).
4. Update `PKG` (and the load-message text) in `scripts/reload.sh`, and the tag in
   `util/L.kt`.
5. Optionally rename the project directory; the output APK name follows it.

## How Compose works inside ATAK (deeper)

Start in `compose/SelectiveLifecycle.kt` and `compose/ComposeHost.kt`; both are
heavily commented. In short:

- `SelectiveLifecycle` is a `Lifecycle` that delivers events only to
  `LifecycleEventObserver`s (Compose's `WrappedComposition`, which must see
  `ON_CREATE` to compose) and silently drops `DefaultLifecycleObserver`s (Compose's
  `AndroidComposeView`, whose inherited `onCreate` is abstract on ATAK and would
  throw `AbstractMethodError`).
- `ComposeHost` wires that lifecycle onto the `ComposeView`, feeds recomposition
  from a private `Recomposer` on the main-thread frame clock, and wraps the view in
  a `FrameLayout` so the lifecycle owners ATAK forces onto the DropDown's root land
  on the wrapper, not on the `ComposeView`.
- `PluginDropDownReceiver.ComposeContext` wraps the plugin context (for merged
  plugin + Compose resources) but returns ATAK's real `Application` from
  `getApplicationContext()`.

`app/build.gradle.kts` bundles `androidx.lifecycle` and `androidx.savedstate` (and
keeps `androidx.core`) on purpose, and excludes `fragment`, `okhttp`, and `okio`,
which ATAK provides. Those choices are load-bearing for the bridge; the file
explains each one.

## atak-mcp

Testing is driven by [atak-mcp](https://github.com/newtypeind/atak-mcp), a
standalone open-source tool (Apache-2.0) that controls ATAK over adb without
guessing pixel coordinates: it reads the on-screen tree, finds elements by text,
taps their bounds, screenshots, tails logcat, and reloads plugins. It runs as a CLI
and as an MCP server.

This project consumes it (it is not vendored): `.mcp.json` registers it as an MCP
server for Claude Code, and `scripts/reload.sh` uses its `confirm-load` command.
Both run it with `uvx` (install [uv](https://docs.astral.sh/uv/) first):

```bash
uvx --from git+https://github.com/newtypeind/atak-mcp@v0.2.0 atak-mcp screenshot -o /tmp/s.png
```

The `@v0.2.0` ref pins a reproducible release. Bump it to a newer tag to upgrade.

## Compose @Preview

Every component and the whole main screen have `@Preview`s and render without ATAK,
because state is hoisted into plain data (`PluginController.UiState`) and the ATAK
calls live only in `PluginController`. Use the Android Studio preview pane to build
UI without deploying to a device on every change.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).

Copyright 2026 Newtype Industries.
