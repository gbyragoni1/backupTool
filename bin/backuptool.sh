#!/bin/bash
# This script is a wrapper for the Java backup tool.
if [ -z "$1" ]; then
  echo "Usage: backuptool snapshot |restore|list|prune"
  echo "backuptool snapshot --target-directory=~/my_important_files"
  echo "backuptool list"
  echo "backuptool restore --snapshot-number=42 --output-directory=./out"
  echo "backuptool prune --snapshot=42"
  
  exit 1
fi

java -jar ../target/backup-tool-0.0.1-SNAPSHOT.jar $1 $2 $3
