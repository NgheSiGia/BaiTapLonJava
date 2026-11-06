#!/bin/sh
# Cach dung: sh run.sh [web | demo | check | reset]   (khong tham so = console). Can ojdbc11.jar trong lib/
cd "$(dirname "$0")"
rm -rf out && mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out src/*.java || exit 1
case "$1" in
  web)   java -cp "out:lib/*" Web ;;
  demo)  java -cp "out:lib/*" Main --demo ;;
  check) java -cp "out:lib/*" Main --check-db ;;
  reset) java -cp "out:lib/*" Main --reset-db ;;
  *)     java -cp "out:lib/*" Main ;;
esac
