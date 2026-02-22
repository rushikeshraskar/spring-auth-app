#!/usr/bin/env bash
set -e

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: ./cut-release.sh <base-branch> <new-branch> [<current-version> <new-version>]"
  exit 1
fi

BASE_BRANCH="$1"
NEW_BRANCH="$2"
CURRENT_VERSION="$3"
NEW_VERSION="$4"

echo "🔄 Fetching latest from origin..."
git fetch origin

# Validate base branch exists
if ! git show-ref --verify --quiet refs/remotes/origin/$BASE_BRANCH; then
  echo "❌ Base branch does not exist: $BASE_BRANCH"
  exit 1
fi

# Delete local leftover branch
if git show-ref --verify --quiet refs/heads/$NEW_BRANCH; then
  echo "⚠️ Local branch exists, deleting: $NEW_BRANCH"
  git branch -D $NEW_BRANCH
fi

echo "📌 Checking out base branch: $BASE_BRANCH"
git checkout $BASE_BRANCH
git pull origin $BASE_BRANCH

git remote prune origin

echo "🌱 Creating new branch: $NEW_BRANCH"
git checkout -b $NEW_BRANCH

if [ -n "$CURRENT_VERSION" ] && [ -n "$NEW_VERSION" ]; then
  echo "🔁 Replacing version occurrences in pom.xml files: $CURRENT_VERSION -> $NEW_VERSION"

  # Collect tracked pom.xml files
  mapfile -t pom_files < <(git ls-files | grep -E '^(.*/)?pom.xml$' || true)

  modified=()
  for f in "${pom_files[@]}"; do
    if grep -Fq "${CURRENT_VERSION}" "$f"; then
      perl -0777 -pe "s/\Q${CURRENT_VERSION}\E/${NEW_VERSION}/g" -i.bak "$f" || true
      rm -f "$f".bak
      git add "$f"
      modified+=("$f")
    fi
  done

  if [ ${#modified[@]} -eq 0 ]; then
    echo "⚠️ No pom.xml files contained $CURRENT_VERSION"
  else
    git commit -m "chore(release): bump version ${CURRENT_VERSION} -> ${NEW_VERSION}"
    git push origin $NEW_BRANCH
    echo "✅ Updated ${#modified[@]} pom.xml file(s) and pushed: $NEW_VERSION"
  fi
else
  echo "✅ Branch $NEW_BRANCH created from $BASE_BRANCH"
fi
