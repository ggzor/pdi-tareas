#!/usr/bin/bash

readarray -t OPERACIONES <<< "$( java regionales.Main | sed -n 's/^ *- \(\w\+\)/\1/p' )"

if (( "$#" != 2 )); then
  echo "Uso: $0 ORIGEN DESTINO"
  exit
fi

mkdir -p "$2"

for op in "${OPERACIONES[@]}"; do
  mkdir -p "$2/$op"
  java regionales.Main "$op" "$1" "$2/$op" &
done

wait
