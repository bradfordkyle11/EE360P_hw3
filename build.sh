#!/usr/bin/env bash

for file in *.java; do
  echo $file
  javac.exe -d bin $file
done
