---
number: 32
title: "Render deploys only for app changes"
date: "2026-10-18"
domain: devops
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [30, 31]
tags: []
---

## Decision

Render deploys only when files under `diagrammer-code-project/` change, ignoring `diagrammer-code-project/**/*.md` and everything outside that folder. This is done with a build filter in [`render.yaml`](../../../render.yaml) ([PR #28](https://github.com/AlbertProfe/diagrammer/pull/28)) and also set on the live service in the Render dashboard. Approved by Albert.

## Rationale

Avoid redeploys for docs-only and out-of-app changes.

## Notes

Relates to [0030](0030-deploy-on-render.md) and [0031](0031-render-service-shape.md).

Note (2026-10-20): the build filter did not mean pushes to `main` auto-deployed before 2026-10-19 20:00 Europe/Madrid. See [0037](0037-render-deploys-via-the-github-app.md).
