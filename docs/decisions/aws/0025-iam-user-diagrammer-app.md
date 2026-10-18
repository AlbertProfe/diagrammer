---
number: 25
title: "IAM user diagrammer-app"
date: "2026-10-18"
domain: aws
status: active
supersedes: []
superseded_by: []
refines: [6]
relates_to: [10]
tags: [security]
---

## Decision

AWS access uses the scoped IAM user `diagrammer-app`. That user can read, write, list, and delete objects in `diagrammer-albertprofe-docs` only. Bedrock permissions are added once a model is chosen. Keys stay secrets (decisions 6 and 10): never pasted in chat, never committed.

## Rationale

Decision 6 required a scoped IAM user and left the scope unnamed. The scope is this bucket. Bedrock access waits until a model is chosen.

## Notes

- **Relation:** Refines decision 6
