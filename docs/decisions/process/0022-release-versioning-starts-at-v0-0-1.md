---
number: 22
title: "Release versioning starts at v0.0.1"
date: "2026-10-18"
domain: process
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [9]
tags: [product]
---

## Decision

App release versioning starts at `v0.0.1`. Releases are tracked with `release/vX.Y.Z` labels on issues. `v0.0.1` is story S1 ([issue #3](https://github.com/AlbertProfe/diagrammer/issues/3), "Log in and see only my diagrams") plus a minimal Hilla skeleton. A release version is separate from a diagram's content versions (`v1` through `vN`) in decision 9.

## Rationale

The first release is the Hilla skeleton and S1. Issue labels are how a release is tracked, and they are not the diagram version numbers.
