#!/usr/bin/env bash
set -euo pipefail

VERSION_FILE="gradle.properties"
PROP_KEY="version"

: "${BUMP:=auto}"

git fetch --tags --quiet || true
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "v0.0.0")

if [[ -f "$VERSION_FILE" ]] && grep -q "^${PROP_KEY}=" "$VERSION_FILE"; then
  CURRENT_VERSION=$(grep "^${PROP_KEY}=" "$VERSION_FILE" | head -1 | cut -d'=' -f2 | tr -d '[:space:]')
else
  CURRENT_VERSION="${LAST_TAG#v}"
fi

IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

if [[ "$BUMP" == "auto" ]]; then
  COMMITS=$(git log "${LAST_TAG}..HEAD" --pretty=%s || true)
  BUMP="patch"
  if echo "$COMMITS" | grep -Eiq 'BREAKING CHANGE|!:'; then
    BUMP="major"
  elif echo "$COMMITS" | grep -Eiq '^feat(\(.+\))?:'; then
    BUMP="minor"
  fi
fi

case "$BUMP" in
  major)
    MAJOR=$((MAJOR+1)); MINOR=0; PATCH=0 ;;
  minor)
    MINOR=$((MINOR+1)); PATCH=0 ;;
  patch|*)
    PATCH=$((PATCH+1)) ;;
esac

NEW_VERSION="${MAJOR}.${MINOR}.${PATCH}"
NEW_TAG="v${NEW_VERSION}"

echo "Last tag: ${LAST_TAG}"
echo "Current version: ${CURRENT_VERSION}"
echo "Bump: ${BUMP}"
echo "New version: ${NEW_VERSION} (tag: ${NEW_TAG})"

if [[ -f "$VERSION_FILE" ]]; then
  sed -i.bak -E "s/^(${PROP_KEY}=).*/\1${NEW_VERSION}/" "$VERSION_FILE" || {
    echo "${PROP_KEY}=${NEW_VERSION}" >> "$VERSION_FILE"
  }
else
  echo "${PROP_KEY}=${NEW_VERSION}" > "$VERSION_FILE"
fi

git config --global user.email "${GITLAB_USER_EMAIL:-ci-bot@example.com}"
git config --global user.name  "${GITLAB_USER_NAME:-CI Bot}"
git config --global --add safe.directory "$CI_PROJECT_DIR"

git remote set-url origin "https://oauth2:${GITLAB_TOKEN}@${CI_SERVER_HOST}/${CI_PROJECT_PATH}.git"

git add "$VERSION_FILE"
git commit -m "chore(release): bump version to ${NEW_VERSION} [skip ci]" || echo "Nothing to commit"
git push origin "HEAD:${CI_COMMIT_BRANCH}"

git tag "${NEW_TAG}" "${CI_COMMIT_SHA}"
git push origin "${NEW_TAG}"

echo "Version bumped and tag ${NEW_TAG} pushed."
