---
number: 33
title: "Pre-Profile chip values"
date: "2026-10-19"
domain: product
status: active
supersedes: []
superseded_by: []
refines: []
relates_to: [9, 17]
tags: [front]
---

## Decision

Pre-Profile chips use these values:

- **domain:** `java`, `spring-boot`, `python`, `ml`, `react`
- **goal:** `explain-flow`, `use-case`, `compare`, `layers`, `other`
- **interactivity:** `static`, `step-by-step`, `multi-scenario`
- **type:** `auto`, `horizontal-steps`, `u-shape`, `pyramid`
- **depth:** `overview`, `with-code`, `with-code-parallels`
- **audience:** `beginner`, `intermediate`

`multi-scenario` is allowed only with `u-shape` or `auto`.

Goal `compare` forces type `auto` and disables `horizontal-steps`, `u-shape`, and `pyramid`.

`static` and `step-by-step` are allowed with every type.

Comparison is a v2 / future layout. It is not a type value in v0.0.2. The issue comment's "no Comparison in v1" means the same thing: Comparison is deferred past current releases, not renamed.

The longer goal values `walk-through-use-case`, `compare-concepts`, and `show-layers` are superseded by `use-case`, `compare`, and `layers`.

## Rationale

Shorter chip values match the code in [PR #30](https://github.com/AlbertProfe/diagrammer/pull/30) and the canonical set in [issue #4](https://github.com/AlbertProfe/diagrammer/issues/4#issuecomment-6270809445). The compare-forces-auto rule and the static / step-by-step rule are the combination rules in that comment and in that pull request.

## Notes

No earlier decision file named these chip values. The longer goal slugs lived only in [ARCHITECTURE.md](../../ARCHITECTURE.md). This relates to [0009](0009-generated-html-pipeline.md) (guided intake and the Pre-Profile) and [0017](0017-layouts-in-v1.md) (v1 layouts; Comparison is v2). It does not supersede either one.
