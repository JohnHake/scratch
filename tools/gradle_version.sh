#!/bin/zsh
# Command line script to update gradle version in SDK code_samples
#   Input: $0 - the gradle version number to implement
#   At whatever directory level the script is run:
#     Iteratively searches subdirectories for files named gradle-wrapper.properties
#     Greps the contents of gradle-wrapper.properties files to see if they already specify $0 version.
#     If not, runs the command "./gradlew wrapper --gradle-version='$version' --distribution-type=all"
#       at each parent directory in the list

# Find all the gradle properties files that don't contain the correct version
grep -L $0 code_samples/**/gradle-wrapper.properties
for ROOT in ${ROOTS[@]}; do
  (
    cd "${IDE_ROOT}/${ROOT}"
    echo -e ${GREEN}"[git $@]"${NC}  ${BLUE}"$ROOT"${NC}
    git "$@"
    echo -e ${RED}"["$?"]"${NC} ${BLUE}"$ROOT"${NC}
    echo
  )
done%

