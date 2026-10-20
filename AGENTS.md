# Diagrammer

Diagrammer generates self-contained interactive HTML diagrams in the style of the PersonCarDog teaching pages. v0.0.2 is login plus the Diagram Orchestrator intake.

Before working, read [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md), [docs/DECISIONS.md](docs/DECISIONS.md), and [docs/CODEMAP.md](docs/CODEMAP.md).

The user guide is [docs/user-guide/](docs/user-guide/README.md).

Application code lives in `diagrammer-code-project/`. The React frontend is `diagrammer-code-project/src/main/frontend/`.

Run locally from `diagrammer-code-project/`:

```bash
./mvnw spring-boot:run -Pdev
```

The Maven `dev` profile turns on the Spring `local` profile for that run. `local` loads the fixture user store and needs no AWS. Sign in with the user in `diagrammer-code-project/src/main/resources/fixture/users.json`. Do not copy that file's password into docs, chat, or commits.

Branch from `main` and open a pull request on GitHub. diag-reviewer reviews it. Albert merges. Nothing goes straight to `main`.

New stories do not re-test login. S1's tests cover it, so story smoke tests and screenshots start already signed in.

If a PR changes code structure (packages, endpoints, views, config, tests), update [docs/CODEMAP.md](docs/CODEMAP.md) in the same PR, or flag it for a docs follow-up.

Never put secrets in the repo. Render push auto-deploy is expected and unproven until the v0.0.3 code merge; until then a manual deploy is the fallback.
