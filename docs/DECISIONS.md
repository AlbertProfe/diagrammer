# Decisions

The decision files under `docs/decisions/` and [ARCHITECTURE.md](ARCHITECTURE.md) are the shared source of truth.

## Status by domain

### architecture

Diagrammer is one Vaadin Hilla project: a Spring Boot backend, a React frontend, and Hilla endpoints between them ([0001](decisions/architecture/0001-hilla-full-stack.md)).

No open questions are recorded for this domain.

Last updated: 2026-10-18

### front

Generated diagrams support a light theme and a dark theme ([0015](decisions/front/0015-light-and-dark-themes.md)).

Open questions:

- Arrow and SVG geometry. Computed from the layout, or hand-placed like the reference. Not chosen ([ARCHITECTURE.md](ARCHITECTURE.md#open-questions--tbd)).

Last updated: 2026-10-18

### back

Model calls use Grok models through AWS Bedrock, in three roles behind an `AiClient` interface: chat, JSON planner, and diagrammer/renderer ([0010](decisions/back/0010-grok-models-on-bedrock.md)).

No open questions are recorded for this domain.

Last updated: 2026-10-18

### aws

User-generated documents (HTML, JSON, and similar) are stored in AWS S3 ([0005](decisions/aws/0005-s3-for-user-generated-documents.md)).

Model calls go to AWS Bedrock through a scoped IAM user, that user's keys stay in secrets, and the model family is Grok via Bedrock ([0006](decisions/aws/0006-bedrock-for-model-calls.md), [0010](decisions/back/0010-grok-models-on-bedrock.md)).

One S3 bucket holds a folder per user and, inside it, a subfolder per diagram for every version ([0012](decisions/aws/0012-one-s3-bucket-folders-per-user-and-diagram.md)).

AWS resources use the region `eu-central-1` (Frankfurt) ([0023](decisions/aws/0023-aws-region-eu-central-1.md)).

The private bucket `diagrammer-albertprofe-docs` has Block Public Access, SSE-S3, and object versioning, and the app serves the public diagram URL from those private keys ([0024](decisions/aws/0024-private-bucket-and-key-layout.md)).

The scoped IAM user is `diagrammer-app`, and it can read, write, list, and delete objects in that bucket only ([0025](decisions/aws/0025-iam-user-diagrammer-app.md)).

The app reads `DIAGRAMMER_S3_BUCKET`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, and `AWS_SECRET_ACCESS_KEY` from the environment ([0026](decisions/aws/0026-configuration-by-environment-variables.md)).

Open questions:

- Exact diagram URL form. The shape is `/diagrams/{id}` plus a per-version URL. The exact path is not chosen ([0012](decisions/aws/0012-one-s3-bucket-folders-per-user-and-diagram.md), [ARCHITECTURE.md](ARCHITECTURE.md#open-questions--tbd)).
- Limits and cost. Generation caps, diagram size, timeout, and cost ceiling are not chosen ([ARCHITECTURE.md](ARCHITECTURE.md#open-questions--tbd)).
- The exact Bedrock model id is still open ([0006](decisions/aws/0006-bedrock-for-model-calls.md), [0010](decisions/back/0010-grok-models-on-bedrock.md)).
- Bedrock permissions are added once a model is chosen ([0025](decisions/aws/0025-iam-user-diagrammer-app.md)).

Last updated: 2026-10-18

### devops

The dev toolchain is Java 21 (Temurin), Maven 3.9.x, and Node 24 LTS ([0027](decisions/devops/0027-java-21-maven-3-9-node-24.md)).

Pinned versions are Maven 3.9.11 via `./mvnw`, Spring Boot 4.1.1, Vaadin Hilla 25.3.1, and AWS SDK for Java v2 2.56.0, and local dev is `./mvnw spring-boot:run -Pdev` from `diagrammer-code-project/` ([0029](decisions/devops/0029-framework-and-library-versions.md)).

The host is Render, account albertprofe, building a Docker image from GitHub `main` ([0030](decisions/devops/0030-deploy-on-render.md)).

The `diagrammer` service runs on the free plan in Frankfurt at https://diagrammer-wt9s.onrender.com, and `healthCheckPath=/actuator/health` is set in the Render dashboard (the redeploy was verified 200 UP at 15:42 Europe/Madrid on 2026-10-18) ([0031](decisions/devops/0031-render-service-shape.md)).

Render deploys only when files under `diagrammer-code-project/` change, ignoring `diagrammer-code-project/**/*.md` and everything outside that folder ([0032](decisions/devops/0032-render-deploys-only-for-app-changes.md)).

No open questions are recorded for this domain.

Last updated: 2026-10-18

### security

Auth is Spring-style, Amazon Cognito is deferred, and the user store was chosen in [0013](decisions/security/0013-json-credentials-file.md) ([0004](decisions/security/0004-auth-without-cognito.md)).

Users are stored as a plaintext JSON credentials file in a bucket folder for v1 ([0013](decisions/security/0013-json-credentials-file.md)).

The DiagramSpec carries text only, the renderer escapes it, responses send a Content-Security-Policy, and opt-in LLM HTML is sanitized in a sandboxed iframe ([0016](decisions/security/0016-safe-diagram-output.md)).

Open questions:

- Amazon Cognito may be considered later ([0004](decisions/security/0004-auth-without-cognito.md)).
- Password hashing (for example BCrypt) is a later backlog item, not part of v1 ([0013](decisions/security/0013-json-credentials-file.md)).

Last updated: 2026-10-18

### process

The shared source of truth is ARCHITECTURE.md and this decision log, and there is no knowledge-graph tool for now ([0007](decisions/process/0007-repo-docs-are-the-shared-memory.md)).

The repository is the public GitHub repo [AlbertProfe/diagrammer](https://github.com/AlbertProfe/diagrammer), the default branch is `main`, and the deploy builder is Render ([0008](decisions/process/0008-public-github-repo-on-main.md), [0030](decisions/devops/0030-deploy-on-render.md)).

GitHub is the source of truth for the clone, the branch, the pull request, the review, and the merge, and this supersedes the Origin pull-request flow ([0021](decisions/process/0021-github-is-the-source-of-truth.md), [0003](decisions/process/0003-cursor-origin-for-pull-requests.md)).

App release versioning starts at `v0.0.1`, tracked with `release/vX.Y.Z` labels on issues ([0022](decisions/process/0022-release-versioning-starts-at-v0-0-1.md)).

Application code lives in `diagrammer-code-project/`, next to `docs/` ([0028](decisions/process/0028-application-code-in-diagrammer-code-project.md)).

No open questions are recorded for this domain.

Last updated: 2026-10-18

### product

Diagrammer generates PersonCarDog-style HTML through guided intake, a Pre-Profile, a planner, a DiagramSpec, and a renderer, and every generate stores a new immutable version ([0009](decisions/product/0009-generated-html-pipeline.md)).

The renderer fills deterministic templates by default, and an LLM render is available when the user opts in ([0011](decisions/product/0011-templates-by-default-llm-render-on-opt-in.md)).

Anyone with the diagram link can view the diagram ([0014](decisions/product/0014-public-diagram-urls.md)).

v1 layouts are horizontal steps, U-shape request/response, and pyramid, and comparison is v2 ([0017](decisions/product/0017-layouts-in-v1.md)).

Templates enable Next from the start, escape text, and include a back link ([0018](decisions/product/0018-templates-fix-reference-quirks.md)).

Diagram output is English only, and the app is i18n-ready ([0019](decisions/product/0019-english-output-i18n-ready.md)).

The user types the diagram title ([0020](decisions/product/0020-user-typed-diagram-title.md)).

Open questions:

- Spring Boot and Python topic placeholders. Java, ML, and React placeholders are set. These two are not ([ARCHITECTURE.md](ARCHITECTURE.md#open-questions--tbd)).
- Diagram deletion. Versions are immutable. Whether a whole diagram can be deleted is not chosen ([0009](decisions/product/0009-generated-html-pipeline.md), [ARCHITECTURE.md](ARCHITECTURE.md#open-questions--tbd)).

Last updated: 2026-10-18

## How to add a decision

diag-lead assigns the next number, so two bots never take the same one. Create one file in the right domain folder, `docs/decisions/<domain>/NNNN-short-kebab-title.md`. Numbers are global. The domains are architecture, front, back, aws, devops, security, process, and product.

The file has the full front matter: `number`, `title`, `date`, `domain`, `status` (`active` or `superseded`), `supersedes`, `superseded_by`, `refines`, `relates_to`, and `tags` when the decision touches other domains. The body has Decision, Rationale, and any notes.

Never edit or delete an old decision's text. To change a decision, add a new one with `supersedes`, and on the old file change only `status` and `superseded_by`. Dated notes are allowed.

Whoever adds or supersedes a decision also updates that domain's Status by domain summary and its Last updated date in the same PR. If the old decision sits in another domain, update that domain's summary and date too.

Afterwards, update [ARCHITECTURE.md](ARCHITECTURE.md) and this index (Current decisions and History).

## Current decisions

Active decisions only.

### architecture

- [0001 Hilla full-stack](decisions/architecture/0001-hilla-full-stack.md)

### front

- [0015 Light and dark themes](decisions/front/0015-light-and-dark-themes.md)

### back

- [0010 Grok models on Bedrock](decisions/back/0010-grok-models-on-bedrock.md)

### aws

- [0005 S3 for user-generated documents](decisions/aws/0005-s3-for-user-generated-documents.md)
- [0006 Bedrock for model calls](decisions/aws/0006-bedrock-for-model-calls.md)
- [0012 One S3 bucket, folders per user and diagram](decisions/aws/0012-one-s3-bucket-folders-per-user-and-diagram.md)
- [0023 AWS region eu-central-1](decisions/aws/0023-aws-region-eu-central-1.md)
- [0024 Private bucket and key layout](decisions/aws/0024-private-bucket-and-key-layout.md)
- [0025 IAM user diagrammer-app](decisions/aws/0025-iam-user-diagrammer-app.md)
- [0026 Configuration by environment variables](decisions/aws/0026-configuration-by-environment-variables.md)

### devops

- [0027 Java 21, Maven 3.9, Node 24](decisions/devops/0027-java-21-maven-3-9-node-24.md)
- [0029 Framework and library versions](decisions/devops/0029-framework-and-library-versions.md)
- [0030 Deploy on Render](decisions/devops/0030-deploy-on-render.md)
- [0031 Render service shape](decisions/devops/0031-render-service-shape.md)
- [0032 Render deploys only for app changes](decisions/devops/0032-render-deploys-only-for-app-changes.md)

### security

- [0004 Auth without Cognito](decisions/security/0004-auth-without-cognito.md)
- [0013 JSON credentials file](decisions/security/0013-json-credentials-file.md)
- [0016 Safe diagram output](decisions/security/0016-safe-diagram-output.md)

### process

- [0007 Repo docs are the shared memory](decisions/process/0007-repo-docs-are-the-shared-memory.md)
- [0008 Public GitHub repo on main](decisions/process/0008-public-github-repo-on-main.md)
- [0021 GitHub is the source of truth](decisions/process/0021-github-is-the-source-of-truth.md)
- [0022 Release versioning starts at v0.0.1](decisions/process/0022-release-versioning-starts-at-v0-0-1.md)
- [0028 Application code lives in diagrammer-code-project](decisions/process/0028-application-code-in-diagrammer-code-project.md)

### product

- [0009 Generated HTML pipeline](decisions/product/0009-generated-html-pipeline.md)
- [0011 Templates by default, LLM render on opt-in](decisions/product/0011-templates-by-default-llm-render-on-opt-in.md)
- [0014 Public diagram URLs](decisions/product/0014-public-diagram-urls.md)
- [0017 Layouts in v1](decisions/product/0017-layouts-in-v1.md)
- [0018 Templates fix reference quirks](decisions/product/0018-templates-fix-reference-quirks.md)
- [0019 English output, i18n-ready](decisions/product/0019-english-output-i18n-ready.md)
- [0020 User-typed diagram title](decisions/product/0020-user-typed-diagram-title.md)

## History

- [0001 Hilla full-stack](decisions/architecture/0001-hilla-full-stack.md) — active
- [0002 Deploy on Railway](decisions/devops/0002-deploy-on-railway.md) — superseded by [0030](decisions/devops/0030-deploy-on-render.md)
- [0003 Cursor Origin for pull requests](decisions/process/0003-cursor-origin-for-pull-requests.md) — superseded by [0021](decisions/process/0021-github-is-the-source-of-truth.md)
- [0004 Auth without Cognito](decisions/security/0004-auth-without-cognito.md) — active
- [0005 S3 for user-generated documents](decisions/aws/0005-s3-for-user-generated-documents.md) — active
- [0006 Bedrock for model calls](decisions/aws/0006-bedrock-for-model-calls.md) — active
- [0007 Repo docs are the shared memory](decisions/process/0007-repo-docs-are-the-shared-memory.md) — active
- [0008 Public GitHub repo on main](decisions/process/0008-public-github-repo-on-main.md) — active
- [0009 Generated HTML pipeline](decisions/product/0009-generated-html-pipeline.md) — active
- [0010 Grok models on Bedrock](decisions/back/0010-grok-models-on-bedrock.md) — active
- [0011 Templates by default, LLM render on opt-in](decisions/product/0011-templates-by-default-llm-render-on-opt-in.md) — active
- [0012 One S3 bucket, folders per user and diagram](decisions/aws/0012-one-s3-bucket-folders-per-user-and-diagram.md) — active
- [0013 JSON credentials file](decisions/security/0013-json-credentials-file.md) — active
- [0014 Public diagram URLs](decisions/product/0014-public-diagram-urls.md) — active
- [0015 Light and dark themes](decisions/front/0015-light-and-dark-themes.md) — active
- [0016 Safe diagram output](decisions/security/0016-safe-diagram-output.md) — active
- [0017 Layouts in v1](decisions/product/0017-layouts-in-v1.md) — active
- [0018 Templates fix reference quirks](decisions/product/0018-templates-fix-reference-quirks.md) — active
- [0019 English output, i18n-ready](decisions/product/0019-english-output-i18n-ready.md) — active
- [0020 User-typed diagram title](decisions/product/0020-user-typed-diagram-title.md) — active
- [0021 GitHub is the source of truth](decisions/process/0021-github-is-the-source-of-truth.md) — active
- [0022 Release versioning starts at v0.0.1](decisions/process/0022-release-versioning-starts-at-v0-0-1.md) — active
- [0023 AWS region eu-central-1](decisions/aws/0023-aws-region-eu-central-1.md) — active
- [0024 Private bucket and key layout](decisions/aws/0024-private-bucket-and-key-layout.md) — active
- [0025 IAM user diagrammer-app](decisions/aws/0025-iam-user-diagrammer-app.md) — active
- [0026 Configuration by environment variables](decisions/aws/0026-configuration-by-environment-variables.md) — active
- [0027 Java 21, Maven 3.9, Node 24](decisions/devops/0027-java-21-maven-3-9-node-24.md) — active
- [0028 Application code lives in diagrammer-code-project](decisions/process/0028-application-code-in-diagrammer-code-project.md) — active
- [0029 Framework and library versions](decisions/devops/0029-framework-and-library-versions.md) — active
- [0030 Deploy on Render](decisions/devops/0030-deploy-on-render.md) — active
- [0031 Render service shape](decisions/devops/0031-render-service-shape.md) — active
- [0032 Render deploys only for app changes](decisions/devops/0032-render-deploys-only-for-app-changes.md) — active
