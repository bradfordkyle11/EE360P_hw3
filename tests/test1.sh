#!/usr/bin/env bash

cmd.exe /c start java.exe -cp bin BookServer input.txt

cmd.exe /c start java.exe -cp bin BookClient tests/test1_client1.txt 1 \& start java.exe -cp bin BookClient tests/test1_client2.txt 2

cat out_1.txt
echo ---------------------------------------------------
cat out_2.txt
