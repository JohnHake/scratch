#!/bin/bash
# Executes passed command for each root in repository

# Ensure a cmd was passed into the script
if [[ -z "$1" ]]; then
  echo "
  Usage:   ./cmd-all.sh <command>
  Example: ./cmd-all.sh git status"
  exit 1
fi

# Get all the inputs and treat them as a single command
USER_CMD=$@
#echo "Received cmd: $USER_CMD"

# If the USER_CMD is a shell script, prefix it with the full path so it will execute in subdirectories
SHELL_EXT='.sh'
if [[ "$USER_CMD" == *"$SHELL_EXT"* ]]; then
  # Prefix the USER_CMD with the absolute path to cmd-all so it can execute in other directories
  USER_CMD="`pwd`/./${USER_CMD}"
  #echo "Updated user command: $USER_CMD"
fi

# The rest is from git-all.sh, with the exception of eval
# Initialize pretty colors if the terminal supports them
if [ -t 1 ]; then
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

# Initialize the starting path
IDE_ROOT="$(cd "`dirname "$0"`"; pwd)"
#echo "IDE_ROOT is: $IDE_ROOT"
# Initialize the array of directories to visit when executing USER_CMD
ROOTS=("/" "/android" "/android/tools-base")

# Iterate over the array of directories, executing USER_CMD and echo'ing pretty colors
for ROOT in ${ROOTS[@]}; do
  (
    cd "${IDE_ROOT}/${ROOT}"
    echo -e ${GREEN}"[$USER_CMD]"${NC} ${BLUE}"$ROOT"${NC}
    eval $USER_CMD
    echo -e ${RED}"["$?"]"${NC} ${BLUE}"$ROOT"${NC}
    echo
  )
done
