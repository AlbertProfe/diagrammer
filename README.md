# Diagrammer

Diagrammer generates self-contained interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) teaching pages. Guided intake (Vaadin chips and a narrow profile-assistant chat) produces a Pre-Profile, a planner produces a DiagramSpec, and a renderer produces the HTML. Each generate stores a new immutable version.

It is one Vaadin Hilla project: a Spring Boot backend and a React frontend talking through Hilla endpoints. Documents are stored in AWS S3. Model calls use Grok on AWS Bedrock. The app is deployed on Render.

[Architecture](docs/ARCHITECTURE.md) and the decision files under [docs/decisions/](docs/decisions/) are the shared source of truth. Every bot on the team (product owner, tech lead, coder, devops, reviewer, documenter, QA) reads the architecture and the decision log before working.

- [User guide](docs/user-guide/README.md): how to sign in and start a diagram (v0.0.2)
- [Architecture](docs/ARCHITECTURE.md): the intended design
- [Decisions](docs/DECISIONS.md): index of the decision log. Each decision is one file under [docs/decisions/](docs/decisions/).

## Application code

All application code lives in [diagrammer-code-project/](diagrammer-code-project/). The Maven/Hilla project root is that folder (`pom.xml`, `src/`, `mvnw`). Run, build, and test from inside it.

Step-by-step local-run instructions, fixture login, and which profile needs the real S3 bucket are in [diagrammer-code-project/README.md](diagrammer-code-project/README.md). Local dev opts into the fixture with `./mvnw spring-boot:run -Pdev` from that folder. A run with no profile does not load the fixture.
