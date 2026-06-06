#!/usr/bin/env bash

set -e

SRC_ROOT="."
OUT_DIR="target/classes"
JAR_FILE="target/jenin.jar"
MAIN_CLASS="org.jenin.sr.Main"

mkdir -p "$OUT_DIR"

find "$SRC_ROOT/org" -name "*.java" > sources.txt

javac -d "$OUT_DIR" @sources.txt

rm sources.txt

MANIFEST_FILE="target/MANIFEST.MF"
echo "Main-Class: $MAIN_CLASS" > "$MANIFEST_FILE"

jar cfm "$JAR_FILE" "$MANIFEST_FILE" -C "$OUT_DIR" .

echo "Built: $JAR_FILE"
echo "Run with: java -jar $JAR_FILE <file.jn>"

# Compile root-level native module (no package)
if [ -f "FileModule.java" ]; then
  javac -cp "$JAR_FILE" FileModule.java
  echo "Compiled: FileModule.class"
fi

# Compile all nativemods found under any directory tree.
# Each nativemods/ directory is compiled with its PARENT as -d (so the
# package sub-directory is placed correctly for URLClassLoader to find).
find . -path "*/nativemods/*.java" ! -path "./org/*" | while read javafile; do
  parentdir="$(dirname "$(dirname "$javafile")")"
  javac -cp "$JAR_FILE" -d "$parentdir" "$javafile" \
    && echo "Compiled: $javafile -> $parentdir/nativemods/"
done
