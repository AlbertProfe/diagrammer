---
number: 11
title: "Templates by default, LLM render on opt-in"
date: "2026-10-18"
domain: product
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [10, 16]
tags: [back]
---

## Decision

The renderer fills deterministic templates from the DiagramSpec by default. The user can opt to have an LLM render instead. That opt-in uses the diagrammer/renderer role from #10.

## Rationale

Templates keep the house style on the common path. An LLM render is available only when the user asks for it.
