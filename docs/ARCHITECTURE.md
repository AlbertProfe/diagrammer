# Architecture

Intended architecture for Diagrammer. It records only decisions already made in [DECISIONS.md](DECISIONS.md). Anything not settled there is listed under [Open questions / TBD](#open-questions--tbd) and is not a decision.

This file is the current picture. When a later decision changes the design, update this file to match. The decision log stays append-only.

There is no knowledge-graph tool. This file and the decision log are the shared source of truth. Every bot (product owner, tech lead, coder, devops, reviewer, documenter, QA) reads both before working.

## Stack

- One Vaadin Hilla project: a Spring Boot backend and a React frontend in the same project. A separate pure-React app is out of scope.
- The frontend talks to the backend through Hilla endpoints.
- Java version, Hilla version, and Spring Boot version are TBD.

## Auth

- Amazon Cognito is not used for now. It may be considered later.
- Auth is Spring-style, backed by a simple store.
- The concrete user store is TBD.

## Storage

- AWS S3 stores user-generated documents, including HTML, JSON, and similar files.
- A users file may be added later. It is optional and is not part of the current design.
- Bucket layout (bucket count, prefixes, object names) is TBD.

## AI model calls

- Model calls go to AWS Bedrock.
- AWS access uses one scoped IAM user.
- That user's keys are secrets. See [Secrets](#secrets).
- Which Bedrock model to call is TBD.

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

- **Diagram format and editor library.** How a diagram is represented, which kinds of diagrams the app supports, and which editor the UI uses.
- **Auth user store.** Auth uses a simple store. The concrete store is not chosen.
- **S3 bucket layout.** Documents go to S3. Prefixes, object names, and whether there is one bucket or several are not chosen.
- **Bedrock model.** Calls go to Bedrock. The model is not chosen.
- **Java, Hilla, and Spring Boot versions.** None of these versions are chosen.
- **Secret store.** Keys are secrets. The store that holds them is not chosen.
- **Docker image and Railway service shape.** A push to `main` builds an image and publishes it. What the image contains, and how the Railway service is laid out, are not chosen.
- **Users file.** Optional and later. Format and location are not chosen.
