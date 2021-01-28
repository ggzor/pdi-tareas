#!/usr/bin/env bash

set -euo pipefail

# Este script ejecuta la tarea proporcionada

if [[ $# -eq 0 ]]; then
  cat <<EOF
No se proporcionó la tarea a ejecutar

Ejemplo:
$ ./ejecutar_tarea.sh visor

Ejecuta la tarea visor

Tareas disponibles:
  - visor
EOF

  exit 1
fi

ARCHIVO="$1/Main.java"
CLASE="$1.Main"
CLASSPATH_EXTRA='./:lib/*'

if [[ ! -f $ARCHIVO ]]; then
  echo "No existe la tarea $1 (aún?)"
  exit 1
fi

# Compilar y ejecutar
./limpiar.sh && javac -cp "$CLASSPATH_EXTRA" "$ARCHIVO" \
             && java -cp "$CLASSPATH_EXTRA" "$CLASE"

