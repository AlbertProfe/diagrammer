---
number: 12
title: "One S3 bucket, folders per user and diagram"
date: "2026-10-18"
domain: aws
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [5, 24]
tags: []
---

## Decision

Storage stays on AWS S3. One bucket. A folder per user, and inside it a subfolder per diagram holding all of that diagram's versions. The URL shape is `/diagrams/{id}` plus a per-version URL. The exact URL form is still TBD.

## Rationale

#5 chose S3 and left the layout open. This sets one bucket and the user/diagram folders. It does not settle the exact spelling of the version URL.

## Notes

- **Relation:** Confirms #5. Supersedes TBD: S3 bucket layout

Confirms [0005](0005-s3-for-user-generated-documents.md).
