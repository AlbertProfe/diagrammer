# Diagrammer

Diagrammer generates self-contained interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) teaching pages. Guided intake (Vaadin chips and a narrow profile-assistant chat) produces a Pre-Profile, a planner produces a DiagramSpec, and a renderer produces the HTML. Each generate stores a new immutable version.

It is one Vaadin Hilla project: a Spring Boot backend and a React frontend talking through Hilla endpoints. Documents are stored in AWS S3. Model calls use Grok on AWS Bedrock. The app is deployed on Railway.

Two docs are the shared source of truth. Every bot on the team (product owner, tech lead, coder, devops, reviewer, documenter, QA) reads them before working.

- [Architecture](docs/ARCHITECTURE.md): the intended design
- [Decisions](docs/DECISIONS.md): the decision log
