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
#echo "TAG is: $TAG"

# Any command which returns non-zero exit code will cause this shell script to exit immediately
set -e

# Define array of github repositories, one matching each directory name - order is important
declare -ar REPOS=("https://github.com/JetBrains/intellij-community.git" "git://git.jetbrains.org/idea/android.git" "git://git.jetbrains.org/idea/adt-tools-base.git")
declare -ar DIRS=("/" "/android" "/tools-base")

# Capture the name of the current local directory
CURR_DIR=$(pwd | grep -oE "/[A-Za-z0-9\-]+$")
#echo "CURR_DIR is: $CURR_DIR"

# Assign a repository based on the directory name
case $CURR_DIR in
${DIRS[2]}) REPO=${REPOS[2]} ;;
${DIRS[1]}) REPO=${REPOS[1]} ;;
*) REPO=${REPOS[0]} ;;           # Hack to handle the sdk root dir, whatever the user named it
esac

# Fetch the tag from the repository and define it locally
#echo "git fetch $REPO tag $TAG --no-tags"
git fetch $REPO tag $TAG --no-tags
