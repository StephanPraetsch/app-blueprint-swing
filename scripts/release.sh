#!/usr/bin/env bash
set -euo pipefail

release_type="${1:-}"

case "$release_type" in
  patch|minor|major)
    ;;
  *)
    echo "Usage: ./scripts/release.sh [patch|minor|major]" >&2
    exit 1
    ;;
esac

current_version="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version | tail -n 1)"
current_version="${current_version%-SNAPSHOT}"

if [[ ! "$current_version" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  echo "Expected project.version to be semver, got: $current_version" >&2
  exit 1
fi

major="${BASH_REMATCH[1]}"
minor="${BASH_REMATCH[2]}"
patch="${BASH_REMATCH[3]}"

case "$release_type" in
  patch)
    next_version="$major.$minor.$((patch + 1))"
    ;;
  minor)
    next_version="$major.$((minor + 1)).0"
    ;;
  major)
    next_version="$((major + 1)).0.0"
    ;;
esac

mvn -q versions:set -DnewVersion="$next_version" -DgenerateBackupPoms=false

git add pom.xml
git commit -m "Release v$next_version"
git tag "v$next_version"
git push --follow-tags

echo "Released v$next_version triggered"

