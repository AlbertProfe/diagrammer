---
number: 34
title: "Code memory for agents is AGENTS.md and CODEMAP.md"
date: "2026-10-20"
domain: process
status: active
supersedes: []
superseded_by: []
refines: [7]
relates_to: []
tags: []
---

## Decision

Code memory for agents is [AGENTS.md](../../../AGENTS.md) plus [CODEMAP.md](../../CODEMAP.md). diag-documenter keeps both. When a pull request changes code structure, update CODEMAP.md in the same PR, or in a follow-up docs PR.

There is no Graphify or other knowledge-graph tool.

## Rationale

Agents need a short entry point and a skimmable map of the code, kept in the repository with the architecture and the decision log.

## Notes

- **Relation:** Refines [0007](0007-repo-docs-are-the-shared-memory.md).
