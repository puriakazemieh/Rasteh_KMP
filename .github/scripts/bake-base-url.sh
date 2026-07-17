#!/usr/bin/env bash
# Bake an API base URL into a platform's PlatformConfig by rewriting the single
# uncommented `actual val baseUrl = "..."` line for that source set.
#
# Usage: bake-base-url.sh <sourceSet> <url>
#   sourceSet = androidMain | jvmMain | jsMain | iosMain
#
# Commented alternatives (lines beginning with //) are left untouched, so only
# the active value changes. The URL is normalised to a single trailing slash
# because the Ktor client builds requests as "${baseUrl}api/...".
set -euo pipefail

SRC_SET="${1:?source set required (androidMain|jvmMain|jsMain|iosMain)}"
URL="${2:?url required}"

case "$SRC_SET" in
  androidMain) FILE="core/network/src/androidMain/kotlin/com/kazemieh/network/PlatformConfig.android.kt" ;;
  jvmMain)     FILE="core/network/src/jvmMain/kotlin/com/kazemieh/network/PlatformConfig.jvm.kt" ;;
  jsMain)      FILE="core/network/src/jsMain/kotlin/com/kazemieh/network/PlatformConfig.js.kt" ;;
  iosMain)     FILE="core/network/src/iosMain/kotlin/com/kazemieh/network/PlatformConfig.ios.kt" ;;
  *) echo "unknown source set: $SRC_SET" >&2; exit 2 ;;
esac

# ensure exactly one trailing slash
URL="${URL%/}/"

# rewrite only the active (uncommented) baseUrl line
sed -i -E "s#^([[:space:]]*)actual val baseUrl = .*#\1actual val baseUrl = \"${URL}\"#" "$FILE"

echo "baked base URL into $FILE:"
grep -nE '^[[:space:]]*actual val baseUrl' "$FILE"
