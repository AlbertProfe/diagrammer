---
number: 15
title: "Light and dark themes"
date: "2026-10-18"
domain: front
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: []
tags: [product]
---

## Decision

Diagrams support a light theme and a dark theme.

## Rationale

Both themes are in scope. The PersonCarDog reference is mostly light, with one dark page.

## Notes

2026-10-20: v0.0.3 adds the Theme chip (`light` default, `dark`) on the Pre-Profile, after Depth and outside Advanced. The app shell stays light. Both palettes are CSS custom properties in `diagrammer-code-project/src/main/resources/META-INF/resources/diagram-theme.css`, which the preview uses now and the renderer (S8) will inline into generated pages. `meta.json`, the version bar, and regenerating a new version for a theme change wait on S9, S17, and S12.
