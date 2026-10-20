---
number: 29
title: "Framework and library versions"
date: "2026-10-18"
domain: devops
status: active
supersedes: []
superseded_by: []
refines: [27]
relates_to: [26]
tags: [aws, back]
---

## Decision

Java 21, Maven 3.9.11 via the `./mvnw` wrapper, Node 24 LTS, Spring Boot 4.1.1, Vaadin Hilla 25.3.1, and AWS SDK for Java v2 2.56.0. Java 21 stays on Temurin (decision 27). These versions were adopted in the v0.0.1 pull request [PR #22](https://github.com/AlbertProfe/diagrammer/pull/22). There is no default profile. With no profile, the app fails fast unless `DIAGRAMMER_S3_BUCKET` and `AWS_REGION` are set, in which case it uses `s3` (credentials still come from decision 26). Local dev is `./mvnw spring-boot:run -Pdev` from `diagrammer-code-project/`. That Maven profile turns on the Spring `local` profile, uses the fixture `diagrammer-code-project/src/main/resources/fixture/users.json`, and needs no AWS. (corrected 2026-10-18)

## Rationale

Decision 27 left Maven on the 3.9 line and left Hilla and Spring Boot open. This pins the versions used for v0.0.1. The run profiles match the merged app: no default, fail fast without the S3 environment variables, and `-Pdev` for the fixture users.

## Notes

- **Relation:** Refines decision 27. Supersedes TBD: Hilla and Spring Boot versions

Note (2026-10-20): the fixture password was removed from the decision text above. Sign in from `diagrammer-code-project/src/main/resources/fixture/users.json`.
