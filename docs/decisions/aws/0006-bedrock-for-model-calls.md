---
number: 6
title: "Bedrock for model calls"
date: "2026-10-18"
domain: aws
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [10, 25, 26]
tags: [back, security]
---

## Decision

Model calls go to AWS Bedrock. AWS access uses a scoped IAM user. That user's keys are stored as secrets. Keys are never pasted in chat and never committed.

## Rationale

Bedrock is the model API. A scoped IAM user is the AWS access path, and its keys stay in secrets so they never appear in chat or in the repository. The Bedrock model is not chosen yet (see architecture TBD).
