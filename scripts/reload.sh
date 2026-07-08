#!/usr/bin/env bash
#
# Reload this plugin onto a connected device, hands-free.
#
# Why it is shaped this way (learned the hard way against ATAK 5.7.0):
#   * ATAK only reliably picks up new plugin CODE after the old package is
#     uninstalled first - a plain reinstall keeps the previously loaded classes.
#   * After (re)installing while ATAK is running, ATAK pops a "load this plugin?"
#     dialog. It does NOT consistently auto-load on restart, so we keep ATAK
#     running and auto-confirm that dialog with the atak-mcp bridge.
#
# Usage:  scripts/reload.sh
#
set -euo pipefail

PKG="com.atakmap.android.kotlinstarter"
ATAK_PKG="com.atakmap.app.civ"
FLAVOR="Civ"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

# JDK 17 is required by the build. If `java` on your PATH is not 17, export
# JAVA_HOME to a JDK 17 before running this script.
if [[ -z "${JAVA_HOME:-}" ]]; then
    echo "==> JAVA_HOME not set; using java from PATH: $(java -version 2>&1 | head -n1)"
fi

echo "==> Building assemble${FLAVOR}Debug"
./gradlew "assemble${FLAVOR}Debug"

APK="$(ls -t app/build/outputs/apk/civ/debug/*-civ-debug.apk | head -n1)"
echo "==> APK: $APK"

echo "==> Uninstalling $PKG (ok if absent)"
adb uninstall "$PKG" >/dev/null 2>&1 || true

echo "==> Installing (-g grants any declared runtime permissions)"
adb install -r -g "$APK"

# This starter declares no permissions. If you add one, remember that plugin code
# runs in ATAK's process/UID, so ATAK - not this APK - must hold it. Grant it to
# ATAK here, e.g.:
#   adb shell pm grant "$ATAK_PKG" android.permission.ACCESS_FINE_LOCATION || true

echo "==> Auto-confirming ATAK's load-plugin dialog"
ATAK_MCP="git+https://github.com/newtypeind/atak-mcp@v0.2.0"
if command -v uvx >/dev/null 2>&1; then
    uvx --from "$ATAK_MCP" atak-mcp confirm-load --timeout 25 ||
        echo "   (no dialog; open Tools > Kotlin Compose Starter manually if needed)"
else
    echo "   uvx not found - install uv (https://docs.astral.sh/uv/), then open"
    echo "   Tools > Kotlin Compose Starter manually, or run:"
    echo "   uvx --from $ATAK_MCP atak-mcp confirm-load"
fi

echo "==> Done. Open ATAK Tools and tap 'Kotlin Compose Starter'."
