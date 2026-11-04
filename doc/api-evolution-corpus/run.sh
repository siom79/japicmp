#!/usr/bin/env bash
# Evaluates japicmp against the API evolution data corpus of
# K. Jezek and J. Dietrich, "API evolution and compatibility: A data corpus and tool evaluation",
# Journal of Object Technology, vol. 16, no. 4, pp. 2:1-23, 2017.
#
# Usage: build japicmp first (mvn -pl japicmp -am install -DskipTests), then run
#   doc/api-evolution-corpus/run.sh [work-dir]
set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$HERE/../.." && pwd)"
WORK="${1:-$ROOT/target/api-evolution-corpus}"
JAPICMP_JAR="$(ls "$ROOT"/japicmp/target/japicmp-*-jar-with-dependencies.jar | head -n 1)"
CORPUS="$WORK/api-evolution-data-corpus"

mkdir -p "$WORK"
if [ ! -d "$CORPUS" ]; then
  git clone https://github.com/kjezek/api-evolution-data-corpus.git "$CORPUS"
fi

for v in 1 2; do
  rm -rf "$WORK/classes-v$v" && mkdir -p "$WORK/classes-v$v"
  javac -nowarn --release 8 -d "$WORK/classes-v$v" $(find "$CORPUS/lib-v$v/src" -name '*.java')
  jar cf "$WORK/lib-v$v.jar" -C "$WORK/classes-v$v" .
done

mkdir -p "$WORK/eval"
javac -cp "$JAPICMP_JAR" -d "$WORK/eval" "$HERE/CorpusEvaluation.java"
java -cp "$JAPICMP_JAR:$WORK/eval" CorpusEvaluation "$WORK/lib-v1.jar" "$WORK/lib-v2.jar" "$WORK/result.csv"
python3 "$HERE/compare.py" "$CORPUS/compatibility.csv" "$WORK/result.csv"
