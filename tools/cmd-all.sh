#!/bin/bash
# Executes passed command for each root in repository

# ($_, an underscore.) At shell startup, set to the absolute pathname used to invoke the shell or shell script being executed as passed in the environment or argument list.
# Use to capture the path, prefix the path to the name of a *.sh command passed into this script.
# OR, have the OS track down the *.sh location

echo "cmd-all.sh PPID is: $$"
# Ensure a cmd was passed into the script
if [[ -z "$1" ]] ; then
  echo "
  Usage:   ./cmd-all.sh <command>
  Example: ./cmd-all.sh git status"
  exit 1
fi

# Get all the inputs and treat them as a single command
USER_CMD=$@
echo $USER_CMD

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
    echo -e ${GREEN}"[$USER_CMD]"${NC}  ${BLUE}"$ROOT"${NC}
    (eval $USER_CMD)
    echo -e ${RED}"["$?"]"${NC} ${BLUE}"$ROOT"${NC}
    echo
  )
done