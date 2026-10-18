---
number: 13
title: "JSON credentials file"
date: "2026-10-18"
domain: security
status: active
supersedes: []
superseded_by: []
refines: [4]
relates_to: [5, 24]
tags: [aws]
---

## Decision

Users are stored as a JSON credentials file in a bucket folder. The file is not encrypted. Albert has confirmed this is intentional for v1: early human testing, keep it simple. Password hashing (for example BCrypt) is a later backlog item, not part of v1.

## Rationale

#4 left the simple store unspecified, and #5 left a users file optional. For v1 the store is this plaintext JSON file. Password hashing waits on the backlog.

## Notes

- **Relation:** Refines #4. Supersedes TBD: auth user store. Settles the optional users file in #5.
