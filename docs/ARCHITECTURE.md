# Architecture

Intended architecture for Diagrammer. It records only decisions already made in the [decision log](DECISIONS.md). Each decision is a file under [docs/decisions/](decisions/). Anything not settled there is listed under [Open questions / TBD](#open-questions--tbd) and is not a decision.

This file is the current picture. When a later decision changes the design, update this file to match. The decision log stays append-only.

There is no knowledge-graph tool. This file and the [decision log](DECISIONS.md) are the shared source of truth. Code memory for agents is [AGENTS.md](../AGENTS.md) and [CODEMAP.md](CODEMAP.md), kept by diag-documenter ([0034](decisions/process/0034-code-memory-for-agents.md)). Every bot (product owner, tech lead, coder, devops, reviewer, documenter, QA) reads them before working. A pull request that changes code structure (packages, endpoints, views, config, tests) updates CODEMAP.md in the same PR, or in a follow-up docs PR.

## Product

Diagrammer generates self-contained interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) teaching pages. Each page stands alone: inline CSS and JavaScript, no external dependencies.

One screen, the Diagram Orchestrator, has two modes:

- **Create.** No diagram yet. Guided intake, then generate version `v1`.
- **Tinker.** One or more versions exist. Adjust the intake and generate again. The latest version is active by default.

Every generate creates a new immutable version (`v1` through `vN`). A regenerate does not overwrite an earlier version. Whether a whole diagram can be deleted is TBD.

The user types the diagram title. Diagram output is English only. The app is i18n-ready. Light and dark themes apply only to generated diagrams and their preview. The app shell stays light ([0035](decisions/front/0035-themes-only-on-generated-diagrams.md)).

## Pipeline

Guided intake → Pre-Profile JSON → Planner → DiagramSpec JSON → Renderer → HTML.

1. **Guided intake.** Vaadin chips plus a narrow profile-assistant chat. The chat only completes the profile. It does not design the diagram, write the spec, or write HTML. It asks for the topic, asks at most one clarifying question when the topic is vague, and echoes a profile summary.
2. **Pre-Profile JSON.** The backend validates it, fills defaults, and, when the diagram type is Auto, suggests a layout. Backend Pre-Profile validation is planned, not yet implemented (no story assigned yet). The auto-layout suggestion (explain-flow → horizontal steps, use-case → U-shape, and layers → pyramid) is planned, not yet implemented (no story assigned yet). Neither is part of v0.0.3, which is only the Theme chip, light/dark tokens and the preview card. The planner is not called without a topic.
3. **Planner.** The JSON planner produces a DiagramSpec: structured text and code only, no raw HTML.
4. **Renderer.** Deterministic templates are filled from the DiagramSpec by default. The user can opt to have an LLM render instead. That opt-in uses the diagrammer/renderer model role.
5. **Store.** That version's HTML is stored in the private bucket, and the app serves it at a public URL. See [Storage](#storage).

### Intake

Chips ([0033](decisions/product/0033-pre-profile-chip-values.md)):

- **domain:** `java` (default), `spring-boot`, `python`, `ml`, `react`
- **goal:** `explain-flow` (default), `use-case`, `compare`, `layers`, `other` (the user phrases it, and the assistant maps it when it can)
- **interactivity:** `static`, `step-by-step` (default), `multi-scenario`
- **type:** `auto` (default), `horizontal-steps`, `u-shape`, `pyramid`
- **depth:** `overview`, `with-code` (default), `with-code-parallels`
- **theme:** `light` (default), `dark`
- **audience:** `beginner` (default), `intermediate`
- **topic:** one sentence, required. This is the generate gate.
- **refinements:** optional notes from the chat
- **parentVersion:** set in Tinker mode so the new version keeps its lineage

The diagram title is separate from the topic. The user types the title.

`multi-scenario` is allowed only with `u-shape` or `auto`.

Goal `compare` forces type `auto` and disables `horizontal-steps`, `u-shape`, and `pyramid`.

`static` and `step-by-step` are allowed with every type.

Theme is allowed with every type and every interactivity. It has no combination rules. The app shell stays light. The selected palette styles the diagram preview now, and the generated page when the renderer exists. Both palettes are the custom properties in `diagrammer-code-project/src/main/resources/META-INF/resources/diagram-theme.css`.

The longer goal values `walk-through-use-case`, `compare-concepts`, and `show-layers` are superseded by `use-case`, `compare`, and `layers`.

Comparison is a v2 / future layout. It is not a type chip in v0.0.2.

When the type is Auto, the suggested layout is `explain-flow` → horizontal steps, `use-case` → U-shape, and `layers` → pyramid. Suggesting comparison for `compare` waits until that v2 layout exists. The auto-layout suggestion (explain-flow → horizontal steps, use-case → U-shape, and layers → pyramid) is planned, not yet implemented (no story assigned yet). It is not part of v0.0.3, which is only the Theme chip, light/dark tokens and the preview card.

Topic placeholders the chat can show:

- Java: "How adopt-dog travels from CLI to Repository"
- ML: "What happens in a train/test split"
- React: "Lifecycle of a component with useEffect"
- Spring Boot and Python: TBD

### DiagramSpec

The planner's JSON names the layout, the nodes and edges, and either steps or scenarios, plus content blocks such as facts, paragraphs, code, analogies, and framework parallels. The renderer escapes every value. Comparison is a v2 layout and is not a Pre-Profile type ([0017](decisions/product/0017-layouts-in-v1.md), [0033](decisions/product/0033-pre-profile-chip-values.md)).

## Layouts and templates

v1 layouts:

- Horizontal steps
- U-shape request/response
- Pyramid

Comparison is v2 / future. It is not a Pre-Profile type chip in v0.0.2 ([0033](decisions/product/0033-pre-profile-chip-values.md)). The catalog will grow.

Templates fix three quirks in the PersonCarDog reference: Next is enabled from the start, text is escaped, and the page has a back link.

Arrow and SVG geometry is TBD. The reference hand-places approximate SVG paths. Whether the renderer computes geometry or keeps hand-placed values is not chosen.

## Repository layout

- Application code lives in `diagrammer-code-project/`, next to `docs/`.
- That folder is the Maven/Hilla project root: `pom.xml` and `src/`.
- The frontend lives in `diagrammer-code-project/src/main/frontend/`, not at the project root.
- Docs stay in `docs/`.

## Stack

- One Vaadin Hilla project: a Spring Boot backend and a React frontend in the same project. A separate pure-React app is out of scope.
- The frontend talks to the backend through Hilla endpoints.
- The Diagram Orchestrator is that frontend. Intake uses Vaadin chips.
- Java 21 (Temurin), Maven 3.9.11 via the `./mvnw` wrapper, and Node 24 LTS.
- Spring Boot 4.1.1, Vaadin Hilla 25.3.1, and AWS SDK for Java v2 2.56.0. These versions were adopted in the v0.0.1 pull request [PR #22](https://github.com/AlbertProfe/diagrammer/pull/22).

## Run profiles

There is no default profile.

- With no profile, the app fails fast unless `DIAGRAMMER_S3_BUCKET` and `AWS_REGION` are set. When both are set, it uses the `s3` profile. Credentials still come from `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`. See [Secrets](#secrets).
- Local dev is `./mvnw spring-boot:run -Pdev` from `diagrammer-code-project/`. The Maven `dev` profile turns on the Spring `local` profile for that run. Sign in with the user in `diagrammer-code-project/src/main/resources/fixture/users.json`. It needs no AWS.

## Auth

- Amazon Cognito is not used for now. It may be considered later.
- Auth is Spring-style.
- Users are stored as `auth/users.json` in the S3 bucket. For v1 the file is plaintext and not encrypted. Albert confirmed that is intentional: early human testing, keep it simple.
- Password hashing (for example BCrypt) is a later backlog item, not part of v1.

## Storage

- AWS S3 stores the generated documents: the HTML, the Pre-Profile JSON, the DiagramSpec JSON, and similar files.
- One private bucket, `diagrammer-albertprofe-docs`, in `eu-central-1` (Frankfurt). Block Public Access is on. SSE-S3 is on. S3 object versioning is on.
- Key layout:
  - `auth/users.json`, the credentials file. See [Auth](#auth).
  - `users/{userId}/diagrams/{diagramId}/v{n}/...`, each diagram version.
  - `index/diagrams/{diagramId}.json`
- A diagram URL is public. Anyone with the link can view it. The bucket is private, so the app serves that URL. S3 does not.
- The URL shape is `/diagrams/{id}` plus a per-version URL. The exact form is TBD.

## AI model calls

- Model calls use Grok models through AWS Bedrock. This extends the earlier Bedrock decision. It does not add a separate Grok API key.
- Three roles sit behind an `AiClient` interface:
  - **Chat:** the profile assistant.
  - **JSON planner:** produces the DiagramSpec.
  - **Diagrammer/renderer:** used only when the user opts in to LLM rendering. The default path fills templates and does not call a model.
- AWS access uses the scoped IAM user `diagrammer-app`. It can read, write, list, and delete objects in `diagrammer-albertprofe-docs` only. The region is `eu-central-1`.
- Bedrock permissions are added once a model is chosen.
- That user's keys are secrets. See [Secrets](#secrets).
- Limits and cost are TBD: generation caps, diagram size, timeout, and cost ceiling are not chosen.

## Safety

- The DiagramSpec carries text only. The template renderer escapes it.
- Diagram responses are served with a Content-Security-Policy.
- LLM-rendered HTML is sanitized, and its preview is a sandboxed iframe.

## Deploy pipeline

- The host is Render ([0030](decisions/devops/0030-deploy-on-render.md)). The Render account is albertprofe. The service name is `diagrammer`.
- The public URL is https://diagrammer-wt9s.onrender.com.
- The service is on the free plan in Frankfurt (`plan: free`, `region: frankfurt` in [`render.yaml`](../render.yaml)), using Render's Docker runtime.
- Render's Source is the GitHub repo [AlbertProfe/diagrammer](https://github.com/AlbertProfe/diagrammer), branch `main`, connected through the Render GitHub app ([0037](decisions/devops/0037-render-deploys-via-the-github-app.md)). Before 2026-10-19 20:00 Europe/Madrid the service was linked by public repo URL, so it never received push events. Every deploy before then was manual or triggered by a settings change.
- PRs #30 and #31 went live through a manual deploy of `main` (commit `b7500e8`), started at 19:51 and live at 19:56 Madrid time on 2026-10-19.
- Around 20:00 that day Albert reconnected the Source through the Render GitHub app and re-added the build filter, because reconnecting had wiped it. Paths are `diagrammer-code-project/**`. Ignored paths are `diagrammer-code-project/**/*.md`. [`render.yaml`](../render.yaml) has the same `buildFilter`. Both `paths` and `ignoredPaths` are set, because a Blueprint sync replaces the whole filter ([0032](decisions/devops/0032-render-deploys-only-for-app-changes.md)).
- Those globs are relative to the repository root. `rootDir` is unset. The Docker build inputs stay included: `Dockerfile`, `pom.xml`, `mvnw`, `package.json`, `package-lock.json`, and `src/` (the frontend lives under `src/main/frontend/`).
- Push-triggered auto-deploy is expected (`autoDeploy: true`, `autoDeployTrigger: commit` in [`render.yaml`](../render.yaml)). It is not proven yet. The next code merge (v0.0.3) is the first test. Manual deploys are the fallback.
- The image is [`diagrammer-code-project/Dockerfile`](../diagrammer-code-project/Dockerfile). [`render.yaml`](../render.yaml) sets `dockerfilePath` to `./diagrammer-code-project/Dockerfile` and `dockerContext` to `./diagrammer-code-project`.
- The service runs with `SPRING_PROFILES_ACTIVE=s3`. The Dockerfile sets that default, and [`render.yaml`](../render.yaml) sets the same value. The decision 26 variables are listed in `render.yaml` with `sync: false`, so the values are set in Render and are never committed.
- [`diagrammer-code-project/docker/entrypoint.sh`](../diagrammer-code-project/docker/entrypoint.sh) refuses the `local` and `test` Spring profiles. The dev fixture login cannot start in this image.
- The health endpoint is `GET /actuator/health`. `render.yaml` sets `healthCheckPath` to `/actuator/health`. Albert set that path in the Render dashboard. The redeploy was verified 200 UP at 15:42 Europe/Madrid on 2026-10-18 ([0031](decisions/devops/0031-render-service-shape.md)).
- diag-devops owns the setup.

## Secrets

- The app reads `DIAGRAMMER_S3_BUCKET`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, and `AWS_SECRET_ACCESS_KEY` from the environment.
- `AWS_REGION` is `eu-central-1`. `DIAGRAMMER_S3_BUCKET` is `diagrammer-albertprofe-docs`.
- Values are never pasted in chat and never committed.
- On Render, these variables are set as Render environment variables. diag-devops owns that setup. See [Deploy pipeline](#deploy-pipeline).

## Release versioning

- App releases start at `v0.0.1`.
- Issues carry a `release/vX.Y.Z` label.
- `v0.0.1` is story S1 ([issue #3](https://github.com/AlbertProfe/diagrammer/issues/3), "Log in and see only my diagrams") plus a minimal Hilla skeleton.
- A release version is not a diagram content version. Diagram versions stay `v1` through `vN` inside a diagram.

## Repository and pull requests

- The git host is GitHub. The repository is public: [AlbertProfe/diagrammer](https://github.com/AlbertProfe/diagrammer). It is not on Cursor Origin.
- The default branch is `main`. Nothing goes straight to `main`.
- GitHub is the single source of truth. Render is connected through the GitHub app. A push to `main` that matches the build filter is the expected deploy, and that auto-deploy is unproven until v0.0.3. Manual deploys are the fallback. See [Deploy pipeline](#deploy-pipeline).
- Cloud agents clone from GitHub, work on a branch, and open pull requests on GitHub.
- diag-reviewer comments on those pull requests. Albert reviews and merges on GitHub.

## Open questions / TBD

Not decided. Do not treat any item here as a choice.

- **Arrow and SVG geometry.** Computed from the layout, or hand-placed like the reference. Not chosen.
- **Limits and cost.** Generation caps, diagram size, timeout, and cost ceiling. Not chosen.
- **Spring Boot and Python topic placeholders.** Java, ML, and React placeholders are set. These two are not.
- **Diagram deletion.** Versions are immutable. Whether a whole diagram can be deleted is not chosen.
- **Exact diagram URL form.** The shape is `/diagrams/{id}` plus a per-version URL. The exact path is not chosen.
