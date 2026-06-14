#!/bin/sh
set -e
if [ -f "./gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -jar ./gradle/wrapper/gradle-wrapper.jar "$@"
fi
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
cat >&2 <<'MSG'
Gradle is required to build the Kotlin framework.
Install it with: brew install gradle
Then run: gradle wrapper --gradle-version 8.14.3
After that, Xcode can call ./gradlew normally.
MSG
exit 1
