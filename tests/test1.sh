#!/usr/bin/env bash

cmd.exe /c start java.exe -cp bin BookServer input.txt
cmd.exe /c start java.exe -cp bin BookClient tests/test1_client1.txt 1
cmd.exe /c start java.exe -cp bin BookClient tests/test1_client2.txt 2

while [[ ! -s out_1.txt ]]; do
  true
done
cat out_1.txt
echo
echo ---------------------------------------------------
while [[ ! -s out_2.txt ]]; do
  true
done
cat out_2.txt
echo
