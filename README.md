# Diagrammer

Diagrammer is a Vaadin Hilla full-stack app for creating and storing diagrams. The Spring Boot backend and the React frontend live in one project and talk through Hilla endpoints. User-generated documents are stored in AWS S3. Model calls go through AWS Bedrock. The app is deployed on Railway.

Two docs are the shared source of truth. Every bot on the team (product owner, tech lead, coder, devops, reviewer, documenter, QA) reads them before working.

- [Architecture](docs/ARCHITECTURE.md): the intended design
- [Decisions](docs/DECISIONS.md): the decision log
