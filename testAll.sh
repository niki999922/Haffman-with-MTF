#!/bin/zsh

rm ./lab2/jpeg30/*.ppm
cp ./archiver/build/libs/archiver.jar ./lab2/jpeg30/
start="$(pwd)"
cd lab2/jpeg30/
./script_bwt.sh
cd "$start"
rm -rf /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test
mkdir -p /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test
cp ./lab2/jpeg30/*.ppm /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test