#!/usr/bin/env bash

git add .
git commit -m "$1"
if [ $2 = "force" ]; then
    git push -u --force origin main
else
    git push -u origin main
fi