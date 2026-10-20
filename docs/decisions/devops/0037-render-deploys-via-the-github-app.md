---
number: 37
title: "Render deploys via the GitHub app; push auto-deploy not yet proven"
date: "2026-10-20"
domain: devops
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [30, 31, 32]
tags: []
---

## Decision

Render deploys via the GitHub app. Push auto-deploy is not yet proven.

Before 2026-10-19 20:00 Europe/Madrid, Render was linked by public repo URL, not through the Render GitHub app, so it never received push events. Every deploy before then was manual or triggered by a settings change.

PRs #30 and #31 went live through a manual deploy of main (commit `b7500e8`), started at 19:51 and live at 19:56 Madrid time on 2026-10-19.

Around 20:00 Albert reconnected the Source through the Render GitHub app and re-added the build filter, because reconnecting had wiped it: paths `diagrammer-code-project/**`, ignoredPaths `diagrammer-code-project/**/*.md`.

Push-triggered auto-deploy isn't proven yet. The next code merge (v0.0.3) is the first test.

A deploy hook for manual deploys exists and is kept privately by diag-devops. It is never put in the repo or docs.

## Rationale

The GitHub app is what delivers push events to Render. That connection was made on 2026-10-19, and a push has not yet proven auto-deploy.

## Notes

Relates to [0030](0030-deploy-on-render.md), [0031](0031-render-service-shape.md), and [0032](0032-render-deploys-only-for-app-changes.md).
