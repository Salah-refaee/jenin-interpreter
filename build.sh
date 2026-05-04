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
