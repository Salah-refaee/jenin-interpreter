#!/usr/bin/env bash
# -*- coding: utf-8 -*-
# a shortcut to run the jenin scripts
# usage: ./jenin.sh path/to/script.jn

jar="target/jenin.jar"

if [ -z "$1" ]; then
  echo "usage: ./jenin.sh path/to/script.jn"
  exit 1
fi
if [ ! -f "$1" ]; then
  echo "file not found: $1"
  exit 1
fi

java -jar $jar "$1"