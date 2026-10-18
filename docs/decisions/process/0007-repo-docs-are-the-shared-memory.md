---
number: 7
title: "Repo docs are the shared memory"
date: "2026-10-18"
domain: process
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: []
tags: []
---

## Decision

Do not use a knowledge-graph tool (such as Graphify) for now. The shared source of truth is [ARCHITECTURE.md](../../ARCHITECTURE.md) and this decision log. Every bot reads both before working.

## Rationale

Keep the shared picture in the repository. Bots read these two files before working, instead of a separate knowledge-graph tool.
