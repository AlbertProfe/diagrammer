---
number: 30
title: "Deploy on Render"
date: "2026-10-18"
domain: devops
status: active
supersedes: [2]
superseded_by: []
refines: []
relates_to: [8, 21, 26, 31, 32]
tags: [aws, process]
---

## Decision

Deploy on Render, not Railway. Approved by Albert. The Render account is albertprofe. Render deploys from GitHub ([AlbertProfe/diagrammer](https://github.com/AlbertProfe/diagrammer), branch `main`) and builds the Docker image itself, from a Dockerfile in the repo, using Render's Docker runtime. The decision 26 variables are set as Render environment variables and are never committed. diag-devops owns the setup. Render service shape (plan, region, health check) is still open.

## Rationale

Decision 2 chose Railway. The host is now Render. GitHub remains the source Render builds from.

## Notes

- **Relation:** Supersedes decision 2. Decisions 8 and 21 still stand for GitHub as the source of truth. This entry replaces Railway with Render as the builder those entries named. Clarifies decision 26: those variables are Render environment variables.

Note (2026-10-20): "Render deploys from GitHub, branch `main`" does not mean pushes to `main` auto-deployed before 2026-10-19 20:00 Europe/Madrid. See [0037](0037-render-deploys-via-the-github-app.md).
