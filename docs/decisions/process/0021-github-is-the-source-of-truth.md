---
number: 21
title: "GitHub is the source of truth"
date: "2026-10-18"
domain: process
status: active
supersedes: [3]
superseded_by: []
refines: []
relates_to: [2, 8, 30]
tags: [devops]
---

## Decision

The repo is hosted on GitHub. It is not on Cursor Origin. Cloud agents clone from GitHub, work on a branch, and open pull requests on GitHub. diag-reviewer comments on those pull requests. Albert reviews and merges on GitHub. Nothing goes straight to `main`. GitHub is the single source of truth. Railway also builds from it (decisions 2 and 8).

## Rationale

Decision 3 named Origin as the pull-request layer. The working flow is GitHub for the clone, the branch, the pull request, the review, and the merge.

## Notes

- **Relation:** Clarifies decision 3

Note (2026-10-18): This decision supersedes [0003](0003-cursor-origin-for-pull-requests.md) for the pull-request flow; that settles the earlier "Clarifies decision 3" wording.
