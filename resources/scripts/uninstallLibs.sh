#!/bin/sh

# Purpose: Uninstall libs previously installed via installLibs
# Usage  : uninstallLibs.sh PATH_TO_LOCAL_REPO
# Example: uninstallLibs.sh /home/username/.m2/repository

if [ $# -ne 1 ]; then
  echo "Error: One argument required (path to .m2/repository)"
  exit 1
fi

if [ -d "$1" ]; then
  rm -rf "$1/org/duraspace"
  rm -rf "$1/org/fedorarepo"
else
  echo "Error: Local repository directory ($1) does not exist"
  exit 1
fi
