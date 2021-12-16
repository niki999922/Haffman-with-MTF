#!/bin/zsh

rm ./lab2/jpeg30/*.ppm
cp ./archiver/build/libs/archiver.jar ./lab2/jpeg30/
#rm ./lab2/jpeg80/*.ppm
#cp ./archiver/build/libs/archiver.jar ./lab2/jpeg80/
start="$(pwd)"
cd lab2/jpeg30/
#cd lab2/jpeg80/
./script_bwt.sh
cd "$start"
rm -rf /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test
mkdir -p /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test
cp ./lab2/jpeg30/*.ppm /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test
#cp ./lab2/jpeg80/*.ppm /Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/just_test