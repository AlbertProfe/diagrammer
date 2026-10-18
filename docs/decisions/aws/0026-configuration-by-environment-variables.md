---
number: 26
title: "Configuration by environment variables"
date: "2026-10-18"
domain: aws
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [6, 29, 30, 31]
tags: [devops, security]
---

## Decision

The app reads `DIAGRAMMER_S3_BUCKET`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, and `AWS_SECRET_ACCESS_KEY` from the environment. Values are never committed.

## Rationale

Decision 6 keeps keys out of the repo. These names are how the app receives the bucket, the region, and the IAM keys. Where the values are stored outside the process is still open.

## Notes

- **Relation:** Clarifies decision 6
