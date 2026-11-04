#!/usr/bin/env bash
# Compile and launch Auto Repair Shop. Run from anywhere; works from the project root.
set -e
cd "$(dirname "$0")"
javac -d out src/*.java
java -cp out AutoShop
