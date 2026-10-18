# Architecture

Intended architecture for Diagrammer. It records only decisions already made in [DECISIONS.md](DECISIONS.md). Anything not settled there is listed under [Open questions / TBD](#open-questions--tbd) and is not a decision.

This file is the current picture. When a later decision changes the design, update this file to match. The decision log stays append-only.

There is no knowledge-graph tool. This file and the decision log are the shared source of truth. Every bot (product owner, tech lead, coder, devops, reviewer, documenter, QA) reads both before working.

## Product

Diagrammer generates self-contained interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) teaching pages. Each page stands alone: inline CSS and JavaScript, no external dependencies.

One screen, the Diagram Orchestrator, has two modes:

- **Create.** No diagram yet. Guided intake, then generate version `v1`.
- **Tinker.** One or more versions exist. Adjust the intake and generate again. The latest version is active by default.

Every generate creates a new immutable version (`v1` through `vN`). A regenerate does not overwrite an earlier version. Whether a whole diagram can be deleted is TBD.

The user types the diagram title. Diagram output is English only. The app is i18n-ready. Themes are light and dark.

## Pipeline

Guided intake → Pre-Profile JSON → Planner → DiagramSpec JSON → Renderer → HTML.

1. **Guided intake.** Vaadin chips plus a narrow profile-assistant chat. The chat only completes the profile. It does not design the diagram, write the spec, or write HTML. It asks for the topic, asks at most one clarifying question when the topic is vague, and echoes a profile summary.
2. **Pre-Profile JSON.** The backend validates it, fills defaults, and, when the diagram type is Auto, suggests a layout. The planner is not called without a topic.
3. **Planner.** The JSON planner produces a DiagramSpec: structured text and code only, no raw HTML.
4. **Renderer.** Deterministic templates are filled from the DiagramSpec by default. The user can opt to have an LLM render instead. That opt-in uses the diagrammer/renderer model role.
5. **Store.** That version's HTML is stored and served at a public URL. See [Storage](#storage).

### Intake

Chips:

- **domain:** `java` (default), `spring-boot`, `python`, `ml`, `react`
- **goal:** `explain-flow` (default), `walk-through-use-case`, `compare-concepts`, `show-layers`, `other` (the user phrases it, and the assistant maps it when it can)
- **interactivity:** `static`, `step-by-step` (default), `multi-scenario`
- **diagram type:** `horizontal-steps`, `u-shape`, `pyramid`, `comparison`, `auto` (default)
- **depth:** `overview`, `with-code` (default), `with-code-parallels`
- **audience:** `beginner` (default), `intermediate` (optional)
- **topic:** one sentence, required. This is the generate gate.
- **refinements:** optional notes from the chat
- **parentVersion:** set in Tinker mode so the new version keeps its lineage

The diagram title is separate from the topic. The user types the title.

When the type is Auto, the suggested layout is explain-flow → horizontal steps, walk-through-use-case → U-shape, show-layers → pyramid, compare-concepts → comparison.

Topic placeholders the chat can show:

- Java: "How adopt-dog travels from CLI to Repository"
- ML: "What happens in a train/test split"
- React: "Lifecycle of a component with useEffect"
- Spring Boot and Python: TBD

### DiagramSpec

The planner's JSON names the layout, the nodes and edges, and either steps or scenarios, plus content blocks such as facts, paragraphs, code, analogies, and framework parallels. The renderer escapes every value. The comparison layout is v2, even if a profile names it.

## Layouts and templates

v1 layouts:

- Horizontal steps
- U-shape request/response
- Pyramid

Comparison is v2. The catalog will grow.

Templates fix three quirks in the PersonCarDog reference: Next is enabled from the start, text is escaped, and the page has a back link.

Arrow and SVG geometry is TBD. The reference hand-places approximate SVG paths. Whether the renderer computes geometry or keeps hand-placed values is not chosen.

## Stack

- One Vaadin Hilla project: a Spring Boot backend and a React frontend in the same project. A separate pure-React app is out of scope.
- The frontend talks to the backend through Hilla endpoints.
- The Diagram Orchestrator is that frontend. Intake uses Vaadin chips.
- Java version, Hilla version, and Spring Boot version are TBD.

## Auth

- Amazon Cognito is not used for now. It may be considered later.
- Auth is Spring-style.
- Users are stored as one JSON credentials file in a folder of the S3 bucket. For v1 the file is plaintext and not encrypted. Albert confirmed that is intentional: early human testing, keep it simple.
- Password hashing (for example BCrypt) is a later backlog item, not part of v1.

## Storage

- AWS S3 stores the generated documents: the HTML, the Pre-Profile JSON, the DiagramSpec JSON, and similar files.
- One bucket. A folder per user. Inside it, a subfolder per diagram holding all of that diagram's versions.
- The JSON credentials file lives in a folder of the same bucket. See [Auth](#auth).
- A diagram URL is public. Anyone with the link can view it.
- The URL shape is `/diagrams/{id}` plus a per-version URL. The exact form is TBD.

## AI model calls

- Model calls use Grok models through AWS Bedrock. This extends the earlier Bedrock decision. It does not add a separate Grok API key.
- Three roles sit behind an `AiClient` interface:
  - **Chat:** the profile assistant.
  - **JSON planner:** produces the DiagramSpec.
  - **Diagrammer/renderer:** used only when the user opts in to LLM rendering. The default path fills templates and does not call a model.
- AWS access uses one scoped IAM user.
- That user's keys are secrets. See [Secrets](#secrets).
- Limits and cost are TBD: generation caps, diagram size, timeout, and cost ceiling are not chosen.

## Safety

- The DiagramSpec carries text only. The template renderer escapes it.
- Diagram responses are served with a Content-Security-Policy.
- LLM-rendered HTML is sanitized, and its preview is a sandboxed iframe.

## Deploy pipeline

- The host is Railway. Render is not the host.
- GitHub is connected to Railway.
- A push to `main` builds a Docker image and publishes it on Railway.
- Railway is secondary for now. The v1 UI can be previewed from cloud agent screenshots or videos, or by running the app locally.
- Image contents and the Railway service layout, beyond the push-to-`main` build and publish above, are TBD.

## Secrets

- The scoped IAM user's keys are stored as secrets.
- Keys are never pasted in chat.
- Keys are never committed to the repository.
- Which secret store holds them is TBD.

## Repository and pull requests

- The git host is GitHub. The repository is public: [AlbertProfe/diagrammer](https://github.com/AlbertProfe/diagrammer).
- The default branch is `main`.
- A push to `main` is what Railway builds and publishes. See [Deploy pipeline](#deploy-pipeline).
- Cursor Origin is the codebase and pull-request layer the bots use. Bots use Origin to inspect the repository, diffs, and checks, and to open pull requests.
- The coder and the reviewer use Origin for pull requests.

## Open questions / TBD

Not decided. Do not treat any item here as a choice.

- **Arrow and SVG geometry.** Computed from the layout, or hand-placed like the reference. Not chosen.
- **Limits and cost.** Generation caps, diagram size, timeout, and cost ceiling. Not chosen.
- **Spring Boot and Python topic placeholders.** Java, ML, and React placeholders are set. These two are not.
- **Diagram deletion.** Versions are immutable. Whether a whole diagram can be deleted is not chosen.
- **Exact diagram URL form.** The shape is `/diagrams/{id}` plus a per-version URL. The exact path is not chosen.
- **Java, Hilla, and Spring Boot versions.** None of these versions are chosen.
- **Secret store.** Keys are secrets. The store that holds them is not chosen.
- **Docker image and Railway service shape.** A push to `main` builds an image and publishes it. What the image contains, and how the Railway service is laid out, are not chosen.
