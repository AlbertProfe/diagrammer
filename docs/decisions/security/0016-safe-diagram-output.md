---
number: 16
title: "Safe diagram output"
date: "2026-10-18"
domain: security
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [11]
tags: [front, product]
---

## Decision

The DiagramSpec carries text only. The renderer escapes that text. Diagram responses are served with a Content-Security-Policy. When the user opts in to LLM rendering (#11), that HTML is sanitized and the preview is a sandboxed iframe.

## Rationale

Generated pages are HTML served to whoever has the link. Escaping, a CSP, and a sandbox on LLM HTML are the safety bar.
