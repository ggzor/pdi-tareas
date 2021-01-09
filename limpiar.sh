#!/usr/bin/env bash

set -euo pipefail

echo 'Limpiando archivos generados...'
find -type f -name '*.class' -delete

