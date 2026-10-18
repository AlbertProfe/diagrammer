---
number: 10
title: "Grok models on Bedrock"
date: "2026-10-18"
domain: back
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [6, 11, 25]
tags: [aws]
---

## Decision

Model calls use Grok models through AWS Bedrock. Three roles sit behind an `AiClient` interface: chat (the profile assistant), JSON planner (produces the DiagramSpec), and diagrammer/renderer (the model used when the user opts in to LLM rendering, see #11). #6 still holds for access: a scoped IAM user, with keys stored as secrets, never pasted in chat and never committed. There is no separate Grok API key.

## Rationale

#6 chose Bedrock and left the model open. The model is Grok, in three roles, still called through Bedrock.

## Notes

- **Relation:** Extends #6. Supersedes TBD: Bedrock model

Extends [0006](../aws/0006-bedrock-for-model-calls.md).
