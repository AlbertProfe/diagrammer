---
number: 31
title: "Render service shape"
date: "2026-10-18"
domain: devops
status: active
supersedes: []
superseded_by: []
refines: [30]
relates_to: [26, 32]
tags: [aws]
---

## Decision

The Render service `diagrammer` runs on the free plan in Frankfurt, with the Docker runtime and build context `diagrammer-code-project`. It auto-deploys from `main`. The public URL is https://diagrammer-wt9s.onrender.com. It runs with `SPRING_PROFILES_ACTIVE=s3`. The decision 26 environment variables are set in Render and are never committed. The health endpoint is `/actuator/health`. Repo [`render.yaml`](../../../render.yaml) sets `healthCheckPath` to that path. Setting `healthCheckPath` in the Render dashboard is still pending. The dev fixture login is rejected in production.

## Rationale

Decision 30 left the service shape open. The merged [`render.yaml`](../../../render.yaml) and [`diagrammer-code-project/Dockerfile`](../../../diagrammer-code-project/Dockerfile) set the plan, region, runtime, and build context. The image entrypoint refuses the `local` and `test` profiles, so the fixture user cannot sign in on this deploy. The dashboard health-check setting is not done yet.

## Notes

- **Relation:** Refines decision 30. Supersedes TBD: Render service shape

Note (2026-10-18): healthCheckPath=/actuator/health is now set in the Render dashboard; the redeploy was verified 200 UP at 15:42 Europe/Madrid on 2026-10-18.
