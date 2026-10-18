# Decisions

Append-only log of project decisions for Diagrammer. This file and [ARCHITECTURE.md](ARCHITECTURE.md) are the shared source of truth. Every bot reads both before working.

## How to add an entry

- Append a new entry at the end. Do not edit or delete an existing entry.
- Number it with the next integer. Include the date, a title, the decision, and a short rationale.
- If a decision changes, add a new entry that supersedes the old one. In the new entry, name the number it replaces. Leave the old entry as it was.
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
