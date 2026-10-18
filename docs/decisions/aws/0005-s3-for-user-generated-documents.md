---
number: 5
title: "S3 for user-generated documents"
date: "2026-10-18"
domain: aws
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [12, 13, 24]
tags: []
---

## Decision

Store user-generated documents (HTML, JSON, and similar) in AWS S3. A users file may be added later. That file is optional.

## Rationale

S3 is the store for documents the app creates. A users file is a later, optional addition, not part of the current design.
