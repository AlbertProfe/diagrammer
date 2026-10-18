---
number: 1
title: "Hilla full-stack"
date: "2026-10-18"
domain: architecture
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: []
tags: [back, front]
---

## Decision

Diagrammer is one Vaadin Hilla project. The backend is Spring Boot. The frontend is React. The UI talks to the backend through Hilla endpoints. A separate pure-React app is out of scope.

## Rationale

Keep the backend and frontend in one project, with Hilla endpoints as the way they communicate.
