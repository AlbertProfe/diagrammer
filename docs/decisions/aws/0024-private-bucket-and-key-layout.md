---
number: 24
title: "Private bucket and key layout"
date: "2026-10-18"
domain: aws
status: active
supersedes: []
superseded_by: []
refines: [5, 12]
relates_to: [13, 14]
tags: [product, security]
---

## Decision

One private bucket, `diagrammer-albertprofe-docs`. Block Public Access is on. SSE-S3 is on. S3 object versioning is on. Key layout: `auth/users.json`, `users/{userId}/diagrams/{diagramId}/v{n}/...`, and `index/diagrams/{diagramId}.json`. Because the bucket is private, the public diagram URLs from decision 14 are served through the app, not directly from S3.

## Rationale

Decisions 5 and 12 chose S3 and a per-user, per-diagram layout. This names the bucket, locks it down, and sets the key prefixes. Decision 13's credentials file is `auth/users.json`. Decision 14 still makes the diagram URL public. The app reads the private object and serves that URL.

## Notes

- **Relation:** Refines decisions 5 and 12. Names the credentials file from decision 13. Clarifies decision 14.
