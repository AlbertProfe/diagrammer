---
number: 28
title: "Application code lives in diagrammer-code-project"
date: "2026-10-18"
domain: process
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: []
tags: [architecture]
---

## Decision

All application code lives in the top-level folder `diagrammer-code-project/`. That folder is the Maven/Hilla project root (`pom.xml` and `src/`). The frontend lives in `diagrammer-code-project/src/main/frontend/`, not at the project root. The folder sits next to `docs/`. Docs stay in `docs/`. (corrected 2026-10-18)

## Rationale

The app and the source-of-truth docs share one repository, in separate top-level folders.
