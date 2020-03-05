#!/bin/bash
# Executes passed command for each root in repository

IDE_ROOT="$(cd "`dirname "$0"`"; pwd)"
ROOTS=("/" "/android" "/android/tools-base")

if [ -t 1 ] ; then
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  BLUE='\033[0;34m'
  NC='\033[0m'
else
  RED=''
  GREEN=''
  BLUE=''
  NC=''
fi

for ROOT in ${ROOTS[@]}; do
  (
    cd "${IDE_ROOT}/${ROOT}"
    echo -e ${GREEN}"[git $@]"${NC}  ${BLUE}"$ROOT"${NC}
    git "$@"
    echo -e ${RED}"["$?"]"${NC} ${BLUE}"$ROOT"${NC}
    echo
  )
done