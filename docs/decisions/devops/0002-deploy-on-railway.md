---
number: 2
title: "Deploy on Railway"
date: "2026-10-18"
domain: devops
status: superseded
supersedes: []
superseded_by: [30]
refines: []
relates_to: [21]
tags: []
---

## Decision

Deploy on Railway. Render is not the host. GitHub is connected to Railway. A push to `main` builds a Docker image and publishes it on Railway. Railway is secondary for now. The v1 UI can be previewed from cloud agent screenshots or videos, or from a local run.

## Rationale

Railway is the chosen host, wired to GitHub so a push to `main` publishes a Docker image. Railway is secondary for now, so the v1 UI is previewed locally or from cloud agent screenshots and videos.
