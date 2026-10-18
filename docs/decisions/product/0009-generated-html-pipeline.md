---
number: 9
title: "Generated HTML pipeline"
date: "2026-10-18"
domain: product
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [22]
tags: [back, front]
---

## Decision

Diagrammer generates self-contained interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) teaching pages. The pipeline is guided intake (Vaadin chips plus a narrow profile-assistant chat) → Pre-Profile JSON → Planner → DiagramSpec JSON → Renderer → HTML. One Diagram Orchestrator screen has Create and Tinker modes. Every generate creates a new immutable version (`v1` through `vN`). Mermaid and a diagram editor library are closed.

## Rationale

The product is a generated teaching page in the PersonCarDog style. That closes the open question of a diagram format and editor library.

## Notes

- **Relation:** Supersedes TBD: diagram format/editor library

Closes the editor-library TBD.
