#!/usr/bin/env bash

set -euo pipefail

# Este script ejecuta la tarea proporcionada

if [[ $# -eq 0 ]]; then
  cat <<EOF
No se proporcionó el número de tarea a ejecutar

Ejemplo:
$ ./ejecutar_tarea.sh 1

Ejecuta la tarea número 1
EOF

  exit 1
fi

ARCHIVO="tarea$1/Main.java"
CLASE="tarea$1.Main"

if [[ ! -f $ARCHIVO ]]; then
  echo "No existe la tarea $1 (aún?)"
  exit 1
fi

# Compilar y ejecutar
javac "$ARCHIVO" && java "$CLASE"

