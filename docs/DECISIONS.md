# Decisions

Append-only log of project decisions for Diagrammer. This file and [ARCHITECTURE.md](ARCHITECTURE.md) are the shared source of truth. Every bot reads both before working.

## How to add an entry

- Append a new entry at the end. Do not edit or delete an existing entry.
- Number it with the next integer. Include the date, a title, the decision, and a short rationale.
- If a decision changes, add a new entry that supersedes the old one. In the new entry, name the number it replaces. Leave the old entry as it was.
- If a new entry extends, confirms, or refines an earlier one, or closes an open question, say so in the new entry (`Extends #6`, `Confirms #5`, `Refines #4`, `Supersedes TBD: …`). Do not edit the old entry.
- After a new entry, update [ARCHITECTURE.md](ARCHITECTURE.md) so the current architecture matches the latest decision.

## 1. Hilla full-stack

- **Date:** 2026-10-18
- **Decision:** Diagrammer is one Vaadin Hilla project. The backend is Spring Boot. The frontend is React. The UI talks to the backend through Hilla endpoints. A separate pure-React app is out of scope.
- **Rationale:** Keep the backend and frontend in one project, with Hilla endpoints as the way they communicate.

## 2. Deploy on Railway

- **Date:** 2026-10-18
- **Decision:** Deploy on Railway. Render is not the host. GitHub is connected to Railway. A push to `main` builds a Docker image and publishes it on Railway. Railway is secondary for now. The v1 UI can be previewed from cloud agent screenshots or videos, or from a local run.
- **Rationale:** Railway is the chosen host, wired to GitHub so a push to `main` publishes a Docker image. Railway is secondary for now, so the v1 UI is previewed locally or from cloud agent screenshots and videos.

## 3. Cursor Origin for pull requests

- **Date:** 2026-10-18
- **Decision:** Cursor Origin is the codebase and pull-request layer the bots use to inspect repositories, diffs, and checks, and to open pull requests. GitHub is the git host. The coder and the reviewer use Origin for pull requests.
- **Rationale:** GitHub stores the git history. Origin is how the coder and reviewer inspect the repo and open pull requests.

## 4. Auth without Cognito

- **Date:** 2026-10-18
- **Decision:** Do not use Amazon Cognito for now. Use Spring-style auth with a simple store. Cognito may be considered later.
- **Rationale:** Start with Spring-style auth and a simple store. Cognito stays a possible later option. The concrete user store is not chosen yet (see architecture TBD).

## 5. S3 for user-generated documents

- **Date:** 2026-10-18
- **Decision:** Store user-generated documents (HTML, JSON, and similar) in AWS S3. A users file may be added later. That file is optional.
- **Rationale:** S3 is the store for documents the app creates. A users file is a later, optional addition, not part of the current design.

## 6. Bedrock for model calls

- **Date:** 2026-10-18
- **Decision:** Model calls go to AWS Bedrock. AWS access uses a scoped IAM user. That user's keys are stored as secrets. Keys are never pasted in chat and never committed.
- **Rationale:** Bedrock is the model API. A scoped IAM user is the AWS access path, and its keys stay in secrets so they never appear in chat or in the repository. The Bedrock model is not chosen yet (see architecture TBD).

## 7. Repo docs are the shared memory

- **Date:** 2026-10-18
- **Decision:** Do not use a knowledge-graph tool (such as Graphify) for now. The shared source of truth is [ARCHITECTURE.md](ARCHITECTURE.md) and this decision log. Every bot reads both before working.
- **Rationale:** Keep the shared picture in the repository. Bots read these two files before working, instead of a separate knowledge-graph tool.

## 8. Public GitHub repo on main

- **Date:** 2026-10-18
- **Decision:** The repository is the public GitHub repo [AlbertProfe/diagrammer](https://github.com/AlbertProfe/diagrammer). The default branch is `main`.
- **Rationale:** GitHub is the public git host, and `main` is the default branch that Railway builds from.

## 9. Generated HTML pipeline

- **Date:** 2026-10-18
- **Relation:** Supersedes TBD: diagram format/editor library
- **Decision:** Diagrammer generates self-contained interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) teaching pages. The pipeline is guided intake (Vaadin chips plus a narrow profile-assistant chat) → Pre-Profile JSON → Planner → DiagramSpec JSON → Renderer → HTML. One Diagram Orchestrator screen has Create and Tinker modes. Every generate creates a new immutable version (`v1` through `vN`). Mermaid and a diagram editor library are closed.
- **Rationale:** The product is a generated teaching page in the PersonCarDog style. That closes the open question of a diagram format and editor library.

## 10. Grok models on Bedrock

- **Date:** 2026-10-18
- **Relation:** Extends #6. Supersedes TBD: Bedrock model
- **Decision:** Model calls use Grok models through AWS Bedrock. Three roles sit behind an `AiClient` interface: chat (the profile assistant), JSON planner (produces the DiagramSpec), and diagrammer/renderer (the model used when the user opts in to LLM rendering, see #11). #6 still holds for access: a scoped IAM user, with keys stored as secrets, never pasted in chat and never committed. There is no separate Grok API key.
- **Rationale:** #6 chose Bedrock and left the model open. The model is Grok, in three roles, still called through Bedrock.

## 11. Templates by default, LLM render on opt-in

- **Date:** 2026-10-18
- **Decision:** The renderer fills deterministic templates from the DiagramSpec by default. The user can opt to have an LLM render instead. That opt-in uses the diagrammer/renderer role from #10.
- **Rationale:** Templates keep the house style on the common path. An LLM render is available only when the user asks for it.

## 12. One S3 bucket, folders per user and diagram

- **Date:** 2026-10-18
- **Relation:** Confirms #5. Supersedes TBD: S3 bucket layout
- **Decision:** Storage stays on AWS S3. One bucket. A folder per user, and inside it a subfolder per diagram holding all of that diagram's versions. The URL shape is `/diagrams/{id}` plus a per-version URL. The exact URL form is still TBD.
- **Rationale:** #5 chose S3 and left the layout open. This sets one bucket and the user/diagram folders. It does not settle the exact spelling of the version URL.

## 13. JSON credentials file

- **Date:** 2026-10-18
- **Relation:** Refines #4. Supersedes TBD: auth user store. Settles the optional users file in #5.
- **Decision:** Users are stored as a JSON credentials file in a bucket folder. The file is not encrypted. Albert has confirmed this is intentional for v1: early human testing, keep it simple. Password hashing (for example BCrypt) is a later backlog item, not part of v1.
- **Rationale:** #4 left the simple store unspecified, and #5 left a users file optional. For v1 the store is this plaintext JSON file. Password hashing waits on the backlog.

## 14. Public diagram URLs

- **Date:** 2026-10-18
- **Decision:** Diagram URLs are public. Anyone with the link can view the diagram.
- **Rationale:** Viewing a diagram requires the link only. It does not require a login.

## 15. Light and dark themes

- **Date:** 2026-10-18
- **Decision:** Diagrams support a light theme and a dark theme.
- **Rationale:** Both themes are in scope. The PersonCarDog reference is mostly light, with one dark page.

## 16. Safe diagram output

- **Date:** 2026-10-18
- **Decision:** The DiagramSpec carries text only. The renderer escapes that text. Diagram responses are served with a Content-Security-Policy. When the user opts in to LLM rendering (#11), that HTML is sanitized and the preview is a sandboxed iframe.
- **Rationale:** Generated pages are HTML served to whoever has the link. Escaping, a CSP, and a sandbox on LLM HTML are the safety bar.

## 17. Layouts in v1

- **Date:** 2026-10-18
- **Decision:** v1 layouts are horizontal steps, U-shape request/response, and pyramid. Comparison is v2. The catalog will grow.
- **Rationale:** v1 covers the three interactive layouts in the PersonCarDog reference. Comparison waits until v2.

## 18. Templates fix reference quirks

- **Date:** 2026-10-18
- **Decision:** Templates fix three quirks in the PersonCarDog reference. Next is enabled from the start. Text is escaped. The page has a back link.
- **Rationale:** In the reference, Next stays disabled until autoplay starts, some text shows a literal escape sequence, and the diagram pages have no way back.

## 19. English output, i18n-ready

- **Date:** 2026-10-18
- **Decision:** Diagram output is English only. The app is i18n-ready.
- **Rationale:** v1 reads in English. The app should be able to take another language later without a redesign.

## 20. User-typed diagram title

- **Date:** 2026-10-18
- **Decision:** The user types the diagram title.
- **Rationale:** The title is user input. The planner does not invent it.

## 21. GitHub is the source of truth

- **Date:** 2026-10-18
- **Relation:** Clarifies decision 3
- **Decision:** The repo is hosted on GitHub. It is not on Cursor Origin. Cloud agents clone from GitHub, work on a branch, and open pull requests on GitHub. diag-reviewer comments on those pull requests. Albert reviews and merges on GitHub. Nothing goes straight to `main`. GitHub is the single source of truth. Railway also builds from it (decisions 2 and 8).
- **Rationale:** Decision 3 named Origin as the pull-request layer. The working flow is GitHub for the clone, the branch, the pull request, the review, and the merge.

## 22. Release versioning starts at v0.0.1

- **Date:** 2026-10-18
- **Decision:** App release versioning starts at `v0.0.1`. Releases are tracked with `release/vX.Y.Z` labels on issues. `v0.0.1` is story S1 ([issue #3](https://github.com/AlbertProfe/diagrammer/issues/3), "Log in and see only my diagrams") plus a minimal Hilla skeleton. A release version is separate from a diagram's content versions (`v1` through `vN`) in decision 9.
- **Rationale:** The first release is the Hilla skeleton and S1. Issue labels are how a release is tracked, and they are not the diagram version numbers.

## 23. AWS region eu-central-1

- **Date:** 2026-10-18
- **Decision:** AWS resources for Diagrammer use the region `eu-central-1` (Frankfurt).
- **Rationale:** The bucket and the rest of the AWS setup share one region.

## 24. Private bucket and key layout

- **Date:** 2026-10-18
- **Relation:** Refines decisions 5 and 12. Names the credentials file from decision 13. Clarifies decision 14.
- **Decision:** One private bucket, `diagrammer-albertprofe-docs`. Block Public Access is on. SSE-S3 is on. S3 object versioning is on. Key layout: `auth/users.json`, `users/{userId}/diagrams/{diagramId}/v{n}/...`, and `index/diagrams/{diagramId}.json`. Because the bucket is private, the public diagram URLs from decision 14 are served through the app, not directly from S3.
- **Rationale:** Decisions 5 and 12 chose S3 and a per-user, per-diagram layout. This names the bucket, locks it down, and sets the key prefixes. Decision 13's credentials file is `auth/users.json`. Decision 14 still makes the diagram URL public. The app reads the private object and serves that URL.

## 25. IAM user diagrammer-app

- **Date:** 2026-10-18
- **Relation:** Refines decision 6
- **Decision:** AWS access uses the scoped IAM user `diagrammer-app`. That user can read, write, list, and delete objects in `diagrammer-albertprofe-docs` only. Bedrock permissions are added once a model is chosen. Keys stay secrets (decisions 6 and 10): never pasted in chat, never committed.
- **Rationale:** Decision 6 required a scoped IAM user and left the scope unnamed. The scope is this bucket. Bedrock access waits until a model is chosen.

## 26. Configuration by environment variables

- **Date:** 2026-10-18
- **Relation:** Clarifies decision 6
- **Decision:** The app reads `DIAGRAMMER_S3_BUCKET`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, and `AWS_SECRET_ACCESS_KEY` from the environment. Values are never committed.
- **Rationale:** Decision 6 keeps keys out of the repo. These names are how the app receives the bucket, the region, and the IAM keys. Where the values are stored outside the process is still open.

## 27. Java 21, Maven 3.9, Node 24

- **Date:** 2026-10-18
- **Relation:** Supersedes TBD: Java version
- **Decision:** The dev toolchain is Java 21 (Temurin), Maven 3.9.x, and Node 24 LTS. Hilla and Spring Boot versions are still open.
- **Rationale:** This closes the Java version question. Hilla and Spring Boot versions are not chosen yet.

## 28. Application code lives in diagrammer-code-project

- **Date:** 2026-10-18
- **Decision:** All application code lives in the top-level folder `diagrammer-code-project/`. That folder is the Maven/Hilla project root (`pom.xml` and `src/`). The frontend lives in `diagrammer-code-project/src/main/frontend/`, not at the project root. The folder sits next to `docs/`. Docs stay in `docs/`. (corrected 2026-10-18)
- **Rationale:** The app and the source-of-truth docs share one repository, in separate top-level folders.

## 29. Framework and library versions

- **Date:** 2026-10-18
- **Relation:** Refines decision 27. Supersedes TBD: Hilla and Spring Boot versions
- **Decision:** Java 21, Maven 3.9.11 via the `./mvnw` wrapper, Node 24 LTS, Spring Boot 4.1.1, Vaadin Hilla 25.3.1, and AWS SDK for Java v2 2.56.0. Java 21 stays on Temurin (decision 27). These versions were adopted in the v0.0.1 pull request [PR #22](https://github.com/AlbertProfe/diagrammer/pull/22). There is no default profile. With no profile, the app fails fast unless `DIAGRAMMER_S3_BUCKET` and `AWS_REGION` are set, in which case it uses `s3` (credentials still come from decision 26). Local dev is `./mvnw spring-boot:run -Pdev` from `diagrammer-code-project/`. That Maven profile turns on the Spring `local` profile, uses the fixture `users.json` (username `albert`, password `diagrammer`), and needs no AWS. (corrected 2026-10-18)
- **Rationale:** Decision 27 left Maven on the 3.9 line and left Hilla and Spring Boot open. This pins the versions used for v0.0.1. The run profiles match the merged app: no default, fail fast without the S3 environment variables, and `-Pdev` for the fixture users.
