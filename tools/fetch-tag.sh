#!/bin/bash
#
# Fetches the given tag and creates it locally.
# Usage: ./fetch-tag.sh <name-of-tag>
# Example: ./fetch-tag.sh idea/183.4139.22

# Ensure a tag was passed into the script
if [[ -z "$1" ]]; then
  echo "
  Usage:   ./fetch-tag.sh <name-of-tag>
  Example: ./fetch-tag.sh idea/183.4139.22"
  exit 1
fi
TAG="$1"
echo "TAG is: $TAG"

# Any command which returns non-zero exit code will cause this shell script to exit immediately
#set -e

# Define array of github repositories, one matching each directory name - order is important
declare -ar REPOS=("https://github.com/JetBrains/intellij-community.git" "git://git.jetbrains.org/idea/android.git" "git://git.jetbrains.org/idea/adt-tools-base.git")

# Capture the name of the current local directory
# LOCAL_DIR=$( pwd | grep -o "/[A-Za-z0-9]\+\$" )
# echo "LOCAL_DIR is: $LOCAL_DIR"

echo `ps`

#ppid="`ps -p "$$" -o ppid=`"
##lsof -nP -p "$ppid" | awk 'NR==4 {print $NF; exit}'
#echo "ppid is: $ppid"
#echo `lsof -nP -p "$ppid"`

#PARENT_PROC_NAME=${ ps -o comm= $PPID; }
#echo "Name of calling process is: $PARENT_PROC_NAME"
#
#PARENT_PPID=${ ps -p $$ -o ppid=; }
#ALT_PARENT_PROC_NAME=${ lsof -nP -p $PARENT_PPID | awk 'NR==3'; }
#echo "ALT_PARENT_PROC_NAME is: $ALT_PARENT_PROC_NAME"
exit 0

# Assign a repository based on the directory name
case $ROOT in
${ROOTS[2]}) REPO=${REPOS[2]} ;;
${ROOTS[1]}) REPO=${REPOS[1]} ;;
${ROOTS[0]}) REPO=${REPOS[0]} ;;
*) REPO="" ;;
esac

# Fetch the tag from the repository and define it locally
echo "fetch $REPO tag $TAG --no-tags"
git fetch $REPO tag $TAG --no-tags
