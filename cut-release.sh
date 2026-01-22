#!/usr/bin/env bash
set -e

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: ./cut-release.sh <base-branch> <new-branch>"
  exit 1
fi

BASE_BRANCH="$1"
NEW_BRANCH="$2"

echo "🔄 Fetching latest from origin..."
git fetch origin

# Validate base branch exists
if ! git show-ref --verify --quiet refs/remotes/origin/$BASE_BRANCH; then
  echo "❌ Base branch does not exist: $BASE_BRANCH"
  exit 1
fi

echo "📌 Checking out base branch: $BASE_BRANCH"
git checkout $BASE_BRANCH
git pull origin $BASE_BRANCH

# Check if remote branch already exists
if git show-ref --verify --quiet refs/remotes/origin/$NEW_BRANCH; then
  echo "❌ Branch already exists on origin: $NEW_BRANCH"
  exit 1
fi

# Delete local leftover branch
if git show-ref --verify --quiet refs/heads/$NEW_BRANCH; then
  echo "⚠️ Local branch exists, deleting: $NEW_BRANCH"
  git branch -D $NEW_BRANCH
fi

echo "🌱 Creating new branch: $NEW_BRANCH"
git checkout -b $NEW_BRANCH

echo "🚀 Pushing branch to origin"
git push origin $NEW_BRANCH

echo "✅ Branch $NEW_BRANCH created from $BASE_BRANCH"
