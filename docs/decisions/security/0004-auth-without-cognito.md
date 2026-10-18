---
number: 4
title: "Auth without Cognito"
date: "2026-10-18"
domain: security
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [13]
tags: [back]
---

## Decision

Do not use Amazon Cognito for now. Use Spring-style auth with a simple store. Cognito may be considered later.

## Rationale

Start with Spring-style auth and a simple store. Cognito stays a possible later option. The concrete user store is not chosen yet (see architecture TBD).
