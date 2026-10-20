#!/bin/sh
# Cach dung: sh run.sh [web | demo | reset]   (khong tham so = giao dien console)
cd "$(dirname "$0")"
rm -rf out && mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out src/*.java || exit 1
case "$1" in
  web)   java -cp "out:lib/*" Web ;;
  demo)  java -cp "out:lib/*" Main --demo ;;
  reset) java -cp "out:lib/*" Main --reset-db ;;
  *)     java -cp "out:lib/*" Main ;;
esac
