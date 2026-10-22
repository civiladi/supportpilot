#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: ./gitpush.sh \"your commit message\""
  exit 1
fi

git add . || exit 1

git commit -m  "$1" || exit 1

git push origin HEAD || exit 1