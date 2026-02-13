#!/bin/sh
# Maven Wrapper script for Unix
# See https://maven.apache.org/tools/wrapper/

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$SCRIPT_DIR}"
export MAVEN_PROJECTBASEDIR

WRAPPER_JAR="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if [ ! -e "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper JAR..."
  mkdir -p "$(dirname "$WRAPPER_JAR")"
  if command -v curl >/dev/null 2>&1; then
    curl -sL -o "$WRAPPER_JAR" "$WRAPPER_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
  else
    echo "Error: curl or wget required to download maven-wrapper.jar"
    exit 1
  fi
fi

exec java -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
  "-Dmaven.wrapper.jar=$WRAPPER_JAR" \
  -jar "$WRAPPER_JAR" "$@"
