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
- **Decision:** Users are stored as a JSON credentials file in a bucket folder. The file is not encrypted. This is Albert's call. The product owner suggested storing BCrypt password hashes instead: near-zero effort, and it avoids plaintext passwords if the bucket or a URL is exposed. That suggestion is raised with Albert and is still open.
- **Rationale:** #4 left the simple store unspecified, and #5 left a users file optional. The store is this JSON file. Whether the passwords stay plaintext is not settled.

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
